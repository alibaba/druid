package com.alibaba.druid.bvt.sql;

import junit.framework.TestCase;

import org.junit.Assert;

import com.alibaba.druid.sql.dialect.oracle.ast.clause.GroupingSetExpr;

public class EqualTest_groupSet extends TestCase {

    public void test_eq() throws Exception {
        GroupingSetExpr exprA = new GroupingSetExpr();
        GroupingSetExpr exprB = new GroupingSetExpr();
        Assert.assertEquals(exprA.hashCode(), exprB.hashCode());
        Assert.assertEquals(exprA, exprB);
    }
}
