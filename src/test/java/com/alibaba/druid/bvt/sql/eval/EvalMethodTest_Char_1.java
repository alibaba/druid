package com.alibaba.druid.bvt.sql.eval;

import junit.framework.TestCase;

import org.junit.Assert;

import com.alibaba.druid.sql.visitor.SQLEvalVisitorUtils;
import com.alibaba.druid.util.JdbcConstants;


public class EvalMethodTest_Char_1 extends TestCase {
    public void test_reverse() throws Exception {
        Assert.assertEquals("MMM", SQLEvalVisitorUtils.evalExpr(JdbcConstants.MYSQL, "CHAR(77,77.3,'77.3')"));
    }
}
