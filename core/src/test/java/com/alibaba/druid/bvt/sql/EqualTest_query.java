package com.alibaba.druid.bvt.sql;

import junit.framework.TestCase;

import static org.junit.Assert.*;

import com.alibaba.druid.sql.ast.expr.SQLQueryExpr;
import com.alibaba.druid.sql.dialect.oracle.parser.OracleExprParser;

public class EqualTest_query extends TestCase {
    public void test_exits() throws Exception {
        String sql = "(select id from t)";
        String sql_c = "(select id from t1)";
        SQLQueryExpr exprA, exprB, exprC;
        {
            OracleExprParser parser = new OracleExprParser(sql);
            exprA = (SQLQueryExpr) parser.expr();
        }
        {
            OracleExprParser parser = new OracleExprParser(sql);
            exprB = (SQLQueryExpr) parser.expr();
        }
        {
            OracleExprParser parser = new OracleExprParser(sql_c);
            exprC = (SQLQueryExpr) parser.expr();
        }
        assertEquals(exprA, exprB);
        assertNotEquals(exprA, exprC);
        assertTrue(exprA.equals(exprA));
        assertFalse(exprA.equals(new Object()));
        assertEquals(exprA.hashCode(), exprB.hashCode());

        assertEquals(new SQLQueryExpr(), new SQLQueryExpr());
        assertEquals(new SQLQueryExpr().hashCode(), new SQLQueryExpr().hashCode());
    }
}
