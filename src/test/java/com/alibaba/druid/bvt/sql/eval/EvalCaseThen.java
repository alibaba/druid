package com.alibaba.druid.bvt.sql.eval;

import org.junit.Assert;
import junit.framework.TestCase;

import com.alibaba.druid.sql.visitor.SQLEvalVisitorUtils;
import com.alibaba.druid.util.JdbcConstants;


public class EvalCaseThen extends TestCase {
    public void test_eval_then() throws Exception {
        Assert.assertEquals(111, SQLEvalVisitorUtils.evalExpr(JdbcConstants.MYSQL, "case ? when 0 then 111 else 222 end", 0));
        Assert.assertEquals(222, SQLEvalVisitorUtils.evalExpr(JdbcConstants.MYSQL, "case ? when 0 then 111 else 222 end", 1));
    }
}
