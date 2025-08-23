package com.alibaba.druid.bvt.sql;

import junit.framework.TestCase;

import static org.junit.Assert.*;

import com.alibaba.druid.sql.ast.expr.SQLDateExpr;

public class EqualTest_OracleDate extends TestCase {
    public void test_eq() throws Exception {
        SQLDateExpr exprA = new SQLDateExpr();
        SQLDateExpr exprB = new SQLDateExpr();
        assertEquals(exprA.hashCode(), exprB.hashCode());
        assertEquals(exprA, exprB);
    }
}
