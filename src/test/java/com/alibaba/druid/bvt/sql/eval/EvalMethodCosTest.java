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
    
    public void test_error() throws Exception {
        Exception error = null;
        try {
            SQLEvalVisitorUtils.evalExpr(JdbcConstants.MYSQL, "cos()", 12L);
        } catch (Exception e) {
            error = e;
        }
        Assert.assertNotNull(error);
    }
    
    public void test_error_1() throws Exception {
        Exception error = null;
        try {
            SQLEvalVisitorUtils.evalExpr(JdbcConstants.MYSQL, "cos(a)");
        } catch (Exception e) {
            error = e;
        }
        Assert.assertNotNull(error);
    }
}
