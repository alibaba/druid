package com.alibaba.druid.bvt.sql.eval;

import com.alibaba.druid.sql.visitor.SQLEvalVisitorUtils;
import com.alibaba.druid.util.JdbcConstants;
import junit.framework.TestCase;

public class EvalMethodBinTest extends TestCase {
    public void test_reverse() throws Exception {
        assertEquals(32, SQLEvalVisitorUtils.evalExpr(JdbcConstants.MYSQL, "BIT_LENGTH('text')"));
    }
}
