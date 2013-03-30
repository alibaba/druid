package com.alibaba.druid.bvt.sql.eval;

import junit.framework.TestCase;

import org.junit.Assert;

import com.alibaba.druid.sql.visitor.SQLEvalVisitorUtils;
import com.alibaba.druid.util.JdbcConstants;

public class EvalMethodTest_lower extends TestCase {

    public void test_method() throws Exception {
        Assert.assertEquals("quadratically",
                            SQLEvalVisitorUtils.evalExpr(JdbcConstants.MYSQL, "LOWER('QUADRATICALLY')"));
    }
}
