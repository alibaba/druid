package com.alibaba.druid.bvt.sql;

import junit.framework.TestCase;

import org.junit.Assert;

import com.alibaba.druid.sql.dialect.oracle.ast.expr.OracleTimestampExpr;
import com.alibaba.druid.sql.dialect.oracle.parser.OracleExprParser;

public class EqualTest_OracleTimestampExpr extends TestCase {

    public void test_exits() throws Exception {
        String sql = "TIMESTAMP '' AT TIME ZONE ''";
        String sql_c = "TIMESTAMP '' AT TIME ZONE 'a'";
        OracleTimestampExpr exprA, exprB, exprC;
        {
            OracleExprParser parser = new OracleExprParser(sql);
            exprA = (OracleTimestampExpr) parser.expr();
        }
        {
            OracleExprParser parser = new OracleExprParser(sql);
            exprB = (OracleTimestampExpr) parser.expr();
        }
        {
            OracleExprParser parser = new OracleExprParser(sql_c);
            exprC = (OracleTimestampExpr) parser.expr();
        }
        Assert.assertEquals(exprA, exprB);
        Assert.assertNotEquals(exprA, exprC);
        Assert.assertTrue(exprA.equals(exprA));
        Assert.assertFalse(exprA.equals(new Object()));
        Assert.assertEquals(exprA.hashCode(), exprB.hashCode());
        
        Assert.assertEquals(new OracleTimestampExpr(), new OracleTimestampExpr());
        Assert.assertEquals(new OracleTimestampExpr().hashCode(), new OracleTimestampExpr().hashCode());
    }
}
