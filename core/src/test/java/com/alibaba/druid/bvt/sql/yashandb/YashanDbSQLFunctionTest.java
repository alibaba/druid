package com.alibaba.druid.bvt.sql.yashandb;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.expr.SQLExprUtils;
import com.alibaba.druid.sql.ast.expr.SQLMethodInvokeExpr;
import com.alibaba.druid.sql.builder.SQLFunctionBuilder;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class YashanDbSQLFunctionTest {
    @Test
    public void test_ifnull_produces_nvl() throws Exception {
        SQLFunctionBuilder builder = new SQLFunctionBuilder(DbType.yashandb);
        SQLMethodInvokeExpr expr = builder.ifnull(
                new com.alibaba.druid.sql.ast.expr.SQLIdentifierExpr("col1"),
                new com.alibaba.druid.sql.ast.expr.SQLCharExpr("default")
        );
        assertEquals("nvl", expr.getMethodName().toLowerCase());
        assertEquals(2, expr.getArguments().size());
    }

    @Test
    public void test_ifnull_yashandb_same_as_oracle() throws Exception {
        SQLFunctionBuilder yashanBuilder = new SQLFunctionBuilder(DbType.yashandb);
        SQLFunctionBuilder oracleBuilder = new SQLFunctionBuilder(DbType.oracle);

        SQLMethodInvokeExpr yashanExpr = yashanBuilder.ifnull(
                new com.alibaba.druid.sql.ast.expr.SQLIdentifierExpr("a"),
                new com.alibaba.druid.sql.ast.expr.SQLIdentifierExpr("b"));
        SQLMethodInvokeExpr oracleExpr = oracleBuilder.ifnull(
                new com.alibaba.druid.sql.ast.expr.SQLIdentifierExpr("a"),
                new com.alibaba.druid.sql.ast.expr.SQLIdentifierExpr("b"));

        assertEquals(yashanExpr.getMethodName(), oracleExpr.getMethodName());
    }

    @Test
    public void test_quote_double_quote_as_identifier() throws Exception {
        // For yashandb, double-quoted strings should become identifiers (like Oracle)
        String result = SQLExprUtils.quote("myColumn", DbType.yashandb, '"');
        assertNotNull(result);
        assertTrue(result.contains("myColumn"));
    }

    @Test
    public void test_quote_double_quote_yashandb_same_as_oracle() throws Exception {
        String yashanResult = SQLExprUtils.quote("test", DbType.yashandb, '"');
        String oracleResult = SQLExprUtils.quote("test", DbType.oracle, '"');
        assertEquals(oracleResult, yashanResult);
    }

    @Test
    public void test_quote_single_quote_as_char() throws Exception {
        String result = SQLExprUtils.quote("hello", DbType.yashandb, '\'');
        assertNotNull(result);
        assertTrue(result.contains("hello"));
    }

    @Test
    public void test_buildToDate_produces_toDate() throws Exception {
        String result = SQLUtils.buildToDate("create_time", null, "yyyy-mm-dd hh24:mi:ss", DbType.yashandb);
        assertNotNull(result);
        assertTrue(result.startsWith("TO_DATE("));
        assertTrue(result.contains("create_time"));
        assertTrue(result.contains("yyyy-mm-dd hh24:mi:ss"));
    }

    @Test
    public void test_buildToDate_default_pattern() throws Exception {
        String result = SQLUtils.buildToDate("col1", "t", null, DbType.yashandb);
        assertNotNull(result);
        assertTrue(result.startsWith("TO_DATE("));
        assertTrue(result.contains("t.col1"));
        assertTrue(result.contains("yyyy-mm-dd hh24:mi:ss"));
    }

    @Test
    public void test_buildToDate_yashandb_same_as_oracle() throws Exception {
        String yashanResult = SQLUtils.buildToDate("col", null, "yyyy-mm-dd", DbType.yashandb);
        String oracleResult = SQLUtils.buildToDate("col", null, "yyyy-mm-dd", DbType.oracle);
        assertEquals(oracleResult, yashanResult);
    }
}
