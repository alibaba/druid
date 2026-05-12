package com.alibaba.druid.bvt.sql.eval;

import com.alibaba.druid.sql.visitor.SQLEvalVisitorUtils;
import com.alibaba.druid.util.JdbcConstants;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class EvalAndOrTest {
    @Test
    public void test_and() throws Exception {
        assertEquals(false, SQLEvalVisitorUtils.evalExpr(JdbcConstants.MYSQL, "?>0 && ?>0", 1, 0));
        assertEquals(true, SQLEvalVisitorUtils.evalExpr(JdbcConstants.MYSQL, "2>1 && 100>10"));
    }

    @Test
    public void test_or() throws Exception {
        assertEquals(true, SQLEvalVisitorUtils.evalExpr(JdbcConstants.MYSQL, "10>0 || 2>0"));
        assertEquals(true, SQLEvalVisitorUtils.evalExpr(JdbcConstants.MYSQL, "10>0 || 2<0"));
        assertEquals(false, SQLEvalVisitorUtils.evalExpr(JdbcConstants.MYSQL, "2>10 || 100<10"));
    }
}
