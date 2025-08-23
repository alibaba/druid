package com.alibaba.druid.bvt.sql;

import junit.framework.TestCase;

import static org.junit.Assert.*;

import com.alibaba.druid.sql.ast.SQLOrderBy;
import com.alibaba.druid.sql.ast.statement.SQLSelectOrderByItem;
import com.alibaba.druid.sql.parser.SQLExprParser;

public class EqualTest_orderBy extends TestCase {
    public void test_exits() throws Exception {
        String sql = "ORDER BY f1";
        String sql_c = "ORDER BY f2";
        SQLOrderBy exprA, exprB, exprC;
        {
            SQLExprParser parser = new SQLExprParser(sql);
            exprA = (SQLOrderBy) parser.parseOrderBy();
        }
        {
            SQLExprParser parser = new SQLExprParser(sql);
            exprB = (SQLOrderBy) parser.parseOrderBy();
        }
        {
            SQLExprParser parser = new SQLExprParser(sql_c);
            exprC = (SQLOrderBy) parser.parseOrderBy();
        }
        assertEquals(exprA, exprB);
        assertNotEquals(exprA, exprC);
        assertTrue(exprA.equals(exprA));
        assertFalse(exprA.equals(new Object()));
        assertEquals(exprA.hashCode(), exprB.hashCode());

        assertEquals(new SQLOrderBy(), new SQLOrderBy());
        assertEquals(new SQLOrderBy().hashCode(), new SQLOrderBy().hashCode());

        assertEquals(new SQLSelectOrderByItem(), new SQLSelectOrderByItem());
        assertEquals(new SQLSelectOrderByItem().hashCode(), new SQLSelectOrderByItem().hashCode());
    }
}
