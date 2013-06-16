package com.alibaba.druid.bvt.sql.eval;

import junit.framework.TestCase;

import org.junit.Assert;

import com.alibaba.druid.sql.visitor.SQLEvalVisitorUtils;
import com.alibaba.druid.util.JdbcConstants;


public class EvalMethodCosTest extends TestCase {
    public void test_reverse() throws Exception {
        Assert.assertEquals(Math.cos(1), SQLEvalVisitorUtils.evalExpr(JdbcConstants.MYSQL, "cos(1)"));
        Assert.assertEquals(Math.cos(1.001), SQLEvalVisitorUtils.evalExpr(JdbcConstants.MYSQL, "cos(1.001)"));
        Assert.assertEquals(Math.cos(0), SQLEvalVisitorUtils.evalExpr(JdbcConstants.MYSQL, "cos(0)"));
    }
}
