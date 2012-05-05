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
import com.alibaba.druid.sql.dialect.mysql.parser.MySqlStatementParser;
import com.alibaba.druid.sql.dialect.mysql.visitor.MySqlASTVisitorAdapter;
import com.alibaba.druid.sql.parser.SQLStatementParser;

public class Demo2 extends TestCase {

    public void test_0() throws Exception {
        String sql = "select * from user where uid = ? and uname = ?";
        List<Object> parameters = new ArrayList<Object>();
        parameters.add(1);
        parameters.add("wenshao");

        SQLStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> stmtList = parser.parseStatementList(); //

        SQLStatement first = (SQLStatement) stmtList.get(0);

        MyVisitor visitor = new MyVisitor();
        first.accept(visitor);

        SQLVariantRefExpr firstVar = visitor.getVariantList().get(0);

        int varIndex = (Integer) firstVar.getAttribute("varIndex");
        Integer param = (Integer) parameters.get(varIndex);

        final String tableName;
        if (param.intValue() == 1) {
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
                    identExpr.setName(tableName);
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
        private List<SQLVariantRefExpr>  variantList     = new ArrayList<SQLVariantRefExpr>();
        private List<SQLExprTableSource> tableSourceList = new ArrayList<SQLExprTableSource>();

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

        public List<SQLVariantRefExpr> getVariantList() {
            return variantList;
        }

        public List<SQLExprTableSource> getTableSourceList() {
            return tableSourceList;
        }

    }


}
