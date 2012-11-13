package com.alibaba.druid.bvt.sql.eval;

import junit.framework.Assert;
import junit.framework.TestCase;

import com.alibaba.druid.sql.visitor.SQLEvalVisitorUtils;
import com.alibaba.druid.util.JdbcConstants;


public class EvalMethodAsinTest extends TestCase {
    public void test_reverse() throws Exception {
        Assert.assertEquals(Math.asin(0.2), SQLEvalVisitorUtils.evalExpr(JdbcConstants.MYSQL, "asin(0.2)"));
    }
}
