package com.alibaba.druid.bvt.sql.eval;

import com.alibaba.druid.sql.visitor.SQLEvalVisitorUtils;
import com.alibaba.druid.util.JdbcConstants;
import junit.framework.TestCase;

public class EvalMethodModTest extends TestCase {
    public void test_reverse() throws Exception {
        assertEquals(2, SQLEvalVisitorUtils.evalExpr(JdbcConstants.MYSQL, "MOD(29,9)"));
    }
}
