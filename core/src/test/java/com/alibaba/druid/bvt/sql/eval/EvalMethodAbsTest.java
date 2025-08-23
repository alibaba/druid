package com.alibaba.druid.bvt.sql.eval;

import java.math.BigDecimal;

import junit.framework.TestCase;

import static org.junit.Assert.*;

import com.alibaba.druid.sql.visitor.SQLEvalVisitorUtils;
import com.alibaba.druid.util.JdbcConstants;

public class EvalMethodAbsTest extends TestCase {
    public void test_abs_int() throws Exception {
        assertEquals(12, SQLEvalVisitorUtils.evalExpr(JdbcConstants.MYSQL, "abs(-12)"));
        assertEquals(12, SQLEvalVisitorUtils.evalExpr(JdbcConstants.MYSQL, "abs(12)"));
    }

    public void test_abs_long() throws Exception {
        assertEquals(12L, SQLEvalVisitorUtils.evalExpr(JdbcConstants.MYSQL, "abs(?)", 12L));
        assertEquals(12L, SQLEvalVisitorUtils.evalExpr(JdbcConstants.MYSQL, "abs(?)", -12L));
    }

    public void test_abs_decimal() throws Exception {
        assertEquals(new BigDecimal("12"), SQLEvalVisitorUtils.evalExpr(JdbcConstants.MYSQL, "abs(?)", new BigDecimal("12")));
        assertEquals(new BigDecimal("12"), SQLEvalVisitorUtils.evalExpr(JdbcConstants.MYSQL, "abs(?)", new BigDecimal("-12")));
    }

    public void test_abs_error() throws Exception {
        Exception error = null;
        try {
            SQLEvalVisitorUtils.evalExpr(JdbcConstants.MYSQL, "abs()", 12L);
        } catch (Exception e) {
            error = e;
        }
        assertNotNull(error);
    }

    public void test_abs_error_1() throws Exception {
        Exception error = null;
        try {
            SQLEvalVisitorUtils.evalExpr(JdbcConstants.MYSQL, "abs(a)");
        } catch (Exception e) {
            error = e;
        }
        assertNotNull(error);
    }
}
