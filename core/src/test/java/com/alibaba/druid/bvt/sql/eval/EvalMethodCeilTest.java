package com.alibaba.druid.bvt.sql.eval;

import junit.framework.TestCase;

import static org.junit.Assert.*;

import com.alibaba.druid.sql.visitor.SQLEvalVisitorUtils;
import com.alibaba.druid.util.JdbcConstants;

public class EvalMethodCeilTest extends TestCase {
    public void test_reverse() throws Exception {
        assertEquals(2, SQLEvalVisitorUtils.evalExpr(JdbcConstants.MYSQL, "ceil(1.23)"));
        assertEquals(-1, SQLEvalVisitorUtils.evalExpr(JdbcConstants.MYSQL, "ceil(-1.23)"));
        assertEquals(-1, SQLEvalVisitorUtils.evalExpr(JdbcConstants.MYSQL, "ceiling(-1.24)"));
    }

    public void test_error() throws Exception {
        Exception error = null;
        try {
            SQLEvalVisitorUtils.evalExpr(JdbcConstants.MYSQL, "ceil()", 12L);
        } catch (Exception e) {
            error = e;
        }
        assertNotNull(error);
    }

    public void test_error_1() throws Exception {
        Exception error = null;
        try {
            SQLEvalVisitorUtils.evalExpr(JdbcConstants.MYSQL, "ceil(a)");
        } catch (Exception e) {
            error = e;
        }
        assertNotNull(error);
    }
}
