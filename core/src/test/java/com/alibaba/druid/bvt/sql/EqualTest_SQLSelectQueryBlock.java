package com.alibaba.druid.bvt.sql;

import com.alibaba.druid.sql.ast.statement.SQLSelectQueryBlock;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class EqualTest_SQLSelectQueryBlock {
    @Test
    public void test_eq() throws Exception {
        SQLSelectQueryBlock exprA = new SQLSelectQueryBlock();
        SQLSelectQueryBlock exprB = new SQLSelectQueryBlock();
        assertEquals(exprA.hashCode(), exprB.hashCode());
        assertEquals(exprA, exprB);
    }
}
