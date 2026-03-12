package com.alibaba.druid.bvt.sql;

import com.alibaba.druid.sql.ast.expr.SQLIdentifierExpr;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class HashTest_0 {
    @Test
    public void test_issue() throws Exception {
        assertEquals(new SQLIdentifierExpr("a").nameHashCode64(),
                new SQLIdentifierExpr("A").nameHashCode64());
    }

    @Test
    public void test_issue_1() throws Exception {
        assertEquals(new SQLIdentifierExpr("a").nameHashCode64(),
                new SQLIdentifierExpr("`A`").nameHashCode64());
    }

    @Test
    public void test_issue_2() throws Exception {
        assertEquals(new SQLIdentifierExpr("\"a\"").nameHashCode64(),
                new SQLIdentifierExpr("`A`").nameHashCode64());
    }
}
