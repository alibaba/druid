package com.alibaba.druid.bvt.sql;

import junit.framework.TestCase;

import org.junit.Assert;

import com.alibaba.druid.sql.ast.expr.SQLUnaryExpr;
import com.alibaba.druid.sql.dialect.oracle.parser.OracleExprParser;

public class EqualTest_unary extends TestCase {

    public void test_exits() throws Exception {
        String sql = "-a";
        String sql_c = "-(a+1 + +(b+1))";
        SQLUnaryExpr exprA, exprB, exprC;
        {
            OracleExprParser parser = new OracleExprParser(sql);
            exprA = (SQLUnaryExpr) parser.expr();
        }
        {
            OracleExprParser parser = new OracleExprParser(sql);
            exprB = (SQLUnaryExpr) parser.expr();
        }
        {
            OracleExprParser parser = new OracleExprParser(sql_c);
            exprC = (SQLUnaryExpr) parser.expr();
        }
        Assert.assertEquals(exprA, exprB);
        Assert.assertNotEquals(exprA, exprC);
        Assert.assertTrue(exprA.equals(exprA));
        Assert.assertFalse(exprA.equals(new Object()));
        Assert.assertEquals(exprA.hashCode(), exprB.hashCode());
        
        Assert.assertEquals(new SQLUnaryExpr(), new SQLUnaryExpr());
        Assert.assertEquals(new SQLUnaryExpr().hashCode(), new SQLUnaryExpr().hashCode());
    }
}
