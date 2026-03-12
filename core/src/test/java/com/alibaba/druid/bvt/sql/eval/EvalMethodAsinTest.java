package com.alibaba.druid.bvt.sql.eval;

import com.alibaba.druid.sql.visitor.SQLEvalVisitorUtils;
import com.alibaba.druid.util.JdbcConstants;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class EvalMethodAsinTest {
    @Test
    public void test_reverse() throws Exception {
        assertEquals(Math.asin(0.2), SQLEvalVisitorUtils.evalExpr(JdbcConstants.MYSQL, "asin(0.2)"));
    }

    @Test
    public void test_abs_error() throws Exception {
        Exception error = null;
        try {
            SQLEvalVisitorUtils.evalExpr(JdbcConstants.MYSQL, "asin()", 12L);
        } catch (Exception e) {
            error = e;
        }
        assertNotNull(error);
    }

    @Test
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
