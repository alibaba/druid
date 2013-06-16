package com.alibaba.druid.bvt.sql.eval;

import junit.framework.TestCase;

import org.junit.Assert;

import com.alibaba.druid.sql.visitor.SQLEvalVisitorUtils;
import com.alibaba.druid.util.JdbcConstants;


public class EvalMethodAbsTest extends TestCase {
    public void test_abs_int() throws Exception {
        Assert.assertEquals(12, SQLEvalVisitorUtils.evalExpr(JdbcConstants.MYSQL, "abs(-12)"));
        Assert.assertEquals(12, SQLEvalVisitorUtils.evalExpr(JdbcConstants.MYSQL, "abs(12)"));
    }
    
    public void test_abs_long() throws Exception {
        Assert.assertEquals(12L, SQLEvalVisitorUtils.evalExpr(JdbcConstants.MYSQL, "abs(?)", 12L));
        Assert.assertEquals(12L, SQLEvalVisitorUtils.evalExpr(JdbcConstants.MYSQL, "abs(?)", -12L));
    }
}
