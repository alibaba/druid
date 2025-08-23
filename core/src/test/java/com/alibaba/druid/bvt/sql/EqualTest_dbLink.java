package com.alibaba.druid.bvt.sql;

import com.alibaba.druid.sql.ast.expr.SQLDbLinkExpr;
import junit.framework.TestCase;

import static org.junit.Assert.*;

import com.alibaba.druid.sql.dialect.oracle.parser.OracleExprParser;

public class EqualTest_dbLink extends TestCase {
    public void test_eq() throws Exception {
        String sql = "a@b";
        String sql_c = "a@c";
        SQLDbLinkExpr exprA, exprB, exprC;
        {
            OracleExprParser parser = new OracleExprParser(sql);
            exprA = (SQLDbLinkExpr) parser.expr();
        }
        {
            OracleExprParser parser = new OracleExprParser(sql);
            exprB = (SQLDbLinkExpr) parser.expr();
        }
        {
            OracleExprParser parser = new OracleExprParser(sql_c);
            exprC = (SQLDbLinkExpr) parser.expr();
        }
        assertEquals(exprA, exprB);
        assertNotEquals(exprA, exprC);
        assertTrue(exprA.equals(exprA));
        assertFalse(exprA.equals(new Object()));
        assertEquals(exprA.hashCode(), exprB.hashCode());

        assertEquals(new SQLDbLinkExpr(), new SQLDbLinkExpr());
        assertEquals(new SQLDbLinkExpr().hashCode(), new SQLDbLinkExpr().hashCode());
    }
}
