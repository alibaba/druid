package com.alibaba.druid.bvt.sql;

import junit.framework.TestCase;

import static org.junit.Assert.*;

import com.alibaba.druid.sql.ast.statement.SQLSelectQueryBlock;

public class EqualTest_SQLSelectQueryBlock extends TestCase {
    public void test_eq() throws Exception {
        SQLSelectQueryBlock exprA = new SQLSelectQueryBlock();
        SQLSelectQueryBlock exprB = new SQLSelectQueryBlock();
        assertEquals(exprA.hashCode(), exprB.hashCode());
        assertEquals(exprA, exprB);
    }
}
