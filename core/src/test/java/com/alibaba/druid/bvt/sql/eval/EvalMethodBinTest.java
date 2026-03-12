package com.alibaba.druid.bvt.sql.eval;

import com.alibaba.druid.sql.visitor.SQLEvalVisitorUtils;
import com.alibaba.druid.util.JdbcConstants;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class EvalMethodBinTest {
    @Test
    public void test_reverse() throws Exception {
        assertEquals(32, SQLEvalVisitorUtils.evalExpr(JdbcConstants.MYSQL, "BIT_LENGTH('text')"));
    }
}
