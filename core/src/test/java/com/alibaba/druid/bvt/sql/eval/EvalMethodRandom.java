package com.alibaba.druid.bvt.sql.eval;

import junit.framework.TestCase;

import static org.junit.Assert.*;

import com.alibaba.druid.sql.visitor.SQLEvalVisitorUtils;
import com.alibaba.druid.util.JdbcConstants;

public class EvalMethodRandom extends TestCase {
    public void test_reverse() throws Exception {
        assertNotNull(SQLEvalVisitorUtils.evalExpr(JdbcConstants.MYSQL, "rand()"));
    }
}
