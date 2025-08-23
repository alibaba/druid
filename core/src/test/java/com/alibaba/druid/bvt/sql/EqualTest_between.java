package com.alibaba.druid.bvt.sql;

import junit.framework.TestCase;

import static org.junit.Assert.*;

import com.alibaba.druid.sql.ast.expr.SQLBetweenExpr;
import com.alibaba.druid.sql.dialect.oracle.parser.OracleExprParser;

public class EqualTest_between extends TestCase {
    public void test_exits() throws Exception {
        String sql = "x between 1 AND 2";
        String sql_c = "x between 1 AND 3";
        SQLBetweenExpr exprA, exprB, exprC;
        {
            OracleExprParser parser = new OracleExprParser(sql);
            exprA = (SQLBetweenExpr) parser.expr();
        }
        {
            OracleExprParser parser = new OracleExprParser(sql);
            exprB = (SQLBetweenExpr) parser.expr();
        }
        {
            OracleExprParser parser = new OracleExprParser(sql_c);
            exprC = (SQLBetweenExpr) parser.expr();
        }
        assertEquals(exprA, exprB);
        assertNotEquals(exprA, exprC);
        assertTrue(exprA.equals(exprA));
        assertFalse(exprA.equals(new Object()));
        assertEquals(exprA.hashCode(), exprB.hashCode());

        assertEquals(new SQLBetweenExpr(), new SQLBetweenExpr());
        assertEquals(new SQLBetweenExpr().hashCode(), new SQLBetweenExpr().hashCode());
    }
}
