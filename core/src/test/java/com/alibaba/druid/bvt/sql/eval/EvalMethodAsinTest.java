package com.alibaba.druid.bvt.sql.eval;

import junit.framework.TestCase;

import static org.junit.Assert.*;

import com.alibaba.druid.sql.visitor.SQLEvalVisitorUtils;
import com.alibaba.druid.util.JdbcConstants;

public class EvalMethodAsinTest extends TestCase {
    public void test_reverse() throws Exception {
        assertEquals(Math.asin(0.2), SQLEvalVisitorUtils.evalExpr(JdbcConstants.MYSQL, "asin(0.2)"));
    }

    public void test_abs_error() throws Exception {
        Exception error = null;
        try {
            SQLEvalVisitorUtils.evalExpr(JdbcConstants.MYSQL, "asin()", 12L);
        } catch (Exception e) {
            error = e;
        }
        assertNotNull(error);
    }

    public void test_abs_error_1() throws Exception {
        Exception error = null;
        try {
            SQLEvalVisitorUtils.evalExpr(JdbcConstants.MYSQL, "asin(a)");
        } catch (Exception e) {
            error = e;
        }
        assertNotNull(error);
    }
}
