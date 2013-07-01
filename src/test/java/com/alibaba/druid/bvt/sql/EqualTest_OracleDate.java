package com.alibaba.druid.bvt.sql;

import junit.framework.TestCase;

import org.junit.Assert;

import com.alibaba.druid.sql.dialect.oracle.ast.expr.OracleDateExpr;

public class EqualTest_OracleDate extends TestCase {

    public void test_eq() throws Exception {
        OracleDateExpr exprA = new OracleDateExpr();
        OracleDateExpr exprB = new OracleDateExpr();
        Assert.assertEquals(exprA.hashCode(), exprB.hashCode());
        Assert.assertEquals(exprA, exprB);
    }
}
