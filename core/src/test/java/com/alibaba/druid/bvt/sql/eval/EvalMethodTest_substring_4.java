package com.alibaba.druid.bvt.sql.eval;

import com.alibaba.druid.sql.visitor.SQLEvalVisitorUtils;
import com.alibaba.druid.util.JdbcConstants;
import junit.framework.TestCase;

public class EvalMethodTest_substring_4 extends TestCase {
    public void test_method() throws Exception {
        assertEquals("ki",
                SQLEvalVisitorUtils.evalExpr(JdbcConstants.MYSQL, "SUBSTRING('Sakila' FROM -4 FOR 2)"));
    }
}
