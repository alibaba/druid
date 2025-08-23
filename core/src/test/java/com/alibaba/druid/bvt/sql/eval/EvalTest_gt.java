package com.alibaba.druid.bvt.sql.eval;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;

import junit.framework.TestCase;

import static org.junit.Assert.*;

import com.alibaba.druid.sql.visitor.SQLEvalVisitorUtils;
import com.alibaba.druid.util.JdbcConstants;

public class EvalTest_gt extends TestCase {
    public void test_long() throws Exception {
        assertEquals(false, SQLEvalVisitorUtils.evalExpr(JdbcConstants.MYSQL, "? > ?", (long) 1, (byte) 2));
    }

    public void test_int() throws Exception {
        assertEquals(false, SQLEvalVisitorUtils.evalExpr(JdbcConstants.MYSQL, "? > ?", (int) 1, (byte) 2));
    }

    public void test_short() throws Exception {
        assertEquals(false, SQLEvalVisitorUtils.evalExpr(JdbcConstants.MYSQL, "? > ?", (short) 1, (byte) 2));
    }

    public void test_byte() throws Exception {
        assertEquals(false, SQLEvalVisitorUtils.evalExpr(JdbcConstants.MYSQL, "? > ?", (byte) 1, (byte) 2));
    }

    public void test_BigInteger() throws Exception {
        assertEquals(false, SQLEvalVisitorUtils.evalExpr(JdbcConstants.MYSQL, "?>?", BigInteger.ONE, (byte) 2));
    }

    public void test_BigDecimal() throws Exception {
        assertEquals(false, SQLEvalVisitorUtils.evalExpr(JdbcConstants.MYSQL, "?>?", BigDecimal.ONE, (byte) 2));
    }

    public void test_float() throws Exception {
        assertEquals(false, SQLEvalVisitorUtils.evalExpr(JdbcConstants.MYSQL, "?>?", (float) 1, (byte) 2));
    }

    public void test_double() throws Exception {
        assertEquals(false, SQLEvalVisitorUtils.evalExpr(JdbcConstants.MYSQL, "?>?", (double) 1, (byte) 2));
    }

    public void test_String() throws Exception {
        assertEquals(false, SQLEvalVisitorUtils.evalExpr(JdbcConstants.MYSQL, "?>?", "1", "2"));
    }

    public void test_Date() throws Exception {
        assertEquals(false,
                SQLEvalVisitorUtils.evalExpr(JdbcConstants.MYSQL, "?>?",
                        new Date(System.currentTimeMillis() - 10),
                        new Date(System.currentTimeMillis())));
    }
}
