package com.alibaba.druid.bvt.sql.eval;

import com.alibaba.druid.sql.visitor.SQLEvalVisitorUtils;
import com.alibaba.druid.util.JdbcConstants;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class EvalTest_add_long {
    @Test
    public void test_add() throws Exception {
        assertEquals(3L, SQLEvalVisitorUtils.evalExpr(JdbcConstants.MYSQL, "? + ?", (long) 1, (byte) 2));
    }

    @Test
    public void test_add_1() throws Exception {
        assertEquals(3L, SQLEvalVisitorUtils.evalExpr(JdbcConstants.MYSQL, "? + ?", (long) 1, "2"));
    }

    @Test
    public void test_add_2() throws Exception {
        assertEquals(null, SQLEvalVisitorUtils.evalExpr(JdbcConstants.MYSQL, "? + ?", (long) 1, null));
    }

    @Test
    public void test_add_3() throws Exception {
        assertEquals(3L, SQLEvalVisitorUtils.evalExpr(JdbcConstants.MYSQL, "? + ?", (byte) 2, (long) 1));
    }

    @Test
    public void test_add_4() throws Exception {
        assertEquals(3L, SQLEvalVisitorUtils.evalExpr(JdbcConstants.MYSQL, "? + ?", "2", (long) 1));
    }

    @Test
    public void test_add_5() throws Exception {
        assertEquals(null, SQLEvalVisitorUtils.evalExpr(JdbcConstants.MYSQL, "? + ?", null, (long) 1));
    }
}
