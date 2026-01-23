package com.alibaba.druid.bvt.sql.eval;

import com.alibaba.druid.sql.visitor.SQLEvalVisitorUtils;
import com.alibaba.druid.util.JdbcConstants;
import junit.framework.TestCase;

public class EvalMethodAsciiTest extends TestCase {
    public void test_ascii() throws Exception {
        assertEquals(50, SQLEvalVisitorUtils.evalExpr(JdbcConstants.MYSQL, "ascii('2')"));
    }
}
