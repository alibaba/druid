package com.alibaba.druid.bvt.sql.eval;

import com.alibaba.druid.sql.visitor.SQLEvalVisitorUtils;
import com.alibaba.druid.util.JdbcConstants;
import junit.framework.TestCase;

public class EvalMethodReverseTest extends TestCase {
    public void test_reverse() throws Exception {
        assertEquals("cba", SQLEvalVisitorUtils.evalExpr(JdbcConstants.MYSQL, "REVERSE('abc')"));
    }
}
