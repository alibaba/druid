package com.alibaba.druid.bvt.sql.eval;

import junit.framework.TestCase;

import org.junit.Assert;

import com.alibaba.druid.sql.visitor.SQLEvalVisitorUtils;
import com.alibaba.druid.util.JdbcConstants;


public class EvalMethodLogTest extends TestCase {
    public void test_reverse() throws Exception {
        Assert.assertEquals(Math.log(1), SQLEvalVisitorUtils.evalExpr(JdbcConstants.MYSQL, "log(1)"));
        Assert.assertEquals(Math.log(1.001), SQLEvalVisitorUtils.evalExpr(JdbcConstants.MYSQL, "log(1.001)"));
        Assert.assertEquals(Math.log(0), SQLEvalVisitorUtils.evalExpr(JdbcConstants.MYSQL, "log(0)"));
    }
}
