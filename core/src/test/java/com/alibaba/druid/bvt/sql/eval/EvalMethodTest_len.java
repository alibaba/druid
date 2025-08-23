package com.alibaba.druid.bvt.sql.eval;

import junit.framework.TestCase;

import static org.junit.Assert.*;

import com.alibaba.druid.sql.visitor.SQLEvalVisitorUtils;
import com.alibaba.druid.util.JdbcConstants;

public class EvalMethodTest_len extends TestCase {
    public void test_method() throws Exception {
        assertEquals(4, SQLEvalVisitorUtils.evalExpr(JdbcConstants.MYSQL, "LENGTH('text')"));
    }
}
