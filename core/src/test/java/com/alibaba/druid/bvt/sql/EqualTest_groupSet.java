package com.alibaba.druid.bvt.sql;

import junit.framework.TestCase;

import static org.junit.Assert.*;

import com.alibaba.druid.sql.ast.expr.SQLGroupingSetExpr;

public class EqualTest_groupSet extends TestCase {
    public void test_eq() throws Exception {
        SQLGroupingSetExpr exprA = new SQLGroupingSetExpr();
        SQLGroupingSetExpr exprB = new SQLGroupingSetExpr();
        assertEquals(exprA.hashCode(), exprB.hashCode());
        assertEquals(exprA, exprB);
    }
}
