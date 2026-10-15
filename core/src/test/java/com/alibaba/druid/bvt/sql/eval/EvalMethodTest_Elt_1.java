package com.alibaba.druid.bvt.sql.eval;

import com.alibaba.druid.sql.visitor.SQLEvalVisitorUtils;
import com.alibaba.druid.util.JdbcConstants;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class EvalMethodTest_Elt_1 {
    @Test
    public void test_method() throws Exception {
        assertEquals(null,
                SQLEvalVisitorUtils.evalExpr(JdbcConstants.MYSQL, "ELT(11, 'ej', 'Heja', 'hej', 'foo')"));
    }
}
