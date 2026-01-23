package com.alibaba.druid.bvt.sql.eval;

import com.alibaba.druid.sql.visitor.SQLEvalVisitorUtils;
import com.alibaba.druid.util.JdbcConstants;
import junit.framework.TestCase;

public class EvalMethodInstrTest extends TestCase {
    public void test_length() throws Exception {
        assertEquals(4, SQLEvalVisitorUtils.evalExpr(JdbcConstants.MYSQL, "instr('foobarbar', 'bar')"));
        assertEquals(0, SQLEvalVisitorUtils.evalExpr(JdbcConstants.MYSQL, "instr('xbar', 'foobar')"));
    }
}
