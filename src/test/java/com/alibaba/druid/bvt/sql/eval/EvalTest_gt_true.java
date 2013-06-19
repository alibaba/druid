package com.alibaba.druid.bvt.sql.eval;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;

import junit.framework.TestCase;

import org.junit.Assert;

import com.alibaba.druid.sql.visitor.SQLEvalVisitorUtils;
import com.alibaba.druid.util.JdbcConstants;

public class EvalTest_gt_true extends TestCase {

    public void test_long() throws Exception {
        Assert.assertEquals(true, SQLEvalVisitorUtils.evalExpr(JdbcConstants.MYSQL, "? > ?", (long) 10, (byte) 2));
    }

    public void test_int() throws Exception {
        Assert.assertEquals(true, SQLEvalVisitorUtils.evalExpr(JdbcConstants.MYSQL, "? > ?", (int) 10, (byte) 2));
    }

    public void test_short() throws Exception {
        Assert.assertEquals(true, SQLEvalVisitorUtils.evalExpr(JdbcConstants.MYSQL, "? > ?", (short) 10, (byte) 2));
    }

    public void test_byte() throws Exception {
        Assert.assertEquals(true, SQLEvalVisitorUtils.evalExpr(JdbcConstants.MYSQL, "? > ?", (byte) 10, (byte) 2));
    }

    public void test_BigInteger() throws Exception {
        Assert.assertEquals(true, SQLEvalVisitorUtils.evalExpr(JdbcConstants.MYSQL, "?>?", BigInteger.TEN, (byte) 2));
    }

    public void test_BigDecimal() throws Exception {
        Assert.assertEquals(true, SQLEvalVisitorUtils.evalExpr(JdbcConstants.MYSQL, "?>?", BigDecimal.TEN, (byte) 2));
    }

    public void test_float() throws Exception {
        Assert.assertEquals(true, SQLEvalVisitorUtils.evalExpr(JdbcConstants.MYSQL, "?>?", (float) 3, (byte) 2));
    }

    public void test_double() throws Exception {
        Assert.assertEquals(true, SQLEvalVisitorUtils.evalExpr(JdbcConstants.MYSQL, "?>?", (double) 3, (byte) 2));
    }

    public void test_String() throws Exception {
        Assert.assertEquals(true, SQLEvalVisitorUtils.evalExpr(JdbcConstants.MYSQL, "?>?", "3", "2"));
    }

    public void test_Date() throws Exception {
        Assert.assertEquals(true,
                            SQLEvalVisitorUtils.evalExpr(JdbcConstants.MYSQL, "?>?",
                                                         new Date(System.currentTimeMillis()),
                                                         new Date(System.currentTimeMillis() - 10)));
    }
}
