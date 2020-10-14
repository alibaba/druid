package com.alibaba.druid.bvt.sql;

import com.alibaba.druid.sql.ast.expr.SQLExtractExpr;
import junit.framework.TestCase;

import org.junit.Assert;

import com.alibaba.druid.sql.dialect.mysql.parser.MySqlExprParser;

public class EqualTest_extract_mysql extends TestCase {

    public void test_exits() throws Exception {
        String sql = "EXTRACT (YEAR FROM x)";
        String sql_c = "EXTRACT (MONTH FROM y)";
        SQLExtractExpr exprA, exprB, exprC;
        {
            MySqlExprParser parser = new MySqlExprParser(sql);
            exprA = (SQLExtractExpr) parser.expr();
        }
        {
            MySqlExprParser parser = new MySqlExprParser(sql);
            exprB = (SQLExtractExpr) parser.expr();
        }
        {
            MySqlExprParser parser = new MySqlExprParser(sql_c);
            exprC = (SQLExtractExpr) parser.expr();
        }
        Assert.assertEquals(exprA, exprB);
        Assert.assertNotEquals(exprA, exprC);
        Assert.assertTrue(exprA.equals(exprA));
        Assert.assertFalse(exprA.equals(new Object()));
        Assert.assertEquals(exprA.hashCode(), exprB.hashCode());

        Assert.assertEquals(new SQLExtractExpr(), new SQLExtractExpr());
        Assert.assertEquals(new SQLExtractExpr().hashCode(), new SQLExtractExpr().hashCode());
    }
}
