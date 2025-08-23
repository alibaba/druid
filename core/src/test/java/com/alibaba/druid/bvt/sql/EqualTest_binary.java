package com.alibaba.druid.bvt.sql;

import junit.framework.TestCase;

import static org.junit.Assert.*;

import com.alibaba.druid.sql.ast.expr.SQLBinaryExpr;
import com.alibaba.druid.sql.dialect.mysql.parser.MySqlExprParser;
import com.alibaba.druid.sql.parser.SQLExprParser;

public class EqualTest_binary extends TestCase {
    public void test_exits() throws Exception {
        String sql = "b'01010'";
        String sql_c = "b'010101'";
        SQLBinaryExpr exprA, exprB, exprC;
        {
            SQLExprParser parser = new MySqlExprParser(sql);
            exprA = (SQLBinaryExpr) parser.expr();
        }
        {
            SQLExprParser parser = new MySqlExprParser(sql);
            exprB = (SQLBinaryExpr) parser.expr();
        }
        {
            SQLExprParser parser = new MySqlExprParser(sql_c);
            exprC = (SQLBinaryExpr) parser.expr();
        }
        assertEquals(exprA, exprB);
        assertNotEquals(exprA, exprC);
        assertTrue(exprA.equals(exprA));
        assertFalse(exprA.equals(new Object()));
        assertEquals(exprA.hashCode(), exprB.hashCode());

        assertEquals(new SQLBinaryExpr(), new SQLBinaryExpr());
        assertEquals(new SQLBinaryExpr().hashCode(), new SQLBinaryExpr().hashCode());
    }
}
