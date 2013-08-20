package com.alibaba.druid.bvt.sql.eval;

import org.junit.Assert;
import junit.framework.TestCase;

import com.alibaba.druid.sql.visitor.SQLEvalVisitorUtils;
import com.alibaba.druid.util.JdbcConstants;


public class EvalMethodRightTest extends TestCase {
    public void test_ascii() throws Exception {
        Assert.assertEquals("rbar", SQLEvalVisitorUtils.evalExpr(JdbcConstants.MYSQL, "right('foobarbar', 4)"));
    }
}
