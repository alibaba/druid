package com.alibaba.druid.bvt.sql.eval;

import com.alibaba.druid.sql.visitor.SQLEvalVisitorUtils;
import com.alibaba.druid.util.JdbcConstants;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class EvalMethodAtanTest {
    @Test
    public void test_reverse() throws Exception {
        assertEquals(Math.atan(2), SQLEvalVisitorUtils.evalExpr(JdbcConstants.MYSQL, "atan(2)"));
        assertEquals(Math.atan(-2), SQLEvalVisitorUtils.evalExpr(JdbcConstants.MYSQL, "atan(-2)"));
    }

    @Test
    public void test_abs_error() throws Exception {
        Exception error = null;
        try {
            SQLEvalVisitorUtils.evalExpr(JdbcConstants.MYSQL, "atan()", 12L);
        } catch (Exception e) {
            error = e;
        }
        assertNotNull(error);
    }

    @Test
    public void test_abs_error_1() throws Exception {
        Exception error = null;
        try {
            SQLEvalVisitorUtils.evalExpr(JdbcConstants.MYSQL, "atan(a)");
        } catch (Exception e) {
            error = e;
        }
        assertNotNull(error);
    }
}
