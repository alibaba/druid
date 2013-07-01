package com.alibaba.druid.bvt.sql.eval;

import junit.framework.TestCase;

import org.junit.Assert;

import com.alibaba.druid.sql.visitor.SQLEvalVisitorUtils;
import com.alibaba.druid.util.JdbcConstants;


public class EvalTest_add_long extends TestCase {
    public void test_add() throws Exception {
        Assert.assertEquals(3L, SQLEvalVisitorUtils.evalExpr(JdbcConstants.MYSQL, "? + ?", (long) 1, (byte) 2));
    }
    
    public void test_add_1() throws Exception {
        Assert.assertEquals(3L, SQLEvalVisitorUtils.evalExpr(JdbcConstants.MYSQL, "? + ?", (long) 1, "2"));
    }
    
    public void test_add_2() throws Exception {
        Assert.assertEquals(null, SQLEvalVisitorUtils.evalExpr(JdbcConstants.MYSQL, "? + ?", (long) 1, null));
    }
    
    public void test_add_3() throws Exception {
        Assert.assertEquals(3L, SQLEvalVisitorUtils.evalExpr(JdbcConstants.MYSQL, "? + ?", (byte) 2, (long) 1));
    }
    
    public void test_add_4() throws Exception {
        Assert.assertEquals(3L, SQLEvalVisitorUtils.evalExpr(JdbcConstants.MYSQL, "? + ?", "2", (long) 1));
    }
    
    public void test_add_5() throws Exception {
        Assert.assertEquals(null, SQLEvalVisitorUtils.evalExpr(JdbcConstants.MYSQL, "? + ?", null, (long) 1));
    }
}
