/*
 * Copyright 1999-2018 Alibaba Group Holding Ltd.
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
package com.alibaba.druid.sql.dialect.oracle.visitor;

import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.expr.SQLBinaryOpExpr;
import com.alibaba.druid.sql.ast.expr.SQLBinaryOperator;
import com.alibaba.druid.sql.ast.expr.SQLIdentifierExpr;
import com.alibaba.druid.sql.ast.expr.SQLIntegerExpr;
import com.alibaba.druid.sql.ast.statement.SQLSelect;
import com.alibaba.druid.sql.ast.statement.SQLSelectItem;
import com.alibaba.druid.sql.ast.statement.SQLSelectQueryBlock;
import com.alibaba.druid.sql.ast.statement.SQLSelectStatement;
import com.alibaba.druid.sql.ast.statement.SQLSubqueryTableSource;
import com.alibaba.druid.sql.ast.statement.SQLTableSource;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleSelectQueryBlock;
import com.alibaba.druid.util.FnvHash;

public class OracleToMySqlOutputVisitor extends OracleOutputVisitor {

    public OracleToMySqlOutputVisitor(Appendable appender, boolean printPostSemi){
        super(appender, printPostSemi);
    }

    public OracleToMySqlOutputVisitor(Appendable appender){
        super(appender);
    }

    public boolean visit(OracleSelectQueryBlock x) {
        boolean parentIsSelectStatment = false;
        {
            if (x.getParent() instanceof SQLSelect) {
                SQLSelect select = (SQLSelect) x.getParent();
                if (select.getParent() instanceof SQLSelectStatement || select.getParent() instanceof  SQLSubqueryTableSource) {
                    parentIsSelectStatment = true;
                }
            }
        }

        if (!parentIsSelectStatment) {
            return super.visit(x);
        }

        if (x.getWhere() instanceof SQLBinaryOpExpr //
            && x.getFrom() instanceof SQLSubqueryTableSource //
        ) {
            int rownum;
            String ident;
            SQLBinaryOpExpr where = (SQLBinaryOpExpr) x.getWhere();
            if (where.getRight() instanceof SQLIntegerExpr && where.getLeft() instanceof SQLIdentifierExpr) {
                rownum = ((SQLIntegerExpr) where.getRight()).getNumber().intValue();
                ident = ((SQLIdentifierExpr) where.getLeft()).getName();
            } else {
                return super.visit(x);
            }

            SQLSelect select = ((SQLSubqueryTableSource) x.getFrom()).getSelect();
            SQLSelectQueryBlock queryBlock = null;
            SQLSelect subSelect = null;
            SQLBinaryOpExpr subWhere = null;
            boolean isSubQueryRowNumMapping = false;

            if (select.getQuery() instanceof SQLSelectQueryBlock) {
                queryBlock = (SQLSelectQueryBlock) select.getQuery();
                if (queryBlock.getWhere() instanceof SQLBinaryOpExpr) {
                    subWhere = (SQLBinaryOpExpr) queryBlock.getWhere();
                }

                for (SQLSelectItem selectItem : queryBlock.getSelectList()) {
                    if (isRowNumber(selectItem.getExpr())) {
                        if (where.getLeft() instanceof SQLIdentifierExpr
                            && ((SQLIdentifierExpr) where.getLeft()).getName().equals(selectItem.getAlias())) {
                            isSubQueryRowNumMapping = true;
                        }
                    }
                }

                SQLTableSource subTableSource = queryBlock.getFrom();
                if (subTableSource instanceof SQLSubqueryTableSource) {
                    subSelect = ((SQLSubqueryTableSource) subTableSource).getSelect();
                }
            }

            if ("ROWNUM".equalsIgnoreCase(ident)) {
                SQLBinaryOperator op = where.getOperator();
                Integer limit = null;
                if (op == SQLBinaryOperator.LessThanOrEqual) {
                    limit = rownum;
                } else if (op == SQLBinaryOperator.LessThan) {
                    limit = rownum - 1;
                }

                if (limit != null) {
                    select.accept(this);
                    println();
                    print0(ucase ? "LIMIT " : "limit ");
                    print(limit);
                    return false;
                }
            } else if (isSubQueryRowNumMapping) {
                SQLBinaryOperator op = where.getOperator();
                SQLBinaryOperator subOp = subWhere.getOperator();

                if (isRowNumber(subWhere.getLeft()) //
                    && subWhere.getRight() instanceof SQLIntegerExpr) {

                    int subRownum = ((SQLIntegerExpr) subWhere.getRight()).getNumber().intValue();

                    Integer offset = null;
                    if (op == SQLBinaryOperator.GreaterThanOrEqual) {
                        offset = rownum + 1;
                    } else if (op == SQLBinaryOperator.GreaterThan) {
                        offset = rownum;
                    }

                    if (offset != null) {
                        Integer limit = null;
                        if (subOp == SQLBinaryOperator.LessThanOrEqual) {
                            limit = subRownum - offset;
                        } else if (subOp == SQLBinaryOperator.LessThan) {
                            limit = subRownum - 1 - offset;
                        }

                        if (limit != null) {
                            subSelect.accept(this);
                            println();
                            print0(ucase ? "LIMIT " : "limit ");
                            print(offset);
                            print0(", ");
                            print(limit);
                            return false;
                        }
                    }
                }
            }
        }
        return super.visit(x);
    }

    static boolean isRowNumber(SQLExpr expr) {
        if (expr instanceof SQLIdentifierExpr) {
            return ((SQLIdentifierExpr) expr)
                    .hashCode64() == FnvHash.Constants.ROWNUM;
        }

        return false;
    }
}
