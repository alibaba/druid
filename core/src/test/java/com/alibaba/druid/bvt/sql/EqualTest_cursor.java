package com.alibaba.druid.bvt.sql;

import junit.framework.TestCase;

import static org.junit.Assert.*;

import com.alibaba.druid.sql.dialect.oracle.ast.expr.OracleCursorExpr;
import com.alibaba.druid.sql.dialect.oracle.parser.OracleExprParser;

public class EqualTest_cursor extends TestCase {
    public void test_exits() throws Exception {
        String sql = "CURSOR(select id from t)";
        String sql_c = "CURSOR(select id from t1)";
        OracleCursorExpr exprA, exprB, exprC;
        {
            OracleExprParser parser = new OracleExprParser(sql);
            exprA = (OracleCursorExpr) parser.expr();
        }
        {
            OracleExprParser parser = new OracleExprParser(sql);
            exprB = (OracleCursorExpr) parser.expr();
        }
        {
            OracleExprParser parser = new OracleExprParser(sql_c);
            exprC = (OracleCursorExpr) parser.expr();
        }
        assertEquals(exprA, exprB);
        assertNotEquals(exprA, exprC);
        assertTrue(exprA.equals(exprA));
        assertFalse(exprA.equals(new Object()));
        assertEquals(exprA.hashCode(), exprB.hashCode());

        assertEquals(new OracleCursorExpr(), new OracleCursorExpr());
        assertEquals(new OracleCursorExpr().hashCode(), new OracleCursorExpr().hashCode());
    }
}
