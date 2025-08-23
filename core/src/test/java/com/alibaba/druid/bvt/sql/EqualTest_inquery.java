package com.alibaba.druid.bvt.sql;

import junit.framework.TestCase;

import static org.junit.Assert.*;

import com.alibaba.druid.sql.ast.expr.SQLInSubQueryExpr;
import com.alibaba.druid.sql.dialect.mysql.parser.MySqlExprParser;

public class EqualTest_inquery extends TestCase {
    public void test_exits() throws Exception {
        String sql = "fstate in (select state from t_status)";
        String sql_c = "fstate_c in (select state from t_status)";
        SQLInSubQueryExpr exprA, exprB, exprC;
        {
            MySqlExprParser parser = new MySqlExprParser(sql);
            exprA = (SQLInSubQueryExpr) parser.expr();
        }
        {
            MySqlExprParser parser = new MySqlExprParser(sql);
            exprB = (SQLInSubQueryExpr) parser.expr();
        }
        {
            MySqlExprParser parser = new MySqlExprParser(sql_c);
            exprC = (SQLInSubQueryExpr) parser.expr();
        }
        assertEquals(exprA, exprB);
        assertNotEquals(exprA, exprC);
        assertTrue(exprA.equals(exprA));
        assertFalse(exprA.equals(new Object()));
        assertEquals(exprA.hashCode(), exprB.hashCode());
    }
}
