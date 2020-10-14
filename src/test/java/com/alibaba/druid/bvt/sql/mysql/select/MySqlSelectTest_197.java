package com.alibaba.druid.bvt.sql.mysql.select;

import com.alibaba.druid.sql.MysqlTest;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.statement.SQLSelectStatement;
import com.alibaba.druid.sql.dialect.mysql.parser.MySqlStatementParser;

import java.util.List;

public class MySqlSelectTest_197 extends MysqlTest {

    public void test_0() throws Exception {
        String sql = "select \"\"\"1\"\"\"\"\" as a;";

        System.out.println(sql);

        MySqlStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();
        SQLSelectStatement stmt = (SQLSelectStatement)statementList.get(0);

        assertEquals(1, statementList.size());

        assertEquals("SELECT '\"1\"\"' AS a;", stmt.toString());

        assertEquals("select '\"1\"\"' as a;", stmt.toLowerCaseString());
    }

    public void test_1() throws Exception {
        String sql = "select \"\\\"1\"\"\"\"\" as a;";

        System.out.println(sql);

        MySqlStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();
        SQLSelectStatement stmt = (SQLSelectStatement)statementList.get(0);

        assertEquals(1, statementList.size());

        assertEquals("SELECT '\"1\"\"' AS a;", stmt.toString());

        assertEquals("select '\"1\"\"' as a;", stmt.toLowerCaseString());
    }

    public void test_2() throws Exception {
        String sql = "select \"\"\"1\"\"\\\"\" as a;";

        System.out.println(sql);

        MySqlStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();
        SQLSelectStatement stmt = (SQLSelectStatement)statementList.get(0);

        assertEquals(1, statementList.size());

        assertEquals("SELECT '\"1\"\"' AS a;", stmt.toString());

        assertEquals("select '\"1\"\"' as a;", stmt.toLowerCaseString());
    }

    public void test_3() throws Exception {
        String sql = "select '''1''''' as a;";

        System.out.println(sql);

        MySqlStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();
        SQLSelectStatement stmt = (SQLSelectStatement)statementList.get(0);

        assertEquals(1, statementList.size());

        assertEquals("SELECT '''1''''' AS a;", stmt.toString());

    }

    public void test_4() throws Exception {
        String sql =  "select '\\'1\\'\\'' as a;";

        System.out.println(sql);

        MySqlStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();
        SQLSelectStatement stmt = (SQLSelectStatement)statementList.get(0);

        assertEquals(1, statementList.size());

        assertEquals("SELECT '''1''''' AS a;", stmt.toString());

    }
}