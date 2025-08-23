package com.alibaba.druid.bvt.sql;

import junit.framework.TestCase;

import static org.junit.Assert.*;

import com.alibaba.druid.sql.ast.expr.SQLIntervalExpr;
import com.alibaba.druid.sql.dialect.mysql.parser.MySqlExprParser;
import com.alibaba.druid.sql.parser.SQLExprParser;

public class EqualTest_interval_mysql extends TestCase {
    public void test_exits() throws Exception {
        String sql = "INTERVAL 3 YEAR";
        String sql_c = "INTERVAL 3 MONTH";
        SQLIntervalExpr exprA, exprB, exprC;
        {
            SQLExprParser parser = new MySqlExprParser(sql);
            exprA = (SQLIntervalExpr) parser.expr();
        }
        {
            SQLExprParser parser = new MySqlExprParser(sql);
            exprB = (SQLIntervalExpr) parser.expr();
        }
        {
            SQLExprParser parser = new MySqlExprParser(sql_c);
            exprC = (SQLIntervalExpr) parser.expr();
        }
        assertEquals(exprA, exprB);
        assertNotEquals(exprA, exprC);
        assertTrue(exprA.equals(exprA));
        assertFalse(exprA.equals(new Object()));
        assertEquals(exprA.hashCode(), exprB.hashCode());

        assertEquals(new SQLIntervalExpr(), new SQLIntervalExpr());
        assertEquals(new SQLIntervalExpr().hashCode(), new SQLIntervalExpr().hashCode());
    }
}
