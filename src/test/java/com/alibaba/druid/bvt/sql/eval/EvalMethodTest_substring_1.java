package com.alibaba.druid.bvt.sql.eval;

import junit.framework.TestCase;

import org.junit.Assert;

import com.alibaba.druid.sql.visitor.SQLEvalVisitorUtils;
import com.alibaba.druid.util.JdbcConstants;

public class EvalMethodTest_substring_1 extends TestCase {

    public void test_method() throws Exception {
        Assert.assertEquals("ratica",
                            SQLEvalVisitorUtils.evalExpr(JdbcConstants.MYSQL, "SUBSTRING('Quadratically',5,6)"));
    }
}
