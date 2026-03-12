package com.alibaba.druid.bvt.sql.eval;

import com.alibaba.druid.sql.visitor.SQLEvalVisitorUtils;
import com.alibaba.druid.util.JdbcConstants;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class EvalMethodCeilTest {
    @Test
    public void test_reverse() throws Exception {
        assertEquals(2, SQLEvalVisitorUtils.evalExpr(JdbcConstants.MYSQL, "ceil(1.23)"));
        assertEquals(-1, SQLEvalVisitorUtils.evalExpr(JdbcConstants.MYSQL, "ceil(-1.23)"));
        assertEquals(-1, SQLEvalVisitorUtils.evalExpr(JdbcConstants.MYSQL, "ceiling(-1.24)"));
    }

    @Test
    public void test_error() throws Exception {
        Exception error = null;
        try {
            SQLEvalVisitorUtils.evalExpr(JdbcConstants.MYSQL, "ceil()", 12L);
        } catch (Exception e) {
            error = e;
        }
        assertNotNull(error);
    }

    @Test
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
