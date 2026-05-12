package com.alibaba.druid.bvt.sql.eval;

import com.alibaba.druid.sql.visitor.SQLEvalVisitorUtils;
import com.alibaba.druid.util.JdbcConstants;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class EvalMethodPITest {
    @Test
    public void test_reverse() throws Exception {
        assertEquals(Math.PI, SQLEvalVisitorUtils.evalExpr(JdbcConstants.MYSQL, "pi()"));
    }
}
