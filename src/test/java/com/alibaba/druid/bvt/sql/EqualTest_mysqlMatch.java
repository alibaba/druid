package com.alibaba.druid.bvt.sql;

import junit.framework.TestCase;

import org.junit.Assert;

import com.alibaba.druid.sql.dialect.mysql.ast.expr.MySqlMatchAgainstExpr;
import com.alibaba.druid.sql.dialect.mysql.parser.MySqlExprParser;

public class EqualTest_mysqlMatch extends TestCase {

    public void test_exits() throws Exception {
        String sql = "MATCH (f1, f2) AGAINST (f3 IN BOOLEAN MODE)";
        String sql_c = "MATCH (f1, f2) AGAINST (f4 IN BOOLEAN MODE)";
        MySqlMatchAgainstExpr exprA, exprB, exprC;
        {
            MySqlExprParser parser = new MySqlExprParser(sql);
            exprA = (MySqlMatchAgainstExpr) parser.expr();
        }
        {
            MySqlExprParser parser = new MySqlExprParser(sql);
            exprB = (MySqlMatchAgainstExpr) parser.expr();
        }
        {
            MySqlExprParser parser = new MySqlExprParser(sql_c);
            exprC = (MySqlMatchAgainstExpr) parser.expr();
        }
        Assert.assertEquals(exprA, exprB);
        Assert.assertNotEquals(exprA, exprC);
        Assert.assertTrue(exprA.equals(exprA));
        Assert.assertFalse(exprA.equals(new Object()));
        Assert.assertEquals(exprA.hashCode(), exprB.hashCode());

        Assert.assertEquals(new MySqlMatchAgainstExpr(), new MySqlMatchAgainstExpr());
        Assert.assertEquals(new MySqlMatchAgainstExpr().hashCode(), new MySqlMatchAgainstExpr().hashCode());
   
        exprA.setColumns(null);
        exprB.setColumns(null);
        Assert.assertEquals(exprA, exprB);
        Assert.assertNotEquals(exprA, exprC);
        Assert.assertTrue(exprA.equals(exprA));
        Assert.assertFalse(exprA.equals(new Object()));
        Assert.assertEquals(exprA.hashCode(), exprB.hashCode());
    }
}
