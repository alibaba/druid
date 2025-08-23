package com.alibaba.druid.bvt.sql.eval;

import junit.framework.TestCase;

import static org.junit.Assert.*;

import com.alibaba.druid.sql.visitor.SQLEvalVisitorUtils;
import com.alibaba.druid.util.JdbcConstants;

public class EvalMethodPowerTest extends TestCase {
    public void test_reverse() throws Exception {
        assertEquals(Math.pow(1, 2), SQLEvalVisitorUtils.evalExpr(JdbcConstants.MYSQL, "power(1, 2)"));
        assertEquals(Math.pow(3, 4), SQLEvalVisitorUtils.evalExpr(JdbcConstants.MYSQL, "power(3, 4)"));
        assertEquals(Math.pow(4, 5), SQLEvalVisitorUtils.evalExpr(JdbcConstants.MYSQL, "pow(4, 5)"));
    }

    public void test_error() throws Exception {
        Exception error = null;
        try {
            SQLEvalVisitorUtils.evalExpr(JdbcConstants.MYSQL, "pow()", 12L);
        } catch (Exception e) {
            error = e;
        }
        assertNotNull(error);
    }

    public void test_error_1() throws Exception {
        Exception error = null;
        try {
            SQLEvalVisitorUtils.evalExpr(JdbcConstants.MYSQL, "pow(a,b)");
        } catch (Exception e) {
            error = e;
        }
        assertNotNull(error);
    }
}
