package com.alibaba.druid.bvt.sql.eval;

import org.junit.Assert;
import junit.framework.TestCase;

import com.alibaba.druid.sql.visitor.SQLEvalVisitorUtils;
import com.alibaba.druid.util.JdbcConstants;


public class EvalMethodAsciiTest extends TestCase {
    public void test_ascii() throws Exception {
        Assert.assertEquals(50, SQLEvalVisitorUtils.evalExpr(JdbcConstants.MYSQL, "ascii('2')"));
    }
}
