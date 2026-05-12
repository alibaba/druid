package com.alibaba.druid.bvt.sql.eval;

import com.alibaba.druid.sql.visitor.SQLEvalVisitorUtils;
import com.alibaba.druid.util.JdbcConstants;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class EvalBetweenTest {
    @Test
    public void test_between() throws Exception {
        assertEquals(false, SQLEvalVisitorUtils.evalExpr(JdbcConstants.MYSQL, "? between 1 and 3", 0));
        assertEquals(true, SQLEvalVisitorUtils.evalExpr(JdbcConstants.MYSQL, "? between 1 and 3", 2));
        assertEquals(false, SQLEvalVisitorUtils.evalExpr(JdbcConstants.MYSQL, "? between 1 and 3", 4));
    }

    @Test
    public void test_not_between() throws Exception {
        assertEquals(true, SQLEvalVisitorUtils.evalExpr(JdbcConstants.MYSQL, "? not between 1 and 3", 0));
        assertEquals(false, SQLEvalVisitorUtils.evalExpr(JdbcConstants.MYSQL, "? not between 1 and 3", 2));
        assertEquals(true, SQLEvalVisitorUtils.evalExpr(JdbcConstants.MYSQL, "? not between 1 and 3", 4));
    }
}
