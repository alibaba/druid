package com.alibaba.druid.bvt.sql;

import junit.framework.TestCase;

import org.junit.Assert;

import com.alibaba.druid.sql.ast.expr.SQLGroupingSetExpr;

public class EqualTest_groupSet extends TestCase {

    public void test_eq() throws Exception {
        SQLGroupingSetExpr exprA = new SQLGroupingSetExpr();
        SQLGroupingSetExpr exprB = new SQLGroupingSetExpr();
        Assert.assertEquals(exprA.hashCode(), exprB.hashCode());
        Assert.assertEquals(exprA, exprB);
    }
}
