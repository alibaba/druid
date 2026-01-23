package com.alibaba.druid.bvt.sql.eval;

import com.alibaba.druid.sql.visitor.SQLEvalVisitorUtils;
import com.alibaba.druid.util.JdbcConstants;
import junit.framework.TestCase;

public class EvalMethodLeftTest extends TestCase {
    public void test_ascii() throws Exception {
        assertEquals("fooba", SQLEvalVisitorUtils.evalExpr(JdbcConstants.MYSQL, "LEFT('foobarbar', 5)"));
    }
}
