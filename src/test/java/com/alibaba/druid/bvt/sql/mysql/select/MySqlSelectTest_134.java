package com.alibaba.druid.bvt.sql.mysql.select;

import com.alibaba.druid.sql.MysqlTest;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.statement.SQLSelectStatement;
import com.alibaba.druid.sql.visitor.ParameterizedOutputVisitorUtils;
import com.alibaba.druid.util.JdbcConstants;

import java.util.List;

public class MySqlSelectTest_134 extends MysqlTest {

    public void test_0() throws Exception {
        String sql = "/*TDDL:RETRY_ERROR_SQL_ON_OLD_SERVER=FALSE*/\n" +
                "select   (('b')not between('a')AND ('x-3')    )\n" +
                "\t,(    (WEIGHT_STRING( 'ab' AS CHAR(4)))  is  not  UNKNOWN  )\n" +
                "from select_base_two_one_db_one_tb";

        List<SQLStatement> statementList = SQLUtils.parseStatements(sql, JdbcConstants.MYSQL);
        SQLSelectStatement stmt = (SQLSelectStatement)statementList.get(0);

        assertEquals(1, statementList.size());

        assertEquals("/*TDDL:RETRY_ERROR_SQL_ON_OLD_SERVER=FALSE*/\n" +
                "SELECT 'b' NOT BETWEEN 'a' AND 'x-3', WEIGHT_STRING('ab' AS CHAR(4)) IS NOT UNKNOWN\n" +
                "FROM select_base_two_one_db_one_tb", stmt.toString());

        assertEquals("SELECT ? NOT BETWEEN ? AND ?, WEIGHT_STRING(? AS CHAR(4)) IS NOT UNKNOWN\n" +
                "FROM select_base_two_one_db_one_tb", ParameterizedOutputVisitorUtils.parameterize(sql, JdbcConstants.MYSQL));
    }

    public void test_1() throws Exception {
        String sql = "SELECT HEX(WEIGHT_STRING(0x007fff LEVEL 1));";

        List<SQLStatement> statementList = SQLUtils.parseStatements(sql, JdbcConstants.MYSQL);
        SQLSelectStatement stmt = (SQLSelectStatement)statementList.get(0);

        assertEquals(1, statementList.size());

        assertEquals("SELECT HEX(WEIGHT_STRING(0x007fff LEVEL 1));", stmt.toString());

        assertEquals("SELECT HEX(WEIGHT_STRING(? LEVEL 1));", ParameterizedOutputVisitorUtils.parameterize(sql, JdbcConstants.MYSQL));
    }

    public void test_2() throws Exception {
        String sql = "SELECT HEX(WEIGHT_STRING(0x007fff LEVEL 1 DESC));";

        List<SQLStatement> statementList = SQLUtils.parseStatements(sql, JdbcConstants.MYSQL);
        SQLSelectStatement stmt = (SQLSelectStatement)statementList.get(0);

        assertEquals(1, statementList.size());

        assertEquals("SELECT HEX(WEIGHT_STRING(0x007fff LEVEL 1 DESC));", stmt.toString());

        assertEquals("SELECT HEX(WEIGHT_STRING(? LEVEL 1 DESC));", ParameterizedOutputVisitorUtils.parameterize(sql, JdbcConstants.MYSQL));
    }

    public void test_3() throws Exception {
        String sql = "SELECT HEX(WEIGHT_STRING(0x007fff LEVEL 2, 3, 5));";

        List<SQLStatement> statementList = SQLUtils.parseStatements(sql, JdbcConstants.MYSQL);
        SQLSelectStatement stmt = (SQLSelectStatement)statementList.get(0);

        assertEquals(1, statementList.size());

        assertEquals("SELECT HEX(WEIGHT_STRING(0x007fff LEVEL 2, 3, 5));", stmt.toString());

        assertEquals("SELECT HEX(WEIGHT_STRING(? LEVEL 2, 3, 5));", ParameterizedOutputVisitorUtils.parameterize(sql, JdbcConstants.MYSQL));
    }

    public void test_4() throws Exception {
        String sql = "SELECT HEX(WEIGHT_STRING(0x007fff LEVEL 1 DESC REVERSE));";

        List<SQLStatement> statementList = SQLUtils.parseStatements(sql, JdbcConstants.MYSQL);
        SQLSelectStatement stmt = (SQLSelectStatement)statementList.get(0);

        assertEquals(1, statementList.size());

        assertEquals("SELECT HEX(WEIGHT_STRING(0x007fff LEVEL 1 DESC REVERSE));", stmt.toString());

        assertEquals("SELECT HEX(WEIGHT_STRING(? LEVEL 1 DESC REVERSE));", ParameterizedOutputVisitorUtils.parameterize(sql, JdbcConstants.MYSQL));
    }
}