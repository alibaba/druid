package com.alibaba.druid.bvt.sql;

import com.alibaba.druid.sql.ast.expr.SQLBinaryOpExpr;
import com.alibaba.druid.sql.dialect.oracle.parser.OracleExprParser;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class EqualTest_binaryOp {
    @Test
    public void test_exits() throws Exception {
        String sql = "a > b";
        String sql_c = "a > 2";
        SQLBinaryOpExpr exprA, exprB, exprC;
        {
            OracleExprParser parser = new OracleExprParser(sql);
            exprA = (SQLBinaryOpExpr) parser.expr();
        }
        {
            OracleExprParser parser = new OracleExprParser(sql);
            exprB = (SQLBinaryOpExpr) parser.expr();
        }
        {
            OracleExprParser parser = new OracleExprParser(sql_c);
            exprC = (SQLBinaryOpExpr) parser.expr();
        }
        assertEquals(exprA, exprB);
        assertNotEquals(exprA, exprC);
        assertTrue(exprA.equals(exprA));
        assertFalse(exprA.equals(new Object()));
        assertEquals(exprA.hashCode(), exprB.hashCode());

        assertEquals(new SQLBinaryOpExpr(), new SQLBinaryOpExpr());
        assertEquals(new SQLBinaryOpExpr().hashCode(), new SQLBinaryOpExpr().hashCode());
    }
}
