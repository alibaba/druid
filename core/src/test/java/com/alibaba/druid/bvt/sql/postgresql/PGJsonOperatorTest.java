package com.alibaba.druid.bvt.sql.postgresql;

import com.alibaba.druid.sql.PGTest;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.postgresql.parser.PGSQLStatementParser;
import com.alibaba.druid.util.JdbcConstants;

import java.util.List;

public class PGJsonOperatorTest extends PGTest {
    public void test_arrow() {
        String sql = "SELECT data->'name' FROM users";
        PGSQLStatementParser parser = new PGSQLStatementParser(sql);
        List<SQLStatement> stmtList = parser.parseStatementList();
        assertEquals(1, stmtList.size());

        String output = SQLUtils.toSQLString(stmtList, JdbcConstants.POSTGRESQL);
        assertTrue(output.contains("->"));
    }

    public void test_double_arrow() {
        String sql = "SELECT data->>'email' FROM users";
        PGSQLStatementParser parser = new PGSQLStatementParser(sql);
        List<SQLStatement> stmtList = parser.parseStatementList();
        assertEquals(1, stmtList.size());

        String output = SQLUtils.toSQLString(stmtList, JdbcConstants.POSTGRESQL);
        assertTrue(output.contains("->>"));
    }

    public void test_hash_arrow() {
        String sql = "SELECT data#>'{address,city}' FROM users";
        PGSQLStatementParser parser = new PGSQLStatementParser(sql);
        List<SQLStatement> stmtList = parser.parseStatementList();
        assertEquals(1, stmtList.size());

        String output = SQLUtils.toSQLString(stmtList, JdbcConstants.POSTGRESQL);
        assertTrue("Expected #> in: " + output, output.contains("#>"));
    }

    public void test_hash_double_arrow() {
        String sql = "SELECT data#>>'{address,city}' FROM users";
        PGSQLStatementParser parser = new PGSQLStatementParser(sql);
        List<SQLStatement> stmtList = parser.parseStatementList();
        assertEquals(1, stmtList.size());

        String output = SQLUtils.toSQLString(stmtList, JdbcConstants.POSTGRESQL);
        assertTrue("Expected #>> in: " + output, output.contains("#>>"));
    }

    public void test_at_greater_than() {
        String sql = "SELECT * FROM users WHERE data @> '{\"active\": true}'::jsonb";
        PGSQLStatementParser parser = new PGSQLStatementParser(sql);
        List<SQLStatement> stmtList = parser.parseStatementList();
        assertEquals(1, stmtList.size());

        String output = SQLUtils.toSQLString(stmtList, JdbcConstants.POSTGRESQL);
        assertTrue(output.contains("@>"));
    }

    public void test_less_than_at() {
        String sql = "SELECT * FROM users WHERE '{\"a\":1}' <@ data";
        PGSQLStatementParser parser = new PGSQLStatementParser(sql);
        List<SQLStatement> stmtList = parser.parseStatementList();
        assertEquals(1, stmtList.size());

        String output = SQLUtils.toSQLString(stmtList, JdbcConstants.POSTGRESQL);
        assertTrue(output.contains("<@"));
    }

    public void test_question_mark() {
        String sql = "SELECT * FROM users WHERE data ? 'name'";
        PGSQLStatementParser parser = new PGSQLStatementParser(sql);
        List<SQLStatement> stmtList = parser.parseStatementList();
        assertEquals(1, stmtList.size());

        String output = SQLUtils.toSQLString(stmtList, JdbcConstants.POSTGRESQL);
        assertTrue(output.contains("?"));
    }

    public void test_question_pipe() {
        String sql = "SELECT * FROM users WHERE data ?| array['name', 'email']";
        PGSQLStatementParser parser = new PGSQLStatementParser(sql);
        List<SQLStatement> stmtList = parser.parseStatementList();
        assertEquals(1, stmtList.size());

        String output = SQLUtils.toSQLString(stmtList, JdbcConstants.POSTGRESQL);
        assertTrue(output.contains("?|"));
    }

    public void test_question_amp() {
        String sql = "SELECT * FROM users WHERE data ?& array['name', 'email']";
        PGSQLStatementParser parser = new PGSQLStatementParser(sql);
        List<SQLStatement> stmtList = parser.parseStatementList();
        assertEquals(1, stmtList.size());

        String output = SQLUtils.toSQLString(stmtList, JdbcConstants.POSTGRESQL);
        assertTrue(output.contains("?&"));
    }
}
