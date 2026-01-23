package com.alibaba.druid.bvt.sql.eval;

import com.alibaba.druid.sql.visitor.SQLEvalVisitorUtils;
import com.alibaba.druid.util.JdbcConstants;
import junit.framework.TestCase;

public class EvalMethodTest_Char_1 extends TestCase {
    public void test_reverse() throws Exception {
        assertEquals("MMM", SQLEvalVisitorUtils.evalExpr(JdbcConstants.MYSQL, "CHAR(77,77.3,'77.3')"));
    }
}
