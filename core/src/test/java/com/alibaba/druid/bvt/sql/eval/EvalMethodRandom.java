package com.alibaba.druid.bvt.sql.eval;

import com.alibaba.druid.sql.visitor.SQLEvalVisitorUtils;
import com.alibaba.druid.util.JdbcConstants;
import junit.framework.TestCase;

public class EvalMethodRandom extends TestCase {
    public void test_reverse() throws Exception {
        assertNotNull(SQLEvalVisitorUtils.evalExpr(JdbcConstants.MYSQL, "rand()"));
    }
}
