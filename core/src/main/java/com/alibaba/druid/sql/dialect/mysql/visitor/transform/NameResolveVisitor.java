/*
 * Copyright 1999-2023 Alibaba Group Holding Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.alibaba.druid.sql.dialect.mysql.visitor.transform;

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLName;
import com.alibaba.druid.sql.ast.SQLObject;
import com.alibaba.druid.sql.ast.expr.*;
import com.alibaba.druid.sql.ast.statement.*;
import com.alibaba.druid.sql.dialect.oracle.visitor.OracleASTVisitorAdapter;
import com.alibaba.druid.util.FnvHash;

/**
 * Created by wenshao on 26/07/2017.
 */
public class NameResolveVisitor extends OracleASTVisitorAdapter {
    @Override
    public boolean visit(SQLIdentifierExpr x) {
        SQLObject parent = x.getParent();

        if (parent instanceof SQLBinaryOpExpr
                && x.getResolvedColumn() == null) {
            SQLBinaryOpExpr binaryOpExpr = (SQLBinaryOpExpr) parent;
            boolean isJoinCondition = binaryOpExpr.getLeft() instanceof SQLName
                    && binaryOpExpr.getRight() instanceof SQLName;
            if (isJoinCondition) {
                return false;
            }
        }

        String name = x.getName();

        if ("ROWNUM".equalsIgnoreCase(name)) {
            return false;
        }

        long hash = x.nameHashCode64();
        SQLTableSource tableSource = null;

        if (hash == FnvHash.Constants.LEVEL
                || hash == FnvHash.Constants.CONNECT_BY_ISCYCLE
                || hash == FnvHash.Constants.SYSTIMESTAMP) {
            return false;
        }

        if (parent instanceof SQLPropertyExpr) {
            return false;
        }

        for (; parent != null; parent = parent.getParent()) {
            if (parent instanceof SQLTableSource) {
                return false;
            }

            if (parent instanceof SQLSelectQueryBlock) {
                SQLSelectQueryBlock queryBlock = (SQLSelectQueryBlock) parent;

                if (queryBlock.getInto() != null) {
                    return false;
                }

                if (queryBlock.getParent() instanceof SQLSelect) {
                    SQLObject pp = queryBlock.getParent().getParent();
                    if (pp instanceof SQLInSubQueryExpr || pp instanceof SQLExistsExpr) {
                        return false;
                    }
                }

                SQLTableSource from = queryBlock.getFrom();
                if (from instanceof SQLExprTableSource || from instanceof SQLSubqueryTableSource) {
                    String alias = from.getAlias();
                    if (alias != null) {
                        // rownum 不加 alias
                        boolean isRowNumColumn = isRowNumColumn(x, queryBlock);
                        // 别名 不加 alias
                        boolean isAliasColumn = isAliasColumn(x, queryBlock);
                        if (!isRowNumColumn && !isAliasColumn) {
                            SQLUtils.replaceInParent(x, new SQLPropertyExpr(alias, name));
                        }
                    }
                }
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean visit(SQLPropertyExpr x) {
        String ownerName = x.getOwnernName();
        if (ownerName == null) {
            return super.visit(x);
        }

        for (SQLObject parent = x.getParent(); parent != null; parent = parent.getParent()) {
            if (parent instanceof SQLSelectQueryBlock) {
                SQLSelectQueryBlock queryBlock = (SQLSelectQueryBlock) parent;
                SQLTableSource tableSource = queryBlock.findTableSource(ownerName);
                if (tableSource == null) {
                    continue;
                }

                String alias = tableSource.computeAlias();
                if (ownerName.equalsIgnoreCase(alias)
                        && !ownerName.equals(alias)) {
                    x.setOwner(alias);
                }

                break;
            }
        }

        return super.visit(x);
    }

    /**
     * 是否是 rownum 或者 rownum 别名
     *
     * @param x      x 是否是 rownum 或者 rownum 别名
     * @param source 从 source 数据中查找 and 判断
     * @return true：是、false：不是
     */
    public boolean isRowNumColumn(SQLExpr x, SQLSelectQueryBlock source) {
        if (x instanceof SQLIdentifierExpr) {
            SQLIdentifierExpr identifierExpr = (SQLIdentifierExpr) x;
            long nameHashCode64 = identifierExpr.nameHashCode64();
            if (nameHashCode64 == FnvHash.Constants.ROWNUM) {
                return true;
            }

            SQLSelectQueryBlock queryBlock = source;
            if (queryBlock.getFrom() instanceof SQLSubqueryTableSource
                    && ((SQLSubqueryTableSource) queryBlock.getFrom()).getSelect().getQuery() instanceof SQLSelectQueryBlock) {
                SQLSelectQueryBlock subQueryBlock = ((SQLSubqueryTableSource) queryBlock.getFrom()).getSelect().getQueryBlock();
                SQLSelectItem selectItem = subQueryBlock.findSelectItem(nameHashCode64);

                if (selectItem != null && isRowNumColumn(selectItem.getExpr(), subQueryBlock)) {
                    return true;
                }

            }

        }
        return false;
    }

    /**
     * 是否是 select item 字段的别名
     *
     * @param x      x 是否是 select item 字段的别名
     * @param source 从 source 数据中查找 and 判断
     * @return true：是、false：不是
     */
    public boolean isAliasColumn(SQLExpr x, SQLSelectQueryBlock source) {
        if (x instanceof SQLIdentifierExpr) {
            SQLIdentifierExpr identifierExpr = (SQLIdentifierExpr) x;
            long nameHashCode64 = identifierExpr.nameHashCode64();
            SQLSelectQueryBlock queryBlock = source;

            SQLSelectItem selectItem = queryBlock.findSelectItem(nameHashCode64);
            if (selectItem != null) {
                return true;
            }

            if (queryBlock.getFrom() instanceof SQLSubqueryTableSource
                    && ((SQLSubqueryTableSource) queryBlock.getFrom()).getSelect().getQuery() instanceof SQLSelectQueryBlock) {
                SQLSelectQueryBlock subQueryBlock = ((SQLSubqueryTableSource) queryBlock.getFrom()).getSelect().getQueryBlock();

                if (isAliasColumn(x, subQueryBlock)) {
                    return true;
                }

            }

        }
        return false;
    }

}
