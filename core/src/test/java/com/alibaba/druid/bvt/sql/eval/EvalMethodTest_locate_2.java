package com.alibaba.druid.bvt.sql.eval;

import com.alibaba.druid.sql.visitor.SQLEvalVisitorUtils;
import com.alibaba.druid.util.JdbcConstants;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class EvalMethodTest_locate_2 {
    @Test
    public void test_method() throws Exception {
        assertEquals(7, SQLEvalVisitorUtils.evalExpr(JdbcConstants.MYSQL, "LOCATE('bar', 'foobarbar', 5)"));
    }
}
