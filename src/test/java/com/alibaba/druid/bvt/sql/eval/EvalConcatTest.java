package com.alibaba.druid.bvt.sql.eval;

import junit.framework.TestCase;

import org.junit.Assert;

import com.alibaba.druid.sql.visitor.SQLEvalVisitorUtils;
import com.alibaba.druid.util.JdbcConstants;


public class EvalConcatTest extends TestCase {
    public void test_concat() throws Exception {
        Assert.assertEquals("abcd", SQLEvalVisitorUtils.evalExpr(JdbcConstants.MYSQL, "concat(?, ?)", "ab", "cd"));
        Assert.assertEquals("abcdef", SQLEvalVisitorUtils.evalExpr(JdbcConstants.MYSQL, "concat(?, ?, ?)", "ab", "cd", "ef"));
    }
}
