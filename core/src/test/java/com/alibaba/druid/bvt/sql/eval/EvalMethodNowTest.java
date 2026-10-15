package com.alibaba.druid.bvt.sql.eval;

import com.alibaba.druid.sql.visitor.SQLEvalVisitorUtils;
import com.alibaba.druid.util.JdbcConstants;
import org.junit.jupiter.api.Test;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

public class EvalMethodNowTest {
    @Test
    public void test_now() throws Exception {
        assertEquals(true, SQLEvalVisitorUtils.evalExpr(JdbcConstants.MYSQL, "now()") instanceof Date);
    }
}
