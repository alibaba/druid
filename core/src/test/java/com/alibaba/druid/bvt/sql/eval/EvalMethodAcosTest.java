package com.alibaba.druid.bvt.sql.eval;

import com.alibaba.druid.sql.visitor.SQLEvalVisitorUtils;
import com.alibaba.druid.util.JdbcConstants;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class EvalMethodAcosTest {
    @Test
    public void test_reverse() throws Exception {
        assertEquals(0.0D, SQLEvalVisitorUtils.evalExpr(JdbcConstants.MYSQL, "acos(1)"));
        assertEquals(null, SQLEvalVisitorUtils.evalExpr(JdbcConstants.MYSQL, "acos(1.001)"));
        assertEquals(Math.acos(0), SQLEvalVisitorUtils.evalExpr(JdbcConstants.MYSQL, "acos(0)"));
    }

    @Test
    public void test_abs_error() throws Exception {
        Exception error = null;
        try {
            SQLEvalVisitorUtils.evalExpr(JdbcConstants.MYSQL, "acos()", 12L);
        } catch (Exception e) {
            error = e;
        }
        assertNotNull(error);
    }

    @Test
    public void test_abs_error_1() throws Exception {
        Exception error = null;
        try {
            SQLEvalVisitorUtils.evalExpr(JdbcConstants.MYSQL, "acos(a)");
        } catch (Exception e) {
            error = e;
        }
        assertNotNull(error);
    }
}
