package com.alibaba.druid.bvt.sql.mysql.select;

import com.alibaba.druid.sql.MysqlTest;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.statement.SQLSelectStatement;
import com.alibaba.druid.util.JdbcConstants;

import java.util.List;

public class MySqlSelectTest_172_trim extends MysqlTest {

    public void test_0() throws Exception {
        String sql = "select 1 as '\\\"f\\\"a';";
//
        List<SQLStatement> statementList = SQLUtils.parseStatements(sql, JdbcConstants.MYSQL);
        SQLSelectStatement stmt = (SQLSelectStatement)statementList.get(0);

        assertEquals(1, statementList.size());

        assertEquals("SELECT 1 AS '\"f\"a';", stmt.toString());
    }

    public void test_1() throws Exception {
        String sql = "select 1 as \"\\\"f\\\"\";";
//
        List<SQLStatement> statementList = SQLUtils.parseStatements(sql, JdbcConstants.MYSQL);
        SQLSelectStatement stmt = (SQLSelectStatement)statementList.get(0);

        assertEquals(1, statementList.size());

//        System.out.println(stmt.toString());

        assertEquals("SELECT 1 AS \"\\\"f\\\"\";", stmt.toString());
    }

    public void test_2() throws Exception {
        String sql = "select 1 as \"\\\"f\\\"\";";
//
        List<SQLStatement> statementList = SQLUtils.parseStatements(sql, JdbcConstants.MYSQL);
        SQLSelectStatement stmt = (SQLSelectStatement)statementList.get(0);

        assertEquals(1, statementList.size());

//        System.out.println(stmt.toString());

        assertEquals("SELECT 1 AS \"\\\"f\\\"\";", stmt.toString());
    }

    public void test_3() throws Exception {
        String sql = "select 1 as '\\'f\\'';";
//
        List<SQLStatement> statementList = SQLUtils.parseStatements(sql, JdbcConstants.MYSQL);
        SQLSelectStatement stmt = (SQLSelectStatement)statementList.get(0);

        assertEquals(1, statementList.size());

        System.out.println(stmt.toString());

        assertEquals("SELECT 1 AS '\\'f\\'';", stmt.toString());
        assertEquals("'f'", stmt.getSelect().getQueryBlock().getSelectItem(0).getAlias2());
    }

    public void test_3x() throws Exception {
        String sql = "select 1 as '\\'\\'f\\'';";
//        System.out.println(sql);
//
        List<SQLStatement> statementList = SQLUtils.parseStatements(sql, JdbcConstants.MYSQL);
        SQLSelectStatement stmt = (SQLSelectStatement)statementList.get(0);

        assertEquals(1, statementList.size());

        assertEquals("SELECT 1 AS '\\'\\'f\\'';", stmt.toString());
        assertEquals("''f'", stmt.getSelect().getQueryBlock().getSelectItem(0).getAlias2());
    }

    public void test_4() throws Exception {
        String sql = "select 1 as \"\\'f\\'\";";
//        System.out.println(sql);
//
        List<SQLStatement> statementList = SQLUtils.parseStatements(sql, JdbcConstants.MYSQL);
        SQLSelectStatement stmt = (SQLSelectStatement)statementList.get(0);

        assertEquals(1, statementList.size());

//        System.out.println(stmt.toString());

        assertEquals("SELECT 1 AS \"'f'\";", stmt.toString());
        assertEquals("'f'", stmt.getSelect().getQueryBlock().getSelectItem(0).getAlias2());
    }

    public void test_5() throws Exception {
        String sql = "select 1 as \"\\\"f\\\"\";";
//
        List<SQLStatement> statementList = SQLUtils.parseStatements(sql, JdbcConstants.MYSQL);
        SQLSelectStatement stmt = (SQLSelectStatement)statementList.get(0);

        assertEquals(1, statementList.size());

//        System.out.println(stmt.toString());

        assertEquals("SELECT 1 AS \"\\\"f\\\"\";", stmt.toString());
        assertEquals("\"f\"", stmt.getSelect().getQueryBlock().getSelectItem(0).getAlias2());
    }

    public void test_6() throws Exception {
        String sql = "select 1 as '\n'";
//
        List<SQLStatement> statementList = SQLUtils.parseStatements(sql, JdbcConstants.MYSQL);
        SQLSelectStatement stmt = (SQLSelectStatement)statementList.get(0);

        assertEquals(1, statementList.size());

//        System.out.println(stmt.toString());

        assertEquals("SELECT 1 AS \"\n" +
                "\"", stmt.toString());
        assertEquals("\n", stmt.getSelect().getQueryBlock().getSelectItem(0).getAlias2());
    }

    public void test_7() throws Exception {
        String sql = "select 1 as '\\\\'";
        System.out.println(sql);
//
        List<SQLStatement> statementList = SQLUtils.parseStatements(sql, JdbcConstants.MYSQL);
        SQLSelectStatement stmt = (SQLSelectStatement)statementList.get(0);

        assertEquals(1, statementList.size());

        assertEquals("SELECT 1 AS \"\\\\\"", stmt.toString());
        assertEquals("\\", stmt.getSelect().getQueryBlock().getSelectItem(0).getAlias2());
    }

    public void test_8() throws Exception {
        String sql = "select 1 as '\\t'";
        System.out.println(sql);
//
        List<SQLStatement> statementList = SQLUtils.parseStatements(sql, JdbcConstants.MYSQL);
        SQLSelectStatement stmt = (SQLSelectStatement)statementList.get(0);

        assertEquals(1, statementList.size());

        assertEquals("SELECT 1 AS \"\t\"", stmt.toString());
        assertEquals("\t", stmt.getSelect().getQueryBlock().getSelectItem(0).getAlias2());
    }

    public void test_9() throws Exception {
        String sql = "select 1 as \"\"\"\", 2 as ''''";
        System.out.println(sql);
//
        List<SQLStatement> statementList = SQLUtils.parseStatements(sql, JdbcConstants.MYSQL);
        SQLSelectStatement stmt = (SQLSelectStatement)statementList.get(0);

        assertEquals(1, statementList.size());

        assertEquals("SELECT 1 AS \"\\\"\", 2 AS '\\''", stmt.toString());
        assertEquals("\"", stmt.getSelect().getQueryBlock().getSelectItem(0).getAlias2());
        assertEquals("'", stmt.getSelect().getQueryBlock().getSelectItem(1).getAlias2());
    }
}