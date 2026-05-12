package com.alibaba.druid.bvt.sql.eval;

import com.alibaba.druid.sql.visitor.SQLEvalVisitorUtils;
import com.alibaba.druid.util.JdbcConstants;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class EvalCaseThen {
    @Test
    public void test_eval_then() throws Exception {
        assertEquals(111, SQLEvalVisitorUtils.evalExpr(JdbcConstants.MYSQL, "case ? when 0 then 111 else 222 end", 0));
        assertEquals(222, SQLEvalVisitorUtils.evalExpr(JdbcConstants.MYSQL, "case ? when 0 then 111 else 222 end", 1));
    }
}
