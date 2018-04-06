package com.alibaba.druid.bvt.sql;

import com.alibaba.druid.sql.ast.expr.SQLIdentifierExpr;
import com.alibaba.druid.sql.ast.expr.SQLPropertyExpr;
import com.alibaba.druid.util.FnvHash;
import junit.framework.TestCase;

public class HashTest_1_propertyExpr extends TestCase {
    public void test_issue() throws Exception {
        assertEquals(new SQLPropertyExpr("t","a").nameHashCode64()
                , new SQLPropertyExpr("t","A").nameHashCode64());;
    }

    public void test_issue_1() throws Exception {
        assertEquals(new SQLPropertyExpr("t","a").nameHashCode64()
                , new SQLPropertyExpr("t","`A`").nameHashCode64());;
    }

    public void test_issue_2() throws Exception {
        assertEquals(new SQLPropertyExpr("t","\"a\"").nameHashCode64()
                , new SQLPropertyExpr("t","`A`").nameHashCode64());;
    }

    public void test_issue_3() throws Exception {
        assertEquals(new SQLPropertyExpr("ESCROW","HT_TASK_TRADE_HISTORY_NEW").nameHashCode64()
                , new SQLPropertyExpr("\"ESCROW\"","\"HT_TASK_TRADE_HISTORY_NEW\"").nameHashCode64());;
    }

    public void test_issue_4() throws Exception {
        assertEquals(new SQLPropertyExpr("ESCROW","HT_TASK_TRADE_HISTORY_NEW").hashCode64()
                , new SQLPropertyExpr("\"ESCROW\"","\"HT_TASK_TRADE_HISTORY_NEW\"").hashCode64());;
    }

    public void test_issue_5() throws Exception {
        assertEquals(
                FnvHash.fnv1a_64_lower("a.b"),
                new SQLPropertyExpr("\"a\"","\"b\"")
                        .hashCode64()
        );
    }

    public void test_issue_6() throws Exception {
        assertEquals(
                FnvHash.fnv1a_64_lower("ESCROW.HT_TASK_TRADE_HISTORY_NEW"),
                new SQLPropertyExpr("\"ESCROW\"","\"HT_TASK_TRADE_HISTORY_NEW\"").hashCode64());
    }

    public void test_changeOwner() throws Exception {
        SQLIdentifierExpr table = new SQLIdentifierExpr("t1");
        SQLPropertyExpr column = new SQLPropertyExpr(table, "f0");

        assertEquals(FnvHash.hashCode64("t1"), table.hashCode64());
        assertEquals(FnvHash.hashCode64("t1.f0"), column.hashCode64());

        table.setName("t2");

        assertEquals(FnvHash.hashCode64("t2"), table.hashCode64());
        assertEquals(FnvHash.hashCode64("t2.f0"), column.hashCode64());

        column.setName("f1");

        assertEquals(FnvHash.hashCode64("t2.f1"), column.hashCode64());

    }

    //ESCROW.HT_TASK_TRADE_HISTORY_NEW
}
