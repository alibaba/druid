package com.alibaba.druid.bvt.sql.eval;

import com.alibaba.druid.sql.visitor.SQLEvalVisitorUtils;
import com.alibaba.druid.util.JdbcConstants;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class EvalTest_add_short {
    @Test
    public void test_byte() throws Exception {
        assertEquals(3, SQLEvalVisitorUtils.evalExpr(JdbcConstants.MYSQL, "? + ?", (short) 1, (byte) 2));
    }

    @Test
    public void test_byte_1() throws Exception {
        assertEquals(3, SQLEvalVisitorUtils.evalExpr(JdbcConstants.MYSQL, "? + ?", (short) 1, "2"));
    }

    @Test
    public void test_byte_2() throws Exception {
        assertEquals(null, SQLEvalVisitorUtils.evalExpr(JdbcConstants.MYSQL, "? + ?", (short) 1, null));
    }

    @Test
    public void test_byte_3() throws Exception {
        assertEquals(3, SQLEvalVisitorUtils.evalExpr(JdbcConstants.MYSQL, "? + ?", "2", (short) 1));
    }
}
