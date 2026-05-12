package com.alibaba.druid.bvt.sql.eval;

import com.alibaba.druid.sql.visitor.SQLEvalVisitorUtils;
import com.alibaba.druid.util.JdbcConstants;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class EvalMethodBitLengthTest {
    @Test
    public void test_reverse() throws Exception {
        assertEquals("1100", SQLEvalVisitorUtils.evalExpr(JdbcConstants.MYSQL, "BIN(12)"));
    }
}
