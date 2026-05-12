package com.alibaba.druid.bvt.sql.eval;

import com.alibaba.druid.sql.visitor.SQLEvalVisitorUtils;
import com.alibaba.druid.util.JdbcConstants;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class EvalMethodLeftTest {
    @Test
    public void test_ascii() throws Exception {
        assertEquals("fooba", SQLEvalVisitorUtils.evalExpr(JdbcConstants.MYSQL, "LEFT('foobarbar', 5)"));
    }
}
