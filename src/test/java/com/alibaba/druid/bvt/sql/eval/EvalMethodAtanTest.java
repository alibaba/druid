package com.alibaba.druid.bvt.sql.eval;

import junit.framework.TestCase;

import org.junit.Assert;

import com.alibaba.druid.sql.visitor.SQLEvalVisitorUtils;
import com.alibaba.druid.util.JdbcConstants;


public class EvalMethodAtanTest extends TestCase {
    public void test_reverse() throws Exception {
        Assert.assertEquals(Math.atan(2), SQLEvalVisitorUtils.evalExpr(JdbcConstants.MYSQL, "atan(2)"));
        Assert.assertEquals(Math.atan(-2), SQLEvalVisitorUtils.evalExpr(JdbcConstants.MYSQL, "atan(-2)"));
    }
    
    public void test_abs_error() throws Exception {
        Exception error = null;
        try {
            SQLEvalVisitorUtils.evalExpr(JdbcConstants.MYSQL, "atan()", 12L);
        } catch (Exception e) {
            error = e;
        }
        Assert.assertNotNull(error);
    }
    
    public void test_abs_error_1() throws Exception {
        Exception error = null;
        try {
            SQLEvalVisitorUtils.evalExpr(JdbcConstants.MYSQL, "atan(a)");
        } catch (Exception e) {
            error = e;
        }
        Assert.assertNotNull(error);
    }
}
