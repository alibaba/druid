package com.alibaba.druid.bvt.sql.eval;

import com.alibaba.druid.sql.visitor.SQLEvalVisitorUtils;
import com.alibaba.druid.util.JdbcConstants;
import junit.framework.TestCase;

public class EvalMethodTest_locate_2 extends TestCase {
    public void test_method() throws Exception {
        assertEquals(7, SQLEvalVisitorUtils.evalExpr(JdbcConstants.MYSQL, "LOCATE('bar', 'foobarbar', 5)"));
    }
}
