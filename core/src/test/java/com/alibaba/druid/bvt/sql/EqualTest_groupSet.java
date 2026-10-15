package com.alibaba.druid.bvt.sql;

import com.alibaba.druid.sql.ast.expr.SQLGroupingSetExpr;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class EqualTest_groupSet {
    @Test
    public void test_eq() throws Exception {
        SQLGroupingSetExpr exprA = new SQLGroupingSetExpr();
        SQLGroupingSetExpr exprB = new SQLGroupingSetExpr();
        assertEquals(exprA.hashCode(), exprB.hashCode());
        assertEquals(exprA, exprB);
    }
}
