package com.alibaba.druid.bvt.sql.eval;

import junit.framework.TestCase;

import static org.junit.Assert.*;

import com.alibaba.druid.sql.visitor.SQLEvalVisitorUtils;
import com.alibaba.druid.util.JdbcConstants;

public class EvalMethodSqrtTest extends TestCase {
    public void test_reverse() throws Exception {
        assertEquals(Math.sqrt(1), SQLEvalVisitorUtils.evalExpr(JdbcConstants.MYSQL, "sqrt(1)"));
        assertEquals(Math.sqrt(1.001), SQLEvalVisitorUtils.evalExpr(JdbcConstants.MYSQL, "sqrt(1.001)"));
        assertEquals(Math.sqrt(0), SQLEvalVisitorUtils.evalExpr(JdbcConstants.MYSQL, "sqrt(0)"));
    }

    public void test_error() throws Exception {
        Exception error = null;
        try {
            SQLEvalVisitorUtils.evalExpr(JdbcConstants.MYSQL, "sqrt()", 12L);
        } catch (Exception e) {
            error = e;
        }
        assertNotNull(error);
    }

    public void test_error_1() throws Exception {
        Exception error = null;
        try {
            SQLEvalVisitorUtils.evalExpr(JdbcConstants.MYSQL, "sqrt(a)");
        } catch (Exception e) {
            error = e;
        }
        assertNotNull(error);
    }
}
