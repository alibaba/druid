package com.alibaba.druid.bvt.sql.eval;

import com.alibaba.druid.sql.visitor.SQLEvalVisitorUtils;
import com.alibaba.druid.util.JdbcConstants;
import junit.framework.TestCase;

public class EvalMethodRightTest extends TestCase {
    public void test_ascii() throws Exception {
        assertEquals("rbar", SQLEvalVisitorUtils.evalExpr(JdbcConstants.MYSQL, "right('foobarbar', 4)"));
    }
}
