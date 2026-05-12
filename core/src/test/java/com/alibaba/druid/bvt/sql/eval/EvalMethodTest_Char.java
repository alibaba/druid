package com.alibaba.druid.bvt.sql.eval;

import com.alibaba.druid.sql.visitor.SQLEvalVisitorUtils;
import com.alibaba.druid.util.JdbcConstants;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class EvalMethodTest_Char {
    @Test
    public void test_reverse() throws Exception {
        assertEquals("MySQL", SQLEvalVisitorUtils.evalExpr(JdbcConstants.MYSQL, "CHAR(77,121,83,81,'76')"));
    }
}
