package com.alibaba.druid.bvt.sql.eval;

import junit.framework.TestCase;

import org.junit.Assert;

import com.alibaba.druid.sql.visitor.SQLEvalVisitorUtils;
import com.alibaba.druid.util.JdbcConstants;


public class EvalMethodBinTest extends TestCase {
    public void test_reverse() throws Exception {
        Assert.assertEquals(32, SQLEvalVisitorUtils.evalExpr(JdbcConstants.MYSQL, "BIT_LENGTH('text')"));
    }
}
