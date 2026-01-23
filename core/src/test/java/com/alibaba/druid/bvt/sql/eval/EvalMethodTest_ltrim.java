package com.alibaba.druid.bvt.sql.eval;

import com.alibaba.druid.sql.visitor.SQLEvalVisitorUtils;
import com.alibaba.druid.util.JdbcConstants;
import junit.framework.TestCase;

public class EvalMethodTest_ltrim extends TestCase {
    public void test_method() throws Exception {
        assertEquals("barbar", SQLEvalVisitorUtils.evalExpr(JdbcConstants.MYSQL, "LTRIM('  barbar')"));
    }
}
