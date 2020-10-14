package com.alibaba.druid.bvt.sql;

import com.alibaba.druid.sql.ast.expr.SQLMatchAgainstExpr;
import com.alibaba.druid.sql.dialect.ads.parser.AdsExprParser;
import junit.framework.TestCase;

import org.junit.Assert;

import com.alibaba.druid.sql.dialect.mysql.parser.MySqlExprParser;

public class EqualTest_mysqlMatch extends TestCase {

    public void test_exits() throws Exception {
        String sql = "MATCH (f1, f2) AGAINST (f3 IN BOOLEAN MODE)";
        String sql_c = "MATCH (f1, f2) AGAINST (f4 IN BOOLEAN MODE)";
        SQLMatchAgainstExpr exprA, exprB, exprC;
        {
            AdsExprParser parser = new AdsExprParser(sql);
            exprA = (SQLMatchAgainstExpr) parser.expr();
        }
        {
            AdsExprParser parser = new AdsExprParser(sql);
            exprB = (SQLMatchAgainstExpr) parser.expr();
        }
        {
            AdsExprParser parser = new AdsExprParser(sql_c);
            exprC = (SQLMatchAgainstExpr) parser.expr();
        }
        Assert.assertEquals(exprA, exprB);
        Assert.assertNotEquals(exprA, exprC);
        Assert.assertTrue(exprA.equals(exprA));
        Assert.assertFalse(exprA.equals(new Object()));
        Assert.assertEquals(exprA.hashCode(), exprB.hashCode());

        Assert.assertEquals(new SQLMatchAgainstExpr(), new SQLMatchAgainstExpr());
        Assert.assertEquals(new SQLMatchAgainstExpr().hashCode(), new SQLMatchAgainstExpr().hashCode());

        exprA.setColumns(null);
        exprB.setColumns(null);
        Assert.assertEquals(exprA, exprB);
        Assert.assertNotEquals(exprA, exprC);
        Assert.assertTrue(exprA.equals(exprA));
        Assert.assertFalse(exprA.equals(new Object()));
        Assert.assertEquals(exprA.hashCode(), exprB.hashCode());
    }
}
