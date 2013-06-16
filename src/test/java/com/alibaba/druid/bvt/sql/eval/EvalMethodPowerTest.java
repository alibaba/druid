package com.alibaba.druid.bvt.sql.eval;

import junit.framework.TestCase;

import org.junit.Assert;

import com.alibaba.druid.sql.visitor.SQLEvalVisitorUtils;
import com.alibaba.druid.util.JdbcConstants;


public class EvalMethodPowerTest extends TestCase {
    public void test_reverse() throws Exception {
        Assert.assertEquals(Math.pow(1, 2), SQLEvalVisitorUtils.evalExpr(JdbcConstants.MYSQL, "power(1, 2)"));
        Assert.assertEquals(Math.pow(3, 4), SQLEvalVisitorUtils.evalExpr(JdbcConstants.MYSQL, "power(3, 4)"));
        Assert.assertEquals(Math.pow(4, 5), SQLEvalVisitorUtils.evalExpr(JdbcConstants.MYSQL, "pow(4, 5)"));
    }
}
