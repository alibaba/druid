package com.alibaba.druid.bvt.sql.postgresql.select;

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.util.JdbcConstants;
import junit.framework.TestCase;

import java.util.List;

public class PGSelectTest80_json extends TestCase {
    public void test_0() throws Exception {
        String sql = "select '[{\"a\":\"foo\"},{\"b\":\"bar\"},{\"c\":\"baz\"}]'::json->2";

        final List<SQLStatement> statements = SQLUtils.parseStatements(sql, JdbcConstants.POSTGRESQL);
        assertEquals(1, statements.size());
        final SQLStatement stmt = statements.get(0);


        assertEquals("SELECT '[{\"a\":\"foo\"},{\"b\":\"bar\"},{\"c\":\"baz\"}]'::json -> 2", stmt.toString());

        assertEquals("select '[{\"a\":\"foo\"},{\"b\":\"bar\"},{\"c\":\"baz\"}]'::json -> 2", stmt.toLowerCaseString());
    }

    public void test_1() throws Exception {
        String sql = "select '{\"a\": {\"b\":\"foo\"}}'::json->'a'";

        final List<SQLStatement> statements = SQLUtils.parseStatements(sql, JdbcConstants.POSTGRESQL);
        assertEquals(1, statements.size());
        final SQLStatement stmt = statements.get(0);


        assertEquals("SELECT '{\"a\": {\"b\":\"foo\"}}'::json -> 'a'", stmt.toString());

        assertEquals("select '{\"a\": {\"b\":\"foo\"}}'::json -> 'a'", stmt.toLowerCaseString());
    }

    public void test_2() throws Exception {
        String sql = "select '[1,2,3]'::json->>2";

        final List<SQLStatement> statements = SQLUtils.parseStatements(sql, JdbcConstants.POSTGRESQL);
        assertEquals(1, statements.size());
        final SQLStatement stmt = statements.get(0);


        assertEquals("SELECT '[1,2,3]'::json ->> 2", stmt.toString());

        assertEquals("select '[1,2,3]'::json ->> 2", stmt.toLowerCaseString());
    }

    public void test_3() throws Exception {
        String sql = "select '{\"a\": {\"b\":{\"c\": \"foo\"}}}'::json#>'{a,b}'";

        final List<SQLStatement> statements = SQLUtils.parseStatements(sql, JdbcConstants.POSTGRESQL);
        assertEquals(1, statements.size());
        final SQLStatement stmt = statements.get(0);


        assertEquals("SELECT '{\"a\": {\"b\":{\"c\": \"foo\"}}}'::json# > '{a,b}'", stmt.toString());

        assertEquals("select '{\"a\": {\"b\":{\"c\": \"foo\"}}}'::json# > '{a,b}'", stmt.toLowerCaseString());
    }

    public void test_4() throws Exception {
        String sql = "select '{\"a\":1, \"b\":2}'::jsonb @> '{\"b\":2}'::jsonb";

        final List<SQLStatement> statements = SQLUtils.parseStatements(sql, JdbcConstants.POSTGRESQL);
        assertEquals(1, statements.size());
        final SQLStatement stmt = statements.get(0);


        assertEquals("SELECT '{\"a\":1, \"b\":2}'::jsonb @> '{\"b\":2}'::jsonb", stmt.toString());

        assertEquals("select '{\"a\":1, \"b\":2}'::jsonb @> '{\"b\":2}'::jsonb", stmt.toLowerCaseString());
    }

    public void test_5() throws Exception {
        String sql = "select '{\"a\":1, \"b\":2}'::jsonb <@ '{\"b\":2}'::jsonb";

        final List<SQLStatement> statements = SQLUtils.parseStatements(sql, JdbcConstants.POSTGRESQL);
        assertEquals(1, statements.size());
        final SQLStatement stmt = statements.get(0);


        assertEquals("SELECT '{\"a\":1, \"b\":2}'::jsonb <@ '{\"b\":2}'::jsonb", stmt.toString());

        assertEquals("select '{\"a\":1, \"b\":2}'::jsonb <@ '{\"b\":2}'::jsonb", stmt.toLowerCaseString());
    }

    public void test_6() throws Exception {
        String sql = "select '{\"a\":1, \"b\":2}'::jsonb ? 'b'";

        final List<SQLStatement> statements = SQLUtils.parseStatements(sql, JdbcConstants.POSTGRESQL);
        assertEquals(1, statements.size());
        final SQLStatement stmt = statements.get(0);


        assertEquals("SELECT '{\"a\":1, \"b\":2}'::jsonb ? 'b'", stmt.toString());

        assertEquals("select '{\"a\":1, \"b\":2}'::jsonb ? 'b'", stmt.toLowerCaseString());
    }

    public void test_7() throws Exception {
        String sql = "select '{\"a\":1, \"b\":2, \"c\":3}'::jsonb ?| array['b', 'c']";

        final List<SQLStatement> statements = SQLUtils.parseStatements(sql, JdbcConstants.POSTGRESQL);
        assertEquals(1, statements.size());
        final SQLStatement stmt = statements.get(0);


        assertEquals("SELECT '{\"a\":1, \"b\":2, \"c\":3}'::jsonb ?| array['b', 'c']", stmt.toString());

        assertEquals("select '{\"a\":1, \"b\":2, \"c\":3}'::jsonb ?| array['b', 'c']", stmt.toLowerCaseString());
    }

    public void test_8() throws Exception {
        String sql = "select '{\"a\":1, \"b\":2, \"c\":3}'::jsonb ?& array['b', 'c']";

        final List<SQLStatement> statements = SQLUtils.parseStatements(sql, JdbcConstants.POSTGRESQL);
        assertEquals(1, statements.size());
        final SQLStatement stmt = statements.get(0);


        assertEquals("SELECT '{\"a\":1, \"b\":2, \"c\":3}'::jsonb ?& array['b', 'c']", stmt.toString());

        assertEquals("select '{\"a\":1, \"b\":2, \"c\":3}'::jsonb ?& array['b', 'c']", stmt.toLowerCaseString());
    }

}
