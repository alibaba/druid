package com.alibaba.druid.bvt.sql;

import com.alibaba.druid.sql.ast.expr.SQLDateExpr;
import junit.framework.TestCase;

public class EqualTest_OracleDate extends TestCase {
    public void test_eq() throws Exception {
        SQLDateExpr exprA = new SQLDateExpr();
        SQLDateExpr exprB = new SQLDateExpr();
        assertEquals(exprA.hashCode(), exprB.hashCode());
        assertEquals(exprA, exprB);
    }
}
