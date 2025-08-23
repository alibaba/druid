package com.alibaba.druid.bvt.sql;

import junit.framework.TestCase;

import static org.junit.Assert.*;

import com.alibaba.druid.sql.ast.expr.SQLAggregateExpr;
import com.alibaba.druid.sql.parser.SQLExprParser;

public class EqualTest_aggreate_over extends TestCase {
    public void test_exits() throws Exception {
        String sql = "count(*) OVER (ORDER BY f1)";
        String sql_c = "count(id) OVER (ORDER BY f2)";
        SQLAggregateExpr exprA, exprB, exprC;
        {
            SQLExprParser parser = new SQLExprParser(sql);
            exprA = (SQLAggregateExpr) parser.expr();
        }
        {
            SQLExprParser parser = new SQLExprParser(sql);
            exprB = (SQLAggregateExpr) parser.expr();
        }
        {
            SQLExprParser parser = new SQLExprParser(sql_c);
            exprC = (SQLAggregateExpr) parser.expr();
        }
        assertEquals(exprA, exprB);
        assertNotEquals(exprA, exprC);
        assertTrue(exprA.equals(exprA));
        assertFalse(exprA.equals(new Object()));
        assertEquals(exprA.hashCode(), exprB.hashCode());

        assertEquals(new SQLAggregateExpr(null), new SQLAggregateExpr(null));
        assertEquals(new SQLAggregateExpr(null).hashCode(), new SQLAggregateExpr(null).hashCode());
    }
}
