package com.alibaba.druid.bvt.sql.postgresql.issues;

import java.util.List;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.parser.SQLParserUtils;
import com.alibaba.druid.sql.parser.SQLStatementParser;

import org.junit.Test;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertEquals;

/**
 * @see <a href="https://github.com/alibaba/druid/issues/6573">Issue来源</a>
 */
public class Issue6573 {

    @Test
    public void test_order_by_collate() {
        String sql = "SELECT * FROM t ORDER BY col COLLATE \"zh-Hans-x-icu\" DESC";
        SQLStatementParser parser = SQLParserUtils.createSQLStatementParser(sql, DbType.postgresql);
        List<SQLStatement> stmtList = parser.parseStatementList();
        assertEquals(1, stmtList.size());
        String output = stmtList.get(0).toString();
        System.out.println("OUTPUT1: " + output.replace("\n", " | "));
        assertTrue("COLLATE clause should be preserved, got: " + output, output.contains("COLLATE"));
    }

    @Test
    public void test_order_by_cast_collate() {
        // From the original issue - cast with COLLATE
        String sql = "SELECT * FROM t ORDER BY col::text COLLATE \"zh-Hans-x-icu\" DESC LIMIT 10";
        SQLStatementParser parser = SQLParserUtils.createSQLStatementParser(sql, DbType.postgresql);
        List<SQLStatement> stmtList = parser.parseStatementList();
        assertEquals(1, stmtList.size());
        String output = stmtList.get(0).toString();
        System.out.println("OUTPUT3: " + output.replace("\n", " | "));
        assertTrue("COLLATE clause should be preserved in cast expr, got: " + output, output.contains("COLLATE"));
    }

    @Test
    public void test_order_by_collate_simple() {
        String sql = "SELECT * FROM t ORDER BY col COLLATE \"C\" DESC";
        SQLStatementParser parser = SQLParserUtils.createSQLStatementParser(sql, DbType.postgresql);
        List<SQLStatement> stmtList = parser.parseStatementList();
        assertEquals(1, stmtList.size());
        String output = stmtList.get(0).toString();
        System.out.println("OUTPUT2: " + output.replace("\n", " | "));
        assertTrue("COLLATE clause should be preserved, got: " + output, output.contains("COLLATE"));
    }
}
