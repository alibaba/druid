/*
 * Copyright 1999-2017 Alibaba Group Holding Ltd.
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

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLObject;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.expr.SQLIdentifierExpr;
import com.alibaba.druid.sql.ast.statement.*;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleSelectSubqueryTableSource;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleSelectTableReference;
import com.alibaba.druid.sql.dialect.oracle.visitor.OracleASTVisitorAdapter;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author wenshao[szujobs@hotmail.com]
 */
public class FromSubqueryResolver extends OracleASTVisitorAdapter {
    private final List<SQLStatement> targetList;
    private final String viewName;
    private final Map<String, String> mappings = new LinkedHashMap<String, String>();

    private int viewNameSeed = 1;

    public FromSubqueryResolver(List<SQLStatement> targetList, String viewName) {
        this.targetList = targetList;
        this.viewName = viewName;
    }

    public boolean visit(OracleSelectSubqueryTableSource x) {
        return visit((SQLSubqueryTableSource) x);
    }

    public boolean visit(SQLSubqueryTableSource x) {
        String subViewName = generateSubViewName();

        SQLObject parent = x.getParent();
        if(parent instanceof SQLSelectQueryBlock) {
            SQLSelectQueryBlock queryBlock = (SQLSelectQueryBlock) parent;
            queryBlock.setFrom(subViewName, x.getAlias());
        } else if(parent instanceof SQLJoinTableSource) {
            SQLJoinTableSource join = (SQLJoinTableSource) parent;
            if (join.getLeft() == x) {
                join.setLeft(subViewName, x.getAlias());
            } else if (join.getRight() == x) {
                join.setRight(subViewName, x.getAlias());
            }
        }

        SQLCreateViewStatement stmt = new SQLCreateViewStatement();

        stmt.setName(generateSubViewName());

        SQLSelect select = x.getSelect();
        stmt.setSubQuery(select);

        targetList.add(0, stmt);

        stmt.accept(new FromSubqueryResolver(targetList, viewName));

        return false;
    }

    public boolean visit(SQLExprTableSource x) {
        SQLExpr expr = x.getExpr();
        if (expr instanceof SQLIdentifierExpr) {
            SQLIdentifierExpr identifierExpr = (SQLIdentifierExpr) expr;
            String ident = identifierExpr.getName();
            String mappingIdent = mappings.get(ident);
            if (mappingIdent != null) {
                x.setExpr(new SQLIdentifierExpr(mappingIdent));
            }
        }
        return false;
    }

    public boolean visit(OracleSelectTableReference x) {
        return visit((SQLExprTableSource) x);
    }

    private String generateSubViewName() {
        return this.viewName + "_" + targetList.size();
    }

    public static List<SQLStatement> resolve(SQLCreateViewStatement stmt) {
        List<SQLStatement> targetList = new ArrayList<SQLStatement>();
        targetList.add(stmt);

        String viewName = SQLUtils.normalize(stmt.getName().getSimpleName());

        FromSubqueryResolver visitor = new FromSubqueryResolver(targetList, viewName);

        SQLWithSubqueryClause withSubqueryClause = stmt.getSubQuery().getWithSubQuery();
        if (withSubqueryClause != null) {
            stmt.getSubQuery().setWithSubQuery(null);

            for (SQLWithSubqueryClause.Entry entry : withSubqueryClause.getEntries()) {
                String entryName = entry.getAlias();

                SQLCreateViewStatement entryStmt = new SQLCreateViewStatement();
                entryStmt.setOrReplace(true);
                entryStmt.setDbType(stmt.getDbType());

                String entryViewName = visitor.generateSubViewName();
                entryStmt.setName(entryViewName);
                entryStmt.setSubQuery(entry.getSubQuery());

                visitor.targetList.add(0, entryStmt);
                visitor.mappings.put(entryName, entryViewName);

                entryStmt.accept(visitor);
            }
        }

        stmt.accept(visitor);

        DbType dbType = stmt.getDbType();
        for (int i = 0; i < targetList.size() - 1; ++i) {
            SQLCreateViewStatement targetStmt = (SQLCreateViewStatement) targetList.get(i);
            targetStmt.setOrReplace(true);
            targetStmt.setDbType(dbType);
            targetStmt.setAfterSemi(true);
        }

        return targetList;
    }
}
