package com.alibaba.druid.bvt.sql.eval;

import junit.framework.TestCase;

import org.junit.Assert;

import com.alibaba.druid.sql.visitor.SQLEvalVisitorUtils;
import com.alibaba.druid.util.JdbcConstants;

public class EvalMethodTest_mid extends TestCase {

    public void test_method() throws Exception {
        Assert.assertEquals("ratically", SQLEvalVisitorUtils.evalExpr(JdbcConstants.MYSQL, "mid('Quadratically',5)"));
    }
}
