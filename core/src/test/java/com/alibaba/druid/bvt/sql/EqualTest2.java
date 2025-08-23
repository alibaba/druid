package com.alibaba.druid.bvt.sql;

import junit.framework.TestCase;

import static org.junit.Assert.*;

import com.alibaba.druid.sql.dialect.oracle.ast.expr.OracleIntervalExpr;
import com.alibaba.druid.sql.dialect.oracle.parser.OracleExprParser;

public class EqualTest2 extends TestCase {
    public void test_exits() throws Exception {
        String sql = "INTERVAL '30.12345' SECOND(2, 4)";
        String sql_c = "INTERVAL '30.12345' SECOND(2, 3)";
        OracleIntervalExpr exprA, exprB, exprC;
        {
            OracleExprParser parser = new OracleExprParser(sql);
            exprA = (OracleIntervalExpr) parser.expr();
        }
        {
            OracleExprParser parser = new OracleExprParser(sql);
            exprB = (OracleIntervalExpr) parser.expr();
        }
        {
            OracleExprParser parser = new OracleExprParser(sql_c);
            exprC = (OracleIntervalExpr) parser.expr();
        }
        assertEquals(exprA, exprB);
        assertNotEquals(exprA, exprC);
        assertTrue(exprA.equals(exprA));
        assertFalse(exprA.equals(new Object()));
        assertEquals(exprA.hashCode(), exprB.hashCode());
    }
}
