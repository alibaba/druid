package com.alibaba.druid.bvt.sql.eval;

import com.alibaba.druid.sql.visitor.SQLEvalVisitorUtils;
import com.alibaba.druid.util.JdbcConstants;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class EvalMethodAtan2Test {
    @Test
    public void test_reverse() throws Exception {
        assertEquals(Math.atan2(-2, 2), SQLEvalVisitorUtils.evalExpr(JdbcConstants.MYSQL, "atan2(-2, 2)"));
    }

    @Test
    public void test_error() throws Exception {
        Exception error = null;
        try {
            SQLEvalVisitorUtils.evalExpr(JdbcConstants.MYSQL, "atan2()", 12L);
        } catch (Exception e) {
            error = e;
        }
        assertNotNull(error);
    }

    @Test
    public void test_error_1() throws Exception {
        Exception error = null;
        try {
            SQLEvalVisitorUtils.evalExpr(JdbcConstants.MYSQL, "atan2(a)");
        } catch (Exception e) {
            error = e;
        }
        assertNotNull(error);
    }
}
