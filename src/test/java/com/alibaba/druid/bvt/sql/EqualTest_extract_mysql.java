package com.alibaba.druid.bvt.sql;

import junit.framework.TestCase;

import org.junit.Assert;

import com.alibaba.druid.sql.dialect.mysql.ast.expr.MySqlExtractExpr;
import com.alibaba.druid.sql.dialect.mysql.parser.MySqlExprParser;

public class EqualTest_extract_mysql extends TestCase {

    public void test_exits() throws Exception {
        String sql = "EXTRACT (YEAR FROM x)";
        String sql_c = "EXTRACT (MONTH FROM y)";
        MySqlExtractExpr exprA, exprB, exprC;
        {
            MySqlExprParser parser = new MySqlExprParser(sql);
            exprA = (MySqlExtractExpr) parser.expr();
        }
        {
            MySqlExprParser parser = new MySqlExprParser(sql);
            exprB = (MySqlExtractExpr) parser.expr();
        }
        {
            MySqlExprParser parser = new MySqlExprParser(sql_c);
            exprC = (MySqlExtractExpr) parser.expr();
        }
        Assert.assertEquals(exprA, exprB);
        Assert.assertNotEquals(exprA, exprC);
        Assert.assertTrue(exprA.equals(exprA));
        Assert.assertFalse(exprA.equals(new Object()));
        Assert.assertEquals(exprA.hashCode(), exprB.hashCode());
        
        Assert.assertEquals(new MySqlExtractExpr(), new MySqlExtractExpr());
        Assert.assertEquals(new MySqlExtractExpr().hashCode(), new MySqlExtractExpr().hashCode());
    }
}
