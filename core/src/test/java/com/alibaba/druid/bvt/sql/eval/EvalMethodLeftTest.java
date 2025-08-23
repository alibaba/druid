package com.alibaba.druid.bvt.sql.eval;

import static org.junit.Assert.*;
import junit.framework.TestCase;

import com.alibaba.druid.sql.visitor.SQLEvalVisitorUtils;
import com.alibaba.druid.util.JdbcConstants;

public class EvalMethodLeftTest extends TestCase {
    public void test_ascii() throws Exception {
        assertEquals("fooba", SQLEvalVisitorUtils.evalExpr(JdbcConstants.MYSQL, "LEFT('foobarbar', 5)"));
    }
}
