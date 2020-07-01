package com.alibaba.druid.bvt.sql.eval;

import junit.framework.TestCase;

import org.junit.Assert;

import com.alibaba.druid.sql.visitor.SQLEvalVisitorUtils;
import com.alibaba.druid.util.JdbcConstants;


public class EvalMethodTanTest extends TestCase {
    public void test_reverse() throws Exception {
        Assert.assertEquals(Math.tan(1), SQLEvalVisitorUtils.evalExpr(JdbcConstants.MYSQL, "tan(1)"));
        Assert.assertEquals(Math.tan(1.001), SQLEvalVisitorUtils.evalExpr(JdbcConstants.MYSQL, "tan(1.001)"));
        Assert.assertEquals(Math.tan(0), SQLEvalVisitorUtils.evalExpr(JdbcConstants.MYSQL, "tan(0)"));
    }
    
    public void test_error() throws Exception {
        Exception error = null;
        try {
            SQLEvalVisitorUtils.evalExpr(JdbcConstants.MYSQL, "tan()", 12L);
        } catch (Exception e) {
            error = e;
        }
        Assert.assertNotNull(error);
    }

    public void test_error_1() throws Exception {
        Exception error = null;
        try {
            SQLEvalVisitorUtils.evalExpr(JdbcConstants.MYSQL, "tan(a)");
        } catch (Exception e) {
            error = e;
        }
        Assert.assertNotNull(error);
    }
}
