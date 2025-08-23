package com.alibaba.druid.bvt.sql.eval;

import static org.junit.Assert.*;
import junit.framework.TestCase;

import com.alibaba.druid.sql.visitor.SQLEvalVisitorUtils;
import com.alibaba.druid.util.JdbcConstants;

public class EvalInTest extends TestCase {
    public void test_in() throws Exception {
        assertEquals(false, SQLEvalVisitorUtils.evalExpr(JdbcConstants.MYSQL, "? IN (1, 2, 3)", 0));
        assertEquals(true, SQLEvalVisitorUtils.evalExpr(JdbcConstants.MYSQL, "? IN (1, 2, 3)", 1));
    }

    public void test_not_in() throws Exception {
        assertEquals(true, SQLEvalVisitorUtils.evalExpr(JdbcConstants.MYSQL, "? NOT IN (1, 2, 3)", 0));
        assertEquals(false, SQLEvalVisitorUtils.evalExpr(JdbcConstants.MYSQL, "? NOT IN (1, 2, 3)", 1));
    }
}
