package com.alibaba.druid.bvt.sql;

import com.alibaba.druid.sql.ast.expr.SQLGroupingSetExpr;
import junit.framework.TestCase;

public class EqualTest_groupSet extends TestCase {
    public void test_eq() throws Exception {
        SQLGroupingSetExpr exprA = new SQLGroupingSetExpr();
        SQLGroupingSetExpr exprB = new SQLGroupingSetExpr();
        assertEquals(exprA.hashCode(), exprB.hashCode());
        assertEquals(exprA, exprB);
    }
}
