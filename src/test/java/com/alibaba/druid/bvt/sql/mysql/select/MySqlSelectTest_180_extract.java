package com.alibaba.druid.bvt.sql.mysql.select;

import com.alibaba.druid.sql.MysqlTest;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.statement.SQLSelectStatement;
import com.alibaba.druid.util.JdbcConstants;

import java.util.List;

public class MySqlSelectTest_180_extract extends MysqlTest {

    public void test_0() throws Exception {
        String sql = "SELECT extract(day_of_week FROM '2001-08-22 03:04:05.321');";

        List<SQLStatement> statementList = SQLUtils.parseStatements(sql, JdbcConstants.MYSQL);
        SQLSelectStatement stmt = (SQLSelectStatement)statementList.get(0);

        assertEquals(1, statementList.size());

        assertEquals("SELECT EXTRACT(DAY_OF_WEEK FROM '2001-08-22 03:04:05.321');", stmt.toString());
    }

    public void test_1() throws Exception {
        String sql = "SELECT extract(dow FROM '2001-08-22 03:04:05.321');";

        List<SQLStatement> statementList = SQLUtils.parseStatements(sql, JdbcConstants.MYSQL);
        SQLSelectStatement stmt = (SQLSelectStatement)statementList.get(0);

        assertEquals(1, statementList.size());

        assertEquals("SELECT EXTRACT(DOW FROM '2001-08-22 03:04:05.321');", stmt.toString());
    }

    public void test_2() throws Exception {
        String sql = "SELECT extract(day_of_month FROM '2001-08-22 03:04:05.321');";

        List<SQLStatement> statementList = SQLUtils.parseStatements(sql, JdbcConstants.MYSQL);
        SQLSelectStatement stmt = (SQLSelectStatement)statementList.get(0);

        assertEquals(1, statementList.size());

        assertEquals("SELECT EXTRACT(DAY_OF_MONTH FROM '2001-08-22 03:04:05.321');", stmt.toString());
    }

    public void test_3() throws Exception {
        String sql = "SELECT extract(day_of_year FROM '2001-08-22 03:04:05.321');";

        List<SQLStatement> statementList = SQLUtils.parseStatements(sql, JdbcConstants.MYSQL);
        SQLSelectStatement stmt = (SQLSelectStatement)statementList.get(0);

        assertEquals(1, statementList.size());

        assertEquals("SELECT EXTRACT(DAY_OF_YEAR FROM '2001-08-22 03:04:05.321');", stmt.toString());
    }

    public void test_4() throws Exception {
        String sql = "SELECT extract(year_of_week FROM '2001-08-22 03:04:05.321');";

        List<SQLStatement> statementList = SQLUtils.parseStatements(sql, JdbcConstants.MYSQL);
        SQLSelectStatement stmt = (SQLSelectStatement)statementList.get(0);

        assertEquals(1, statementList.size());

        assertEquals("SELECT EXTRACT(YEAR_OF_WEEK FROM '2001-08-22 03:04:05.321');", stmt.toString());
    }

    public void test_5() throws Exception {
        String sql = "SELECT extract(doy FROM '2001-08-22 03:04:05.321');";

        List<SQLStatement> statementList = SQLUtils.parseStatements(sql, JdbcConstants.MYSQL);
        SQLSelectStatement stmt = (SQLSelectStatement)statementList.get(0);

        assertEquals(1, statementList.size());

        assertEquals("SELECT EXTRACT(DOY FROM '2001-08-22 03:04:05.321');", stmt.toString());
    }

    public void test_6() throws Exception {
        String sql = "SELECT extract(yow FROM '2001-08-22 03:04:05.321');";

        List<SQLStatement> statementList = SQLUtils.parseStatements(sql, JdbcConstants.MYSQL);
        SQLSelectStatement stmt = (SQLSelectStatement)statementList.get(0);

        assertEquals(1, statementList.size());

        assertEquals("SELECT EXTRACT(YOW FROM '2001-08-22 03:04:05.321');", stmt.toString());
    }
}