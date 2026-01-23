package com.alibaba.druid.bvt.sql.eval;

import com.alibaba.druid.sql.visitor.SQLEvalVisitorUtils;
import com.alibaba.druid.util.JdbcConstants;
import junit.framework.TestCase;

public class EvalMethodPITest extends TestCase {
    public void test_reverse() throws Exception {
        assertEquals(Math.PI, SQLEvalVisitorUtils.evalExpr(JdbcConstants.MYSQL, "pi()"));
    }
}
