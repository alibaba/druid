package com.alibaba.druid.bvt.sql.eval;

import java.math.BigDecimal;
import java.math.BigInteger;

import junit.framework.TestCase;

import static org.junit.Assert.*;

import com.alibaba.druid.sql.visitor.SQLEvalVisitorUtils;
import com.alibaba.druid.util.JdbcConstants;

public class EvalTest_div extends TestCase {
    public void test_long() throws Exception {
        assertEquals(0L, SQLEvalVisitorUtils.evalExpr(JdbcConstants.MYSQL, "?/?", (long) 1, (byte) 2));
    }

    public void test_int() throws Exception {
        assertEquals(0, SQLEvalVisitorUtils.evalExpr(JdbcConstants.MYSQL, "?/?", (int) 1, (byte) 2));
    }

    public void test_short() throws Exception {
        assertEquals(0, SQLEvalVisitorUtils.evalExpr(JdbcConstants.MYSQL, "?/?", (short) 1, (byte) 2));
    }

    public void test_byte() throws Exception {
        assertEquals(0, SQLEvalVisitorUtils.evalExpr(JdbcConstants.MYSQL, "?/?", (byte) 1, (byte) 2));
    }

    public void test_BigInteger() throws Exception {
        assertEquals(BigInteger.ZERO,
                SQLEvalVisitorUtils.evalExpr(JdbcConstants.MYSQL, "?/?", BigInteger.ONE, (byte) 2));
    }

    public void test_BigDecimal() throws Exception {
        assertEquals(new BigDecimal("0.5"),
                SQLEvalVisitorUtils.evalExpr(JdbcConstants.MYSQL, "?/?", BigDecimal.ONE, (byte) 2));
    }

    public void test_float() throws Exception {
        assertEquals(0.5F, SQLEvalVisitorUtils.evalExpr(JdbcConstants.MYSQL, "?/?", (float) 1, (byte) 2));
    }

    public void test_double() throws Exception {
        assertEquals(0.5D, SQLEvalVisitorUtils.evalExpr(JdbcConstants.MYSQL, "?/?", (double) 1, (byte) 2));
    }

    public void test_double_zero() throws Exception {
        assertEquals(Double.POSITIVE_INFINITY,
                SQLEvalVisitorUtils.evalExpr(JdbcConstants.MYSQL, "?/?", (double) 1, 0));
    }

    public void test_double_zero_1() throws Exception {
        assertEquals(Double.NEGATIVE_INFINITY,
                SQLEvalVisitorUtils.evalExpr(JdbcConstants.MYSQL, "?/?", (double) -1D, 0));
    }

    public void test_double_zero_2() throws Exception {
        assertEquals(Double.NaN,
                SQLEvalVisitorUtils.evalExpr(JdbcConstants.MYSQL, "?/?", (double) 0D, 0));
    }

    public void test_double_null() throws Exception {
        assertEquals(null, SQLEvalVisitorUtils.evalExpr(JdbcConstants.MYSQL, "?/?", (double) 1, null));
    }

    public void test_double_null_1() throws Exception {
        assertEquals(null, SQLEvalVisitorUtils.evalExpr(JdbcConstants.MYSQL, "?/?", null, (double) 1));
    }

    //
    public void test_float_zero() throws Exception {
        assertEquals(Float.POSITIVE_INFINITY,
                SQLEvalVisitorUtils.evalExpr(JdbcConstants.MYSQL, "?/?", (float) 1, 0));
    }

    public void test_float_zero_1() throws Exception {
        assertEquals(Float.NEGATIVE_INFINITY,
                SQLEvalVisitorUtils.evalExpr(JdbcConstants.MYSQL, "?/?", (float) -1F, 0));
    }

    public void test_float_zero_2() throws Exception {
        assertEquals(Float.NaN,
                SQLEvalVisitorUtils.evalExpr(JdbcConstants.MYSQL, "?/?", (float) 0F, 0));
    }

    public void test_float_null() throws Exception {
        assertEquals(null, SQLEvalVisitorUtils.evalExpr(JdbcConstants.MYSQL, "?/?", (float) 1, null));
    }

    public void test_float_null_1() throws Exception {
        assertEquals(null, SQLEvalVisitorUtils.evalExpr(JdbcConstants.MYSQL, "?/?", null, (float) 1));
    }
}
