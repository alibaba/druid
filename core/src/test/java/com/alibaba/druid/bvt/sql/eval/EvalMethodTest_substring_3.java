package com.alibaba.druid.bvt.sql.eval;

import com.alibaba.druid.sql.visitor.SQLEvalVisitorUtils;
import com.alibaba.druid.util.JdbcConstants;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class EvalMethodTest_substring_3 {
    @Test
    public void test_method() throws Exception {
        assertEquals("aki",
                SQLEvalVisitorUtils.evalExpr(JdbcConstants.MYSQL, "SUBSTRING('Sakila', -5, 3)"));
    }
}
