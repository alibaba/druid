package com.alibaba.druid.bvt.sql.eval;

import com.alibaba.druid.sql.visitor.SQLEvalVisitorUtils;
import com.alibaba.druid.util.JdbcConstants;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class EvalMethodAsciiTest {
    @Test
    public void test_ascii() throws Exception {
        assertEquals(50, SQLEvalVisitorUtils.evalExpr(JdbcConstants.MYSQL, "ascii('2')"));
    }
}
