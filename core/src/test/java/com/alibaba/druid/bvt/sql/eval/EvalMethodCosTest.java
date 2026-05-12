package com.alibaba.druid.bvt.sql.eval;

import com.alibaba.druid.sql.visitor.SQLEvalVisitorUtils;
import com.alibaba.druid.util.JdbcConstants;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class EvalMethodCosTest {
    @Test
    public void test_reverse() throws Exception {
        assertEquals(Math.cos(1), SQLEvalVisitorUtils.evalExpr(JdbcConstants.MYSQL, "cos(1)"));
        assertEquals(Math.cos(1.001), SQLEvalVisitorUtils.evalExpr(JdbcConstants.MYSQL, "cos(1.001)"));
        assertEquals(Math.cos(0), SQLEvalVisitorUtils.evalExpr(JdbcConstants.MYSQL, "cos(0)"));
    }

    @Test
    public void test_error() throws Exception {
        Exception error = null;
        try {
            SQLEvalVisitorUtils.evalExpr(JdbcConstants.MYSQL, "cos()", 12L);
        } catch (Exception e) {
            error = e;
        }
        assertNotNull(error);
    }

    @Test
    public void test_error_1() throws Exception {
        Exception error = null;
        try {
            SQLEvalVisitorUtils.evalExpr(JdbcConstants.MYSQL, "cos(a)");
        } catch (Exception e) {
            error = e;
        }
        assertNotNull(error);
    }
}
