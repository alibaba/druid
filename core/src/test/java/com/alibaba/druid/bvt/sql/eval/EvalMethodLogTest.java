package com.alibaba.druid.bvt.sql.eval;

import com.alibaba.druid.sql.visitor.SQLEvalVisitorUtils;
import com.alibaba.druid.util.JdbcConstants;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class EvalMethodLogTest {
    @Test
    public void test_reverse() throws Exception {
        assertEquals(Math.log(1), SQLEvalVisitorUtils.evalExpr(JdbcConstants.MYSQL, "log(1)"));
        assertEquals(Math.log(1.001), SQLEvalVisitorUtils.evalExpr(JdbcConstants.MYSQL, "log(1.001)"));
        assertEquals(Math.log(0), SQLEvalVisitorUtils.evalExpr(JdbcConstants.MYSQL, "log(0)"));
    }

    @Test
    public void test_error() throws Exception {
        Exception error = null;
        try {
            SQLEvalVisitorUtils.evalExpr(JdbcConstants.MYSQL, "log()", 12L);
        } catch (Exception e) {
            error = e;
        }
        assertNotNull(error);
    }

    @Test
    public void test_error_1() throws Exception {
        Exception error = null;
        try {
            SQLEvalVisitorUtils.evalExpr(JdbcConstants.MYSQL, "log(a)");
        } catch (Exception e) {
            error = e;
        }
        assertNotNull(error);
    }
}
