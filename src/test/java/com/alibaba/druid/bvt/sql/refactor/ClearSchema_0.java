package com.alibaba.druid.bvt.sql.refactor;

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.expr.SQLCaseExpr;
import com.alibaba.druid.sql.ast.expr.SQLIdentifierExpr;
import com.alibaba.druid.sql.ast.expr.SQLPropertyExpr;
import com.alibaba.druid.sql.dialect.mysql.visitor.MySqlASTVisitorAdapter;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;
import com.alibaba.druid.sql.visitor.SQLASTVisitorAdapter;
import com.alibaba.druid.util.JdbcConstants;
import junit.framework.TestCase;

import java.util.List;

public class ClearSchema_0 extends TestCase {
    public void test_insert_0() throws Exception {
        String sql = "INSERT INTO testdb.Websites (name, country)\n" +
                "SELECT app_name, country FROM testdb.apps;";

        SQLStatement stmt = SQLUtils.parseStatements(sql, JdbcConstants.MYSQL).get(0);

        SQLASTVisitor v = new SQLASTVisitorAdapter() {
            @Override
            public boolean visit(SQLPropertyExpr x) {
                if (SQLUtils.replaceInParent(x, new SQLIdentifierExpr(x.getName()))) {
                    return false;
                }
                return super.visit(x);
            }
        };
        stmt.accept(v);

        assertEquals("INSERT INTO Websites (name, country)\n" +
                "SELECT app_name, country\n" +
                "FROM apps;", stmt.toString());
    }
}
