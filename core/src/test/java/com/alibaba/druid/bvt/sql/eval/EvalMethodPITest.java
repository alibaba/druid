package com.alibaba.druid.bvt.sql.eval;

import static org.junit.Assert.*;
import junit.framework.TestCase;

import com.alibaba.druid.sql.visitor.SQLEvalVisitorUtils;
import com.alibaba.druid.util.JdbcConstants;

public class EvalMethodPITest extends TestCase {
    public void test_reverse() throws Exception {
        assertEquals(Math.PI, SQLEvalVisitorUtils.evalExpr(JdbcConstants.MYSQL, "pi()"));
    }
}
