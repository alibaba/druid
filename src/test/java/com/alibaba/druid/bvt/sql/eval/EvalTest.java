package com.alibaba.druid.bvt.sql.eval;

import junit.framework.Assert;
import junit.framework.TestCase;

import com.alibaba.druid.sql.visitor.SQLEvalVisitorUtils;
import com.alibaba.druid.util.JdbcUtils;

public class EvalTest extends TestCase {

    public void testEval() throws Exception {
        Assert.assertEquals("A", SQLEvalVisitorUtils.evalExpr(JdbcUtils.MYSQL, "?", "A"));
        Assert.assertEquals(123, SQLEvalVisitorUtils.evalExpr(JdbcUtils.MYSQL, "?", 123));
    }

    public void testEval_1() throws Exception {
        Assert.assertEquals("AB", SQLEvalVisitorUtils.evalExpr(JdbcUtils.MYSQL, "? + ?", "A", "B"));
        Assert.assertEquals(234, SQLEvalVisitorUtils.evalExpr(JdbcUtils.MYSQL, "? + ?", 123, 111));
    }
}
