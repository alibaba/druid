package com.alibaba.druid.bvt.sql.eval;

import org.junit.Assert;
import junit.framework.TestCase;

import com.alibaba.druid.sql.visitor.SQLEvalVisitorUtils;
import com.alibaba.druid.util.JdbcConstants;


public class EvalMethodModTest extends TestCase {
    public void test_reverse() throws Exception {
        Assert.assertEquals(2, SQLEvalVisitorUtils.evalExpr(JdbcConstants.MYSQL, "MOD(29,9)"));
    }
}
