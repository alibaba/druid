package com.alibaba.druid.bvt.sql.eval;

import com.alibaba.druid.sql.visitor.SQLEvalVisitorUtils;
import com.alibaba.druid.util.JdbcConstants;

import junit.framework.Assert;
import junit.framework.TestCase;


public class EvalIsNullTest extends TestCase {
    public void test_null() throws Exception {
        Assert.assertEquals(false, SQLEvalVisitorUtils.evalExpr(JdbcConstants.MYSQL, "? is null", 0));
        Assert.assertEquals(true, SQLEvalVisitorUtils.evalExpr(JdbcConstants.MYSQL, "? is null", (Object) null));
    }
}
