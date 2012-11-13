package com.alibaba.druid.bvt.sql.eval;

import junit.framework.Assert;
import junit.framework.TestCase;

import com.alibaba.druid.sql.visitor.SQLEvalVisitorUtils;
import com.alibaba.druid.util.JdbcConstants;


public class EvalMethodAtan2Test extends TestCase {
    public void test_reverse() throws Exception {
        Assert.assertEquals(Math.atan2(-2, 2), SQLEvalVisitorUtils.evalExpr(JdbcConstants.MYSQL, "atan2(-2, 2)"));
    }
}
