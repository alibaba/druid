package com.alibaba.druid.bvt.sql.eval;

import com.alibaba.druid.sql.visitor.SQLEvalVisitorUtils;
import com.alibaba.druid.util.JdbcConstants;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class EvalMethodModTest {
    @Test
    public void test_reverse() throws Exception {
        assertEquals(2, SQLEvalVisitorUtils.evalExpr(JdbcConstants.MYSQL, "MOD(29,9)"));
    }
}
