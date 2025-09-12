package com.alibaba.druid.bvt.sql.eval;

import com.alibaba.druid.sql.visitor.SQLEvalVisitorUtils;
import com.alibaba.druid.util.JdbcConstants;
import junit.framework.TestCase;

public class EvalMethodTrimTest extends TestCase {
    public void test_trim() throws Exception {
        assertEquals("bar", SQLEvalVisitorUtils.evalExpr(JdbcConstants.MYSQL, "TRIM('  bar   ')"));
    }
}
