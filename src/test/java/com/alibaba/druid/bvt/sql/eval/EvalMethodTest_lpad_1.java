package com.alibaba.druid.bvt.sql.eval;

import junit.framework.TestCase;

import org.junit.Assert;

import com.alibaba.druid.sql.visitor.SQLEvalVisitorUtils;
import com.alibaba.druid.util.JdbcConstants;

public class EvalMethodTest_lpad_1 extends TestCase {

    public void test_method() throws Exception {
        Assert.assertEquals("h", SQLEvalVisitorUtils.evalExpr(JdbcConstants.MYSQL, "LPAD('hi',1,'??')"));
    }
}
