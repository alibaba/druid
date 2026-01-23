package com.alibaba.druid.bvt.sql.eval;

import com.alibaba.druid.sql.visitor.SQLEvalVisitorUtils;
import com.alibaba.druid.util.JdbcConstants;
import junit.framework.TestCase;

public class EvalMethodTest_locate_1 extends TestCase {
    public void test_method() throws Exception {
        assertEquals(0, SQLEvalVisitorUtils.evalExpr(JdbcConstants.MYSQL, "LOCATE('xbar', 'foobar')"));
    }
}
