package com.alibaba.druid.bvt.sql;

import junit.framework.TestCase;

import static org.junit.Assert.*;

import com.alibaba.druid.sql.ast.SQLDataTypeImpl;
import com.alibaba.druid.sql.ast.expr.SQLCastExpr;
import com.alibaba.druid.sql.dialect.oracle.parser.OracleExprParser;

public class EqualTest_cast extends TestCase {
    public void test_exits() throws Exception {
        String sql = "cast(a as varchar(50))";
        String sql_c = "cast(b as varchar(50))";
        SQLCastExpr exprA, exprB, exprC;
        {
            OracleExprParser parser = new OracleExprParser(sql);
            exprA = (SQLCastExpr) parser.expr();
        }
        {
            OracleExprParser parser = new OracleExprParser(sql);
            exprB = (SQLCastExpr) parser.expr();
        }
        {
            OracleExprParser parser = new OracleExprParser(sql_c);
            exprC = (SQLCastExpr) parser.expr();
        }
        assertEquals(exprA, exprB);
        assertNotEquals(exprA, exprC);
        assertTrue(exprA.equals(exprA));
        assertFalse(exprA.equals(new Object()));
        assertEquals(exprA.hashCode(), exprB.hashCode());

        assertEquals(new SQLCastExpr(), new SQLCastExpr());
        assertEquals(new SQLCastExpr().hashCode(), new SQLCastExpr().hashCode());

        assertEquals(new SQLDataTypeImpl(), new SQLDataTypeImpl());
        assertEquals(new SQLDataTypeImpl().hashCode(), new SQLDataTypeImpl().hashCode());
    }
}
