package com.alibaba.druid.bvt.sql.eval;

import com.alibaba.druid.sql.visitor.SQLEvalVisitorUtils;
import com.alibaba.druid.util.JdbcConstants;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

public class EvalTest_lt_false {
    @Test
    public void test_long() throws Exception {
        assertEquals(false, SQLEvalVisitorUtils.evalExpr(JdbcConstants.MYSQL, "? < ?", (long) 10, (byte) 2));
    }

    @Test
    public void test_int() throws Exception {
        assertEquals(false, SQLEvalVisitorUtils.evalExpr(JdbcConstants.MYSQL, "? < ?", (int) 10, (byte) 2));
    }

    @Test
    public void test_short() throws Exception {
        assertEquals(false, SQLEvalVisitorUtils.evalExpr(JdbcConstants.MYSQL, "? < ?", (short) 10, (byte) 2));
    }

    @Test
    public void test_byte() throws Exception {
        assertEquals(false, SQLEvalVisitorUtils.evalExpr(JdbcConstants.MYSQL, "? < ?", (byte) 10, (byte) 2));
    }

    @Test
    public void test_BigInteger() throws Exception {
        assertEquals(false, SQLEvalVisitorUtils.evalExpr(JdbcConstants.MYSQL, "?<?", BigInteger.TEN, (byte) 2));
    }

    @Test
    public void test_BigDecimal() throws Exception {
        assertEquals(false, SQLEvalVisitorUtils.evalExpr(JdbcConstants.MYSQL, "?<?", BigDecimal.TEN, (byte) 2));
    }

    @Test
    public void test_float() throws Exception {
        assertEquals(false, SQLEvalVisitorUtils.evalExpr(JdbcConstants.MYSQL, "?<?", (float) 3, (byte) 2));
    }

    @Test
    public void test_double() throws Exception {
        assertEquals(false, SQLEvalVisitorUtils.evalExpr(JdbcConstants.MYSQL, "?<?", (double) 3, (byte) 2));
    }

    @Test
    public void test_String() throws Exception {
        assertEquals(false, SQLEvalVisitorUtils.evalExpr(JdbcConstants.MYSQL, "?<?", "3", "2"));
    }

    @Test
    public void test_Date() throws Exception {
        assertEquals(false,
                SQLEvalVisitorUtils.evalExpr(JdbcConstants.MYSQL, "?<?",
                        new Date(System.currentTimeMillis()),
                        new Date(System.currentTimeMillis() - 10)));
    }
}
