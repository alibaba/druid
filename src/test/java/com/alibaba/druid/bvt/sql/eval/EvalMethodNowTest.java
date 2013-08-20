package com.alibaba.druid.bvt.sql.eval;

import java.util.Date;

import org.junit.Assert;
import junit.framework.TestCase;

import com.alibaba.druid.sql.visitor.SQLEvalVisitorUtils;
import com.alibaba.druid.util.JdbcConstants;


public class EvalMethodNowTest extends TestCase {
    public void test_now() throws Exception {
        Assert.assertEquals(true, SQLEvalVisitorUtils.evalExpr(JdbcConstants.MYSQL, "now()") instanceof Date);
    }
}
