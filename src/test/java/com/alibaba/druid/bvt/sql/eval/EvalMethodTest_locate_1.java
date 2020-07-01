package com.alibaba.druid.bvt.sql.eval;

import junit.framework.TestCase;

import org.junit.Assert;

import com.alibaba.druid.sql.visitor.SQLEvalVisitorUtils;
import com.alibaba.druid.util.JdbcConstants;

public class EvalMethodTest_locate_1 extends TestCase {

    public void test_method() throws Exception {
        Assert.assertEquals(0, SQLEvalVisitorUtils.evalExpr(JdbcConstants.MYSQL, "LOCATE('xbar', 'foobar')"));
    }
}
