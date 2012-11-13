package com.alibaba.druid.bvt.sql.eval;

import junit.framework.Assert;
import junit.framework.TestCase;

import com.alibaba.druid.sql.visitor.SQLEvalVisitorUtils;
import com.alibaba.druid.util.JdbcConstants;


public class EvalMethodAbsTest extends TestCase {
    public void test_reverse() throws Exception {
        Assert.assertEquals(12, SQLEvalVisitorUtils.evalExpr(JdbcConstants.MYSQL, "abs(-12)"));
        Assert.assertEquals(12, SQLEvalVisitorUtils.evalExpr(JdbcConstants.MYSQL, "abs(12)"));
    }
}
