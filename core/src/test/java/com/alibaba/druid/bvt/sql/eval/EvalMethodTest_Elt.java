package com.alibaba.druid.bvt.sql.eval;

import com.alibaba.druid.sql.visitor.SQLEvalVisitorUtils;
import com.alibaba.druid.util.JdbcConstants;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class EvalMethodTest_Elt {
    @Test
    public void test_method() throws Exception {
        assertEquals("ej",
                SQLEvalVisitorUtils.evalExpr(JdbcConstants.MYSQL, "ELT(1, 'ej', 'Heja', 'hej', 'foo')"));
    }
}
