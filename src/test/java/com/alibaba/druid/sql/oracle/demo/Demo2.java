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
package com.alibaba.druid.sql.oracle.demo;

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.expr.SQLBinaryOpExpr;
import com.alibaba.druid.sql.ast.expr.SQLIdentifierExpr;
import com.alibaba.druid.sql.ast.expr.SQLNumericLiteralExpr;
import com.alibaba.druid.sql.ast.expr.SQLPropertyExpr;
import com.alibaba.druid.sql.ast.expr.SQLVariantRefExpr;
import com.alibaba.druid.sql.ast.statement.SQLExprTableSource;
import com.alibaba.druid.sql.dialect.mysql.parser.MySqlStatementParser;
import com.alibaba.druid.sql.dialect.mysql.visitor.MySqlASTVisitorAdapter;
import com.alibaba.druid.sql.parser.SQLStatementParser;

public class Demo2 extends TestCase {

    public void test_0() throws Exception {
        String sql = "select * from user where uid = 2 and uname = ?";
        List<Object> parameters = new ArrayList<Object>();
        parameters.add(1);
        parameters.add("wenshao");

        SQLStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> stmtList = parser.parseStatementList(); //

        SQLStatement first = (SQLStatement) stmtList.get(0);

        MyVisitor visitor = new MyVisitor();
        first.accept(visitor);


        SQLExpr firstVar = visitor.getVariantList().get(0);

        int userId;
        if (firstVar instanceof SQLVariantRefExpr) {
            int varIndex = (Integer) firstVar.getAttribute("varIndex");
            userId = (Integer) parameters.get(varIndex);
        } else {
            userId = ((SQLNumericLiteralExpr) firstVar).getNumber().intValue();
        }

        String tableName;
        if (userId == 1) {
            tableName = "user_1";
        } else {
            tableName = "user_x";
        }

        for (SQLExprTableSource tableSource : visitor.getTableSourceList()) {
            SQLExpr expr = tableSource.getExpr();
            if (expr instanceof SQLIdentifierExpr) {
                SQLIdentifierExpr identExpr = (SQLIdentifierExpr) expr;
                String ident = identExpr.getName();

                if (ident.equals("user")) {
                    tableSource.setExpr(tableName);
                }
            } else if (expr instanceof SQLPropertyExpr) {
                SQLPropertyExpr proExpr = (SQLPropertyExpr) expr;
                String ident = proExpr.getName();

                if (ident.equals("user")) {
                    proExpr.setName(tableName);
                }
            }
        }

        String realSql = SQLUtils.toOracleString(first);
        System.out.println(realSql);
    }

    private static class MyVisitor extends MySqlASTVisitorAdapter {

        private int                      varIndex        = 0;
        private List<SQLExpr>  variantList     = new ArrayList<SQLExpr>();
        private List<SQLExprTableSource> tableSourceList = new ArrayList<SQLExprTableSource>();

        public boolean visit(SQLVariantRefExpr x) {
            x.getAttributes().put("varIndex", varIndex++);
            return true;
        }

        public boolean visit(SQLBinaryOpExpr x) {
            if (x.getLeft() instanceof SQLIdentifierExpr) {
                if (x.getRight() instanceof SQLVariantRefExpr) {
                    SQLIdentifierExpr identExpr = (SQLIdentifierExpr) x.getLeft();
                    String ident = identExpr.getName();
                    if (ident.equals("uid")) {
                        variantList.add(x.getRight());
                    }
                } else if (x.getRight() instanceof SQLNumericLiteralExpr) {
                    variantList.add(x.getRight());
                }
            }

            return true;
        }
        
        public boolean visit(SQLExprTableSource x) {
            tableSourceList.add(x);
            return true;
        }

        public int getVarIndex() {
            return varIndex;
        }

        public void setVarIndex(int varIndex) {
            this.varIndex = varIndex;
        }

        public List<SQLExpr> getVariantList() {
            return variantList;
        }

        public List<SQLExprTableSource> getTableSourceList() {
            return tableSourceList;
        }

    }


}
