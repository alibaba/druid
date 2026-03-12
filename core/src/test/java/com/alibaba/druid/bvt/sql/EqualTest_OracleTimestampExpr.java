package com.alibaba.druid.bvt.sql;

import com.alibaba.druid.sql.ast.expr.SQLTimestampExpr;
import com.alibaba.druid.sql.dialect.oracle.parser.OracleExprParser;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class EqualTest_OracleTimestampExpr {
    @Test
    public void test_exits() throws Exception {
        String sql = "TIMESTAMP '' AT TIME ZONE ''";
        String sql_c = "TIMESTAMP '' AT TIME ZONE 'a'";
        SQLTimestampExpr exprA, exprB, exprC;
        {
            OracleExprParser parser = new OracleExprParser(sql);
            exprA = (SQLTimestampExpr) parser.expr();
        }
        {
            OracleExprParser parser = new OracleExprParser(sql);
            exprB = (SQLTimestampExpr) parser.expr();
        }
        {
            OracleExprParser parser = new OracleExprParser(sql_c);
            exprC = (SQLTimestampExpr) parser.expr();
        }
        assertEquals(exprA, exprB);
        assertNotEquals(exprA, exprC);
        assertTrue(exprA.equals(exprA));
        assertFalse(exprA.equals(new Object()));
        assertEquals(exprA.hashCode(), exprB.hashCode());

        assertEquals(new SQLTimestampExpr(), new SQLTimestampExpr());
        assertEquals(new SQLTimestampExpr().hashCode(), new SQLTimestampExpr().hashCode());
    }
}
