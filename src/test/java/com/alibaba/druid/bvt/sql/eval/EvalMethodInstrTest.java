package com.alibaba.druid.bvt.sql.eval;

import org.junit.Assert;
import junit.framework.TestCase;

import com.alibaba.druid.sql.visitor.SQLEvalVisitorUtils;
import com.alibaba.druid.util.JdbcConstants;


public class EvalMethodInstrTest extends TestCase {
    public void test_length() throws Exception {
        Assert.assertEquals(4, SQLEvalVisitorUtils.evalExpr(JdbcConstants.MYSQL, "instr('foobarbar', 'bar')"));
        Assert.assertEquals(0, SQLEvalVisitorUtils.evalExpr(JdbcConstants.MYSQL, "instr('xbar', 'foobar')"));
    }
}
