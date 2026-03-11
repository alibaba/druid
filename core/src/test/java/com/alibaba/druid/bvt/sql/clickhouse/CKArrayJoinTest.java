package com.alibaba.druid.bvt.sql.clickhouse;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.SQLParseAssertUtil;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.statement.SQLJoinTableSource;
import com.alibaba.druid.sql.ast.statement.SQLSelectStatement;
import com.alibaba.druid.sql.parser.SQLParserUtils;
import com.alibaba.druid.sql.parser.SQLStatementParser;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;

public class CKArrayJoinTest {
    @Test
    public void test_array_join_basic() {
        String sql = "SELECT arr, a FROM test_table ARRAY JOIN arr AS a";
        SQLStatementParser parser = SQLParserUtils.createSQLStatementParser(sql, DbType.clickhouse);
        List<SQLStatement> stmts = parser.parseStatementList();
        assertEquals(1, stmts.size());
        SQLSelectStatement stmt = (SQLSelectStatement) stmts.get(0);
        SQLJoinTableSource join = (SQLJoinTableSource) stmt.getSelect().getQueryBlock().getFrom();
        assertEquals("ARRAY JOIN", join.getJoinType().name);
        SQLParseAssertUtil.assertParseSql(sql, DbType.clickhouse);
    }

    @Test
    public void test_left_array_join() {
        String sql = "SELECT * FROM test_table LEFT ARRAY JOIN arr AS a";
        SQLStatementParser parser = SQLParserUtils.createSQLStatementParser(sql, DbType.clickhouse);
        List<SQLStatement> stmts = parser.parseStatementList();
        assertEquals(1, stmts.size());
        SQLSelectStatement stmt = (SQLSelectStatement) stmts.get(0);
        SQLJoinTableSource join = (SQLJoinTableSource) stmt.getSelect().getQueryBlock().getFrom();
        assertEquals("LEFT ARRAY JOIN", join.getJoinType().name);
        SQLParseAssertUtil.assertParseSql(sql, DbType.clickhouse);
    }

    @Test
    public void test_array_join_with_array_literal() {
        String sql = "SELECT * FROM test_table ARRAY JOIN arr AS a, [1,2,3] AS b";
        SQLStatementParser parser = SQLParserUtils.createSQLStatementParser(sql, DbType.clickhouse);
        List<SQLStatement> stmts = parser.parseStatementList();
        assertEquals(1, stmts.size());
        SQLParseAssertUtil.assertParseSql(sql, DbType.clickhouse);
    }

    @Test
    public void test_array_join_with_array_map() {
        String sql = "SELECT s, arr, a, mapped FROM arrays_test ARRAY JOIN arr AS a, arrayMap(x -> x + 1, arr) AS mapped";
        SQLStatementParser parser = SQLParserUtils.createSQLStatementParser(sql, DbType.clickhouse);
        List<SQLStatement> stmts = parser.parseStatementList();
        assertEquals(1, stmts.size());
        SQLParseAssertUtil.assertParseSql(sql, DbType.clickhouse);
    }
}
