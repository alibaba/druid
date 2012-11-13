package com.alibaba.druid.bvt.sql.eval;

import junit.framework.Assert;
import junit.framework.TestCase;

import com.alibaba.druid.sql.visitor.SQLEvalVisitorUtils;
import com.alibaba.druid.util.JdbcConstants;


public class EvalMethodAcosTest extends TestCase {
    public void test_reverse() throws Exception {
        Assert.assertEquals(0.0D, SQLEvalVisitorUtils.evalExpr(JdbcConstants.MYSQL, "acos(1)"));
        Assert.assertEquals(null, SQLEvalVisitorUtils.evalExpr(JdbcConstants.MYSQL, "acos(1.001)"));
        Assert.assertEquals(Math.acos(0), SQLEvalVisitorUtils.evalExpr(JdbcConstants.MYSQL, "acos(0)"));
    }
}
