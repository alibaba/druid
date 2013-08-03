package com.alibaba.druid.bvt.sql.eval;

import org.junit.Assert;
import junit.framework.TestCase;

import com.alibaba.druid.sql.visitor.SQLEvalVisitorUtils;
import com.alibaba.druid.util.JdbcConstants;


public class EvalMethodReverseTest extends TestCase {
    public void test_reverse() throws Exception {
        Assert.assertEquals("cba", SQLEvalVisitorUtils.evalExpr(JdbcConstants.MYSQL, "REVERSE('abc')"));
    }
}
