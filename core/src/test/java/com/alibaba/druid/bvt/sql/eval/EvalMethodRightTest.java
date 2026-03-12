package com.alibaba.druid.bvt.sql.eval;

import com.alibaba.druid.sql.visitor.SQLEvalVisitorUtils;
import com.alibaba.druid.util.JdbcConstants;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class EvalMethodRightTest {
    @Test
    public void test_ascii() throws Exception {
        assertEquals("rbar", SQLEvalVisitorUtils.evalExpr(JdbcConstants.MYSQL, "right('foobarbar', 4)"));
    }
}
