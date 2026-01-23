package com.alibaba.druid.bvt.sql.eval;

import com.alibaba.druid.sql.visitor.SQLEvalVisitorUtils;
import com.alibaba.druid.util.JdbcConstants;
import junit.framework.TestCase;

public class EvalMethodBitLengthTest extends TestCase {
    public void test_reverse() throws Exception {
        assertEquals("1100", SQLEvalVisitorUtils.evalExpr(JdbcConstants.MYSQL, "BIN(12)"));
    }
}
