package com.alibaba.druid.bvt.sql.starrocks.parser;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class StarRocksLambdaTest {
    @Test
    public void testSingleParamLambda() {
        String sql = "SELECT array_map(x -> x * 2, array_col) FROM t";
        SQLStatement stmt = SQLUtils.parseSingleStatement(sql, DbType.starrocks);
        assertNotNull(stmt);
        String output = stmt.toString();
        assertEquals("SELECT array_map(x -> x * 2, array_col)\nFROM t", output);
    }

    @Test
    public void testMultiParamLambda() {
        String sql = "SELECT array_filter((x, y) -> x + y > 10, arr1, arr2) FROM t";
        SQLStatement stmt = SQLUtils.parseSingleStatement(sql, DbType.starrocks);
        assertNotNull(stmt);
        String output = stmt.toString();
        assertEquals("SELECT array_filter((x, y) -> x + y > 10, arr1, arr2)\nFROM t", output);
    }

    @Test
    public void testLambdaWithColumnReference() {
        String sql = "SELECT array_map(x -> x > avg_score, scores) FROM t";
        SQLStatement stmt = SQLUtils.parseSingleStatement(sql, DbType.starrocks);
        assertNotNull(stmt);
        String output = stmt.toString();
        assertEquals("SELECT array_map(x -> x > avg_score, scores)\nFROM t", output);
    }

    @Test
    public void testLambdaClone() {
        String sql = "SELECT array_map(x -> x + 1, arr) FROM t";
        SQLStatement stmt = SQLUtils.parseSingleStatement(sql, DbType.starrocks);
        SQLStatement cloned = stmt.clone();
        assertEquals(stmt.toString(), cloned.toString());
    }

    @Test
    public void testLambdaInNestedFunction() {
        String sql = "SELECT array_map(x -> x * 2, array_filter(y -> y > 0, arr)) FROM t";
        SQLStatement stmt = SQLUtils.parseSingleStatement(sql, DbType.starrocks);
        assertNotNull(stmt);
        String output = stmt.toString();
        assertEquals("SELECT array_map(x -> x * 2, array_filter(y -> y > 0, arr))\nFROM t", output);
    }

    @Test
    public void testNestedLambda() {
        // a lambda whose body is itself a lambda — exercises the bitXorRest -> tryParseLambda re-entry path
        String sql = "SELECT array_map(x -> array_map(y -> x + y, arr2), arr1) FROM t";
        SQLStatement stmt = SQLUtils.parseSingleStatement(sql, DbType.starrocks);
        assertNotNull(stmt);
        assertEquals("SELECT array_map(x -> array_map(y -> x + y, arr2), arr1)\nFROM t", stmt.toString());
        assertEquals(stmt.toString(), stmt.clone().toString());
    }

    @Test
    public void testLambdaArrowNotJsonArrow() {
        // `col -> 'json_path'` (string-literal RHS) must stay a SubGt JSON-arrow, not become a lambda
        String sql = "SELECT j -> 'a' FROM t";
        SQLStatement stmt = SQLUtils.parseSingleStatement(sql, DbType.starrocks);
        assertNotNull(stmt);
        assertEquals("SELECT j -> 'a'\nFROM t", stmt.toString());
    }
}
