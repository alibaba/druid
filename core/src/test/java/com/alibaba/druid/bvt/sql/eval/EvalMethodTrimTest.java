package com.alibaba.druid.bvt.sql.eval;

import com.alibaba.druid.sql.visitor.SQLEvalVisitorUtils;
import com.alibaba.druid.util.JdbcConstants;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class EvalMethodTrimTest {
    @Test
    public void test_trim() throws Exception {
        assertEquals("bar", SQLEvalVisitorUtils.evalExpr(JdbcConstants.MYSQL, "TRIM('  bar   ')"));
    }
}
