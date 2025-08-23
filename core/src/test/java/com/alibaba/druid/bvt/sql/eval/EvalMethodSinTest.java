package com.alibaba.druid.bvt.sql.eval;

import junit.framework.TestCase;

import static org.junit.Assert.*;

import com.alibaba.druid.sql.visitor.SQLEvalVisitorUtils;
import com.alibaba.druid.util.JdbcConstants;

public class EvalMethodSinTest extends TestCase {
    public void test_reverse() throws Exception {
        assertEquals(Math.sin(1), SQLEvalVisitorUtils.evalExpr(JdbcConstants.MYSQL, "sin(1)"));
        assertEquals(Math.sin(1.001), SQLEvalVisitorUtils.evalExpr(JdbcConstants.MYSQL, "sin(1.001)"));
        assertEquals(Math.sin(0), SQLEvalVisitorUtils.evalExpr(JdbcConstants.MYSQL, "sin(0)"));
        assertEquals(Math.sin(2), SQLEvalVisitorUtils.evalExpr(JdbcConstants.MYSQL, "sin(2)"));
    }

    public void test_error() throws Exception {
        Exception error = null;
        try {
            SQLEvalVisitorUtils.evalExpr(JdbcConstants.MYSQL, "sin()", 12L);
        } catch (Exception e) {
            error = e;
        }
        assertNotNull(error);
    }

    public void test_error_1() throws Exception {
        Exception error = null;
        try {
            SQLEvalVisitorUtils.evalExpr(JdbcConstants.MYSQL, "sin(a)");
        } catch (Exception e) {
            error = e;
        }
        assertNotNull(error);
    }
}
