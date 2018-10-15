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
import com.alibaba.druid.sql.ast.expr.SQLPropertyExpr;
import com.alibaba.druid.sql.ast.expr.SQLVariantRefExpr;
import com.alibaba.druid.sql.ast.statement.SQLExprTableSource;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleSelectTableReference;
import com.alibaba.druid.sql.dialect.oracle.parser.OracleStatementParser;
import com.alibaba.druid.sql.dialect.oracle.visitor.OracleASTVisitorAdapter;
import com.alibaba.druid.sql.parser.SQLStatementParser;

public class Demo1 extends TestCase {

    public void test_0() throws Exception {
        String sql = "select * from user where uid = ? and uname = ?";
        List<Object> parameters = new ArrayList<Object>();
        parameters.add(1);
        parameters.add("wenshao");

        SQLStatementParser parser = new OracleStatementParser(sql);
        List<SQLStatement> stmtList = parser.parseStatementList(); //

        SQLStatement first = (SQLStatement) stmtList.get(0);

        GetVariantVisitor variantVisitor = new GetVariantVisitor();
        first.accept(variantVisitor);

        SQLVariantRefExpr firstVar = variantVisitor.getVariantList().get(0);
        
        int varIndex = (Integer) firstVar.getAttribute("varIndex");
        Integer param = (Integer) parameters.get(varIndex);
        
        String tableName;
        if (param.intValue() == 1) {
            tableName = "user_1";
        } else {
            tableName = "user_x";
        }

        MyOracleVisitor visitor = new MyOracleVisitor(tableName);
        first.accept(visitor);
        
        String realSql = SQLUtils.toOracleString(first);
        System.out.println(realSql);
    }

    private static class GetVariantVisitor extends OracleASTVisitorAdapter {

        private int                     varIndex    = 0;
        private List<SQLVariantRefExpr> variantList = new ArrayList<SQLVariantRefExpr>();

        public boolean visit(SQLVariantRefExpr x) {
            x.getAttributes().put("varIndex", varIndex++);
            return true;
        }

        public boolean visit(SQLBinaryOpExpr x) {
            if (x.getLeft() instanceof SQLIdentifierExpr && x.getRight() instanceof SQLVariantRefExpr) {
                SQLIdentifierExpr identExpr = (SQLIdentifierExpr) x.getLeft();
                String ident = identExpr.getName();
                if (ident.equals("uid")) {
                    variantList.add((SQLVariantRefExpr) x.getRight());
                }
            }

            return true;
        }

        public int getVarIndex() {
            return varIndex;
        }

        public void setVarIndex(int varIndex) {
            this.varIndex = varIndex;
        }

        public List<SQLVariantRefExpr> getVariantList() {
            return variantList;
        }

        public void setVariantList(List<SQLVariantRefExpr> variantList) {
            this.variantList = variantList;
        }

    }

    private static class MyOracleVisitor extends OracleASTVisitorAdapter {

        private String tableName;

        public MyOracleVisitor(String tableName){
            this.tableName = tableName;
        }
        
        public boolean visit(OracleSelectTableReference x) {
            SQLExpr expr = x.getExpr();
            if (expr instanceof SQLIdentifierExpr) {
                SQLIdentifierExpr identExpr = (SQLIdentifierExpr) expr;
                String tableName = identExpr.getName();
                
                if (tableName.equals("user")) {
                    x.setExpr(this.tableName);
                }
            } else if (expr instanceof SQLPropertyExpr) {
                SQLPropertyExpr proExpr = (SQLPropertyExpr) expr;
                String tableName = proExpr.getName();
                
                if (tableName.equals("user")) {
                    proExpr.setName(this.tableName);
                }
            }

            return true;
        }

        public boolean visit(SQLExprTableSource x) {
            SQLExpr expr = x.getExpr();
            if (expr instanceof SQLIdentifierExpr) {
                SQLIdentifierExpr identExpr = (SQLIdentifierExpr) expr;
                String tableName = identExpr.getName();
                
                if (tableName.equals("user")) {
                    x.setExpr(this.tableName);
                }
            } else if (expr instanceof SQLPropertyExpr) {
                SQLPropertyExpr proExpr = (SQLPropertyExpr) expr;
                String tableName = proExpr.getName();
                
                if (tableName.equals("user")) {
                    proExpr.setName(this.tableName);
                }
            }

            return true;
        }
    }

}
