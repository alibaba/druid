package com.alibaba.druid.bvt.sql.eval;

import org.junit.Assert;
import junit.framework.TestCase;

import com.alibaba.druid.sql.visitor.SQLEvalVisitorUtils;
import com.alibaba.druid.util.JdbcConstants;


public class EvalMethodTrimTest extends TestCase {
    public void test_trim() throws Exception {
        Assert.assertEquals("bar", SQLEvalVisitorUtils.evalExpr(JdbcConstants.MYSQL, "TRIM('  bar   ')"));
    }
}
