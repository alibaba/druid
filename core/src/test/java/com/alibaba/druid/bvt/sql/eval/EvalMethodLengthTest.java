package com.alibaba.druid.bvt.sql.eval;

import com.alibaba.druid.sql.visitor.SQLEvalVisitorUtils;
import com.alibaba.druid.util.JdbcConstants;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class EvalMethodLengthTest {
    @Test
    public void test_length() throws Exception {
        assertEquals(4, SQLEvalVisitorUtils.evalExpr(JdbcConstants.MYSQL, "length('text')"));
    }
}
