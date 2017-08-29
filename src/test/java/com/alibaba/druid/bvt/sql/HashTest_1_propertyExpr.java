package com.alibaba.druid.bvt.sql;

import com.alibaba.druid.sql.ast.expr.SQLIdentifierExpr;
import com.alibaba.druid.sql.ast.expr.SQLPropertyExpr;
import com.alibaba.druid.util.FNVUtils;
import junit.framework.TestCase;

public class HashTest_1_propertyExpr extends TestCase {
    public void test_issue() throws Exception {
        assertEquals(new SQLPropertyExpr("t","a").name_hash_lower()
                , new SQLPropertyExpr("t","A").name_hash_lower());;
    }

    public void test_issue_1() throws Exception {
        assertEquals(new SQLPropertyExpr("t","a").name_hash_lower()
                , new SQLPropertyExpr("t","`A`").name_hash_lower());;
    }

    public void test_issue_2() throws Exception {
        assertEquals(new SQLPropertyExpr("t","\"a\"").name_hash_lower()
                , new SQLPropertyExpr("t","`A`").name_hash_lower());;
    }

    public void test_issue_3() throws Exception {
        assertEquals(new SQLPropertyExpr("ESCROW","HT_TASK_TRADE_HISTORY_NEW").name_hash_lower()
                , new SQLPropertyExpr("\"ESCROW\"","\"HT_TASK_TRADE_HISTORY_NEW\"").name_hash_lower());;
    }

    public void test_issue_4() throws Exception {
        assertEquals(new SQLPropertyExpr("ESCROW","HT_TASK_TRADE_HISTORY_NEW").hashCode64()
                , new SQLPropertyExpr("\"ESCROW\"","\"HT_TASK_TRADE_HISTORY_NEW\"").hashCode64());;
    }

    public void test_issue_5() throws Exception {
        assertEquals(
                FNVUtils.fnv_64_lower("a.b")
                , FNVUtils.fnv_64_lower(new SQLPropertyExpr("\"a\"","\"b\"")));;
    }

    public void test_issue_6() throws Exception {
        assertEquals(
                FNVUtils.fnv_64_lower("ESCROW.HT_TASK_TRADE_HISTORY_NEW")
                , FNVUtils.fnv_64_lower(new SQLPropertyExpr("\"ESCROW\"","\"HT_TASK_TRADE_HISTORY_NEW\"")));;
    }

    //ESCROW.HT_TASK_TRADE_HISTORY_NEW
}
