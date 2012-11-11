package com.alibaba.druid.bvt.sql.eval;

import junit.framework.Assert;
import junit.framework.TestCase;

import com.alibaba.druid.sql.visitor.SQLEvalVisitorUtils;
import com.alibaba.druid.util.JdbcConstants;


public class EvalSelectTest extends TestCase {
    public void test_select() throws Exception {
        Assert.assertEquals(1, SQLEvalVisitorUtils.evalExpr(JdbcConstants.MYSQL, "SELECT 1"));
    }
}
