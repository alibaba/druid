package com.alibaba.druid.bvt.sql;

import com.alibaba.druid.sql.ast.expr.SQLIdentifierExpr;
import com.alibaba.druid.sql.ast.expr.SQLPropertyExpr;
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
}
