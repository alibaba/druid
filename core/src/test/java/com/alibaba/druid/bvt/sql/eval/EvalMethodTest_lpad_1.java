package com.alibaba.druid.bvt.sql.eval;

import com.alibaba.druid.sql.visitor.SQLEvalVisitorUtils;
import com.alibaba.druid.util.JdbcConstants;
import junit.framework.TestCase;

public class EvalMethodTest_lpad_1 extends TestCase {
    public void test_method() throws Exception {
        assertEquals("h", SQLEvalVisitorUtils.evalExpr(JdbcConstants.MYSQL, "LPAD('hi',1,'??')"));
    }
}
