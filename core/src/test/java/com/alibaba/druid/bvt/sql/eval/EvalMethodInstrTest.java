package com.alibaba.druid.bvt.sql.eval;

import com.alibaba.druid.sql.visitor.SQLEvalVisitorUtils;
import com.alibaba.druid.util.JdbcConstants;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class EvalMethodInstrTest {
    @Test
    public void test_length() throws Exception {
        assertEquals(4, SQLEvalVisitorUtils.evalExpr(JdbcConstants.MYSQL, "instr('foobarbar', 'bar')"));
        assertEquals(0, SQLEvalVisitorUtils.evalExpr(JdbcConstants.MYSQL, "instr('xbar', 'foobar')"));
    }
}
