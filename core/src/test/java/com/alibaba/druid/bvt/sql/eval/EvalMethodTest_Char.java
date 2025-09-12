package com.alibaba.druid.bvt.sql.eval;

import com.alibaba.druid.sql.visitor.SQLEvalVisitorUtils;
import com.alibaba.druid.util.JdbcConstants;
import junit.framework.TestCase;

public class EvalMethodTest_Char extends TestCase {
    public void test_reverse() throws Exception {
        assertEquals("MySQL", SQLEvalVisitorUtils.evalExpr(JdbcConstants.MYSQL, "CHAR(77,121,83,81,'76')"));
    }
}
