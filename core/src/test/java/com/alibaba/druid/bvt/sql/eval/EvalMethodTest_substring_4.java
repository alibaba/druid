package com.alibaba.druid.bvt.sql.eval;

import com.alibaba.druid.sql.visitor.SQLEvalVisitorUtils;
import com.alibaba.druid.util.JdbcConstants;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class EvalMethodTest_substring_4 {
    @Test
    public void test_method() throws Exception {
        assertEquals("ki",
                SQLEvalVisitorUtils.evalExpr(JdbcConstants.MYSQL, "SUBSTRING('Sakila' FROM -4 FOR 2)"));
    }
}
