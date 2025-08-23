package com.alibaba.druid.bvt.sql;

import com.alibaba.druid.sql.ast.expr.SQLMethodInvokeExpr;
import junit.framework.TestCase;

import static org.junit.Assert.*;

import com.alibaba.druid.sql.dialect.oracle.parser.OracleExprParser;

public class EqualTest_extract_oracle extends TestCase {
    public void test_exits() throws Exception {
        String sql = "EXTRACT(MONTH FROM x)";
        String sql_c = "EXTRACT(MONTH FROM 7)";
        SQLMethodInvokeExpr exprA, exprB, exprC;
        {
            OracleExprParser parser = new OracleExprParser(sql);
            exprA = (SQLMethodInvokeExpr) parser.expr();
        }
        {
            OracleExprParser parser = new OracleExprParser(sql);
            exprB = (SQLMethodInvokeExpr) parser.expr();
        }
        {
            OracleExprParser parser = new OracleExprParser(sql_c);
            exprC = (SQLMethodInvokeExpr) parser.expr();
        }
        assertEquals(exprA, exprB);
        assertNotEquals(exprA, exprC);
        assertTrue(exprA.equals(exprA));
        assertFalse(exprA.equals(new Object()));
        assertEquals(exprA.hashCode(), exprB.hashCode());

        assertEquals(new SQLMethodInvokeExpr(), new SQLMethodInvokeExpr());
        assertEquals(new SQLMethodInvokeExpr().hashCode(), new SQLMethodInvokeExpr().hashCode());
    }
}
