package com.alibaba.druid.bvt.sql.eval;

import com.alibaba.druid.sql.visitor.SQLEvalVisitorUtils;
import com.alibaba.druid.util.JdbcConstants;
import junit.framework.TestCase;

public class EvalMethodTest_insert_2 extends TestCase {
    public void test_method() throws Exception {
        assertEquals("QuWhat",
                SQLEvalVisitorUtils.evalExpr(JdbcConstants.MYSQL, "INSERT('Quadratic', 3, 100, 'What')"));
    }
}
