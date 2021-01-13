package com.alibaba.druid.bvt.sql.mysql.select;

import com.alibaba.druid.sql.MysqlTest;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.expr.SQLBinaryOpExpr;
import com.alibaba.druid.sql.ast.expr.SQLBinaryOperator;
import com.alibaba.druid.sql.ast.statement.SQLSelectStatement;
import com.alibaba.druid.sql.dialect.mysql.parser.MySqlStatementParser;
import com.alibaba.druid.util.JdbcConstants;

import java.util.List;

public class MySqlSelectTest_123_mod extends MysqlTest {

    public void test_0() throws Exception {
        String sql = "SELECT 3 mod";

        List<SQLStatement> statementList = SQLUtils.parseStatements(sql, JdbcConstants.MYSQL);
        SQLSelectStatement stmt = (SQLSelectStatement)statementList.get(0);

        assertEquals(1, statementList.size());

        assertEquals("SELECT 3 AS mod", stmt.toString());
    }

    public void test_1() throws Exception {
        String sql = "SELECT 3 `mod`";

        List<SQLStatement> statementList = SQLUtils.parseStatements(sql, JdbcConstants.MYSQL);
        SQLSelectStatement stmt = (SQLSelectStatement)statementList.get(0);

        assertEquals(1, statementList.size());

        assertEquals("SELECT 3 AS `mod`", stmt.toString());
    }

    public void test_2() throws Exception {
        String sql = "SELECT n mod m from t";

        List<SQLStatement> statementList = SQLUtils.parseStatements(sql, JdbcConstants.MYSQL);
        SQLSelectStatement stmt = (SQLSelectStatement)statementList.get(0);

        assertEquals(1, statementList.size());

        assertEquals("SELECT n % m\n" +
                "FROM t", stmt.toString());
    }

    public void test_3() throws Exception {
        String sql = "select 2.1 mod 2 = 2";

        List<SQLStatement> statementList = SQLUtils.parseStatements(sql, JdbcConstants.MYSQL);
        SQLSelectStatement stmt = (SQLSelectStatement)statementList.get(0);

        assertEquals(1, statementList.size());

        assertEquals("SELECT 2.1 % 2 = 2", stmt.toString());

        SQLBinaryOpExpr expr = (SQLBinaryOpExpr) stmt.getSelect().getQueryBlock().getSelectList().get(0).getExpr();
        assertEquals(SQLBinaryOperator.Equality, expr.getOperator());
    }
}