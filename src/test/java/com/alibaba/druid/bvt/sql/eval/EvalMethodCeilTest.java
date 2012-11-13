package com.alibaba.druid.bvt.sql.eval;

import junit.framework.Assert;
import junit.framework.TestCase;

import com.alibaba.druid.sql.visitor.SQLEvalVisitorUtils;
import com.alibaba.druid.util.JdbcConstants;


public class EvalMethodCeilTest extends TestCase {
    public void test_reverse() throws Exception {
        Assert.assertEquals(2, SQLEvalVisitorUtils.evalExpr(JdbcConstants.MYSQL, "ceil(1.23)"));
        Assert.assertEquals(-1, SQLEvalVisitorUtils.evalExpr(JdbcConstants.MYSQL, "ceil(-1.23)"));
        Assert.assertEquals(-1, SQLEvalVisitorUtils.evalExpr(JdbcConstants.MYSQL, "ceiling(-1.24)"));
    }
}
