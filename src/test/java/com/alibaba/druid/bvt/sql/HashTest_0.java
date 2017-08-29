package com.alibaba.druid.bvt.sql;

import com.alibaba.druid.sql.ast.expr.SQLIdentifierExpr;
import junit.framework.TestCase;

public class HashTest_0 extends TestCase {
    public void test_issue() throws Exception {
        assertEquals(new SQLIdentifierExpr("a").nameHashCode64()
                , new SQLIdentifierExpr("A").nameHashCode64());;
    }

    public void test_issue_1() throws Exception {
        assertEquals(new SQLIdentifierExpr("a").nameHashCode64()
                , new SQLIdentifierExpr("`A`").nameHashCode64());;
    }

    public void test_issue_2() throws Exception {
        assertEquals(new SQLIdentifierExpr("\"a\"").nameHashCode64()
                , new SQLIdentifierExpr("`A`").nameHashCode64());;
    }
}
