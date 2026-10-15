package com.alibaba.druid.bvt.sql.eval;

import com.alibaba.druid.sql.visitor.SQLEvalVisitorUtils;
import com.alibaba.druid.util.JdbcConstants;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class EvalMethodTest_lpad {
    @Test
    public void test_method() throws Exception {
        assertEquals("??hi", SQLEvalVisitorUtils.evalExpr(JdbcConstants.MYSQL, "LPAD('hi',4,'??')"));
    }
}
