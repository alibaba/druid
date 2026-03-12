package com.alibaba.druid.bvt.sql;

import com.alibaba.druid.sql.ast.expr.SQLDateExpr;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class EqualTest_OracleDate {
    @Test
    public void test_eq() throws Exception {
        SQLDateExpr exprA = new SQLDateExpr();
        SQLDateExpr exprB = new SQLDateExpr();
        assertEquals(exprA.hashCode(), exprB.hashCode());
        assertEquals(exprA, exprB);
    }
}
