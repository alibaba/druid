package com.alibaba.druid.bvt.sql.eval;

import com.alibaba.druid.sql.visitor.SQLEvalVisitorUtils;
import com.alibaba.druid.util.JdbcConstants;
import junit.framework.TestCase;

public class EvalMethodTest_lower extends TestCase {
    public void test_method() throws Exception {
        assertEquals("quadratically",
                SQLEvalVisitorUtils.evalExpr(JdbcConstants.MYSQL, "LOWER('QUADRATICALLY')"));
    }
}
