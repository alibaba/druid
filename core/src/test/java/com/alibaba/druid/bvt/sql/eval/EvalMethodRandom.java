package com.alibaba.druid.bvt.sql.eval;

import com.alibaba.druid.sql.visitor.SQLEvalVisitorUtils;
import com.alibaba.druid.util.JdbcConstants;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class EvalMethodRandom {
    @Test
    public void test_reverse() throws Exception {
        assertNotNull(SQLEvalVisitorUtils.evalExpr(JdbcConstants.MYSQL, "rand()"));
    }
}
