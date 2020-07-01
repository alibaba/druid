package com.alibaba.druid.bvt.sql.refactor;

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.statement.SQLDeleteStatement;
import com.alibaba.druid.sql.ast.statement.SQLSelectStatement;
import com.alibaba.druid.sql.ast.statement.SQLUpdateStatement;
import com.alibaba.druid.util.JdbcConstants;
import junit.framework.TestCase;

public class AddWhere_0 extends TestCase {
    public void test_select_0() throws Exception {
        String sql = "select * from t";
        SQLSelectStatement stmt = (SQLSelectStatement) SQLUtils.parseStatements(sql, JdbcConstants.MYSQL).get(0);
        stmt.addWhere(SQLUtils.toSQLExpr("id = 1", JdbcConstants.MYSQL));

        assertEquals("SELECT *\n" +
                "FROM t\n" +
                "WHERE id = 1", stmt.toString());
    }

    public void test_select_1() throws Exception {
        String sql = "select * from t where name = 'xx'";
        SQLSelectStatement stmt = (SQLSelectStatement) SQLUtils.parseStatements(sql, JdbcConstants.MYSQL).get(0);
        stmt.addWhere(SQLUtils.toSQLExpr("id = 1", JdbcConstants.MYSQL));

        assertEquals("SELECT *\n" +
                "FROM t\n" +
                "WHERE name = 'xx'\n" +
                "\tAND id = 1", stmt.toString());
    }

    public void test_select_1_union() throws Exception {
        String sql = "select * from t1 union all select * from t2";
        SQLSelectStatement stmt = (SQLSelectStatement) SQLUtils.parseStatements(sql, JdbcConstants.MYSQL).get(0);
        stmt.addWhere(SQLUtils.toSQLExpr("id = 1", JdbcConstants.MYSQL));

        assertEquals("SELECT *\n" +
                "FROM (\n" +
                "\tSELECT *\n" +
                "\tFROM t1\n" +
                "\tUNION ALL\n" +
                "\tSELECT *\n" +
                "\tFROM t2\n" +
                ") u", stmt.toString());
    }

    public void test_delete_0() throws Exception {
        String sql = "delete from t";
        SQLDeleteStatement stmt = (SQLDeleteStatement) SQLUtils.parseStatements(sql, JdbcConstants.MYSQL).get(0);
        stmt.addWhere(SQLUtils.toSQLExpr("id = 1", JdbcConstants.MYSQL));

        assertEquals("DELETE FROM t\n" +
                "WHERE id = 1", stmt.toString());
    }

    public void test_delete_1() throws Exception {
        String sql = "delete from t where name = 'xx'";
        SQLDeleteStatement stmt = (SQLDeleteStatement) SQLUtils.parseStatements(sql, JdbcConstants.MYSQL).get(0);
        stmt.addWhere(SQLUtils.toSQLExpr("id = 1", JdbcConstants.MYSQL));

        assertEquals("DELETE FROM t\n" +
                "WHERE name = 'xx'\n" +
                "\tAND id = 1", stmt.toString());
    }

    public void test_update_0() throws Exception {
        String sql = "update t set val = 'abc' where name = 'xx'";
        SQLUpdateStatement stmt = (SQLUpdateStatement) SQLUtils.parseStatements(sql, JdbcConstants.MYSQL).get(0);
        stmt.addWhere(SQLUtils.toSQLExpr("id = 1", JdbcConstants.MYSQL));

        assertEquals("UPDATE t\n" +
                "SET val = 'abc'\n" +
                "WHERE name = 'xx'\n" +
                "\tAND id = 1", stmt.toString());
    }
}
