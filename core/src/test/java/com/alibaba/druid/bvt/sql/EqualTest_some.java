package com.alibaba.druid.bvt.sql;

import junit.framework.TestCase;

import static org.junit.Assert.*;

import com.alibaba.druid.sql.ast.expr.SQLSomeExpr;
import com.alibaba.druid.sql.dialect.oracle.parser.OracleExprParser;

public class EqualTest_some extends TestCase {
    public void test_exits() throws Exception {
        String sql = "some(select id from t)";
        String sql_c = "some(select id from t1)";
        SQLSomeExpr exprA, exprB, exprC;
        {
            OracleExprParser parser = new OracleExprParser(sql);
            exprA = (SQLSomeExpr) parser.expr();
        }
        {
            OracleExprParser parser = new OracleExprParser(sql);
            exprB = (SQLSomeExpr) parser.expr();
        }
        {
            OracleExprParser parser = new OracleExprParser(sql_c);
            exprC = (SQLSomeExpr) parser.expr();
        }
        assertEquals(exprA, exprB);
        assertNotEquals(exprA, exprC);
        assertTrue(exprA.equals(exprA));
        assertFalse(exprA.equals(new Object()));
        assertEquals(exprA.hashCode(), exprB.hashCode());

        assertEquals(new SQLSomeExpr(), new SQLSomeExpr());
        assertEquals(new SQLSomeExpr().hashCode(), new SQLSomeExpr().hashCode());
    }
}
