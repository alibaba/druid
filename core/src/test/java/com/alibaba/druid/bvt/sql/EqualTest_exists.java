package com.alibaba.druid.bvt.sql;

import com.alibaba.druid.sql.ast.expr.SQLExistsExpr;
import com.alibaba.druid.sql.dialect.oracle.parser.OracleExprParser;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class EqualTest_exists {
    @Test
    public void test_exits() throws Exception {
        String sql = "exists (select 1)";
        String sql_c = "not exists (select 1)";
        SQLExistsExpr exprA, exprB, exprC;
        {
            OracleExprParser parser = new OracleExprParser(sql);
            exprA = (SQLExistsExpr) parser.expr();
        }
        {
            OracleExprParser parser = new OracleExprParser(sql);
            exprB = (SQLExistsExpr) parser.expr();
        }
        {
            OracleExprParser parser = new OracleExprParser(sql_c);
            exprC = (SQLExistsExpr) parser.expr();
        }
        assertEquals(exprA, exprB);
        assertNotEquals(exprA, exprC);
        assertTrue(exprA.equals(exprA));
        assertFalse(exprA.equals(new Object()));
        assertEquals(exprA.hashCode(), exprB.hashCode());

        assertEquals(new SQLExistsExpr(), new SQLExistsExpr());
        assertEquals(new SQLExistsExpr().hashCode(), new SQLExistsExpr().hashCode());
    }
}
