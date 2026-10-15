package com.alibaba.druid.bvt.sql.eval;

import com.alibaba.druid.sql.visitor.SQLEvalVisitorUtils;
import com.alibaba.druid.util.JdbcConstants;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class EvalMethodTest_insert_2 {
    @Test
    public void test_method() throws Exception {
        assertEquals("QuWhat",
                SQLEvalVisitorUtils.evalExpr(JdbcConstants.MYSQL, "INSERT('Quadratic', 3, 100, 'What')"));
    }
}
