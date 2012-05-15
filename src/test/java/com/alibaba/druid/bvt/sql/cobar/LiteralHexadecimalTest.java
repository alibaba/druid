/**
 * (created at 2011-11-11)
 */
package com.alibaba.druid.bvt.sql.cobar;

import junit.framework.Assert;
import junit.framework.TestCase;

import com.alibaba.druid.sql.ast.expr.SQLHexExpr;
import com.alibaba.druid.sql.dialect.mysql.parser.MySqlExprParser;

/**
 * @author <a href="mailto:shuo.qius@alibaba-inc.com">QIU Shuo</a>
 */
public class LiteralHexadecimalTest extends TestCase {
    public void test_0() throws Exception {
        String sql = "x'E982B1E7A195275C73'";
        SQLHexExpr hex = (SQLHexExpr) new MySqlExprParser(sql).expr();
        Assert.assertEquals("邱硕'\\s", new String(hex.toBytes(), "utf-8"));
    }

    public void test_1() throws Exception {
        String sql = "x'0D0A'";
        SQLHexExpr hex = (SQLHexExpr) new MySqlExprParser(sql).expr();
        Assert.assertEquals("\r\n", new String(hex.toBytes(), "utf-8"));
    }
    
    public void test_2() throws Exception {
        String sql = "X'4D7953514C'";
        SQLHexExpr hex = (SQLHexExpr) new MySqlExprParser(sql).expr();
        Assert.assertEquals("MySQL", new String(hex.toBytes(), "utf-8"));
    }
    
    public void test_3() throws Exception {
        String sql = "0x5061756c";
        SQLHexExpr hex = (SQLHexExpr) new MySqlExprParser(sql).expr();
        Assert.assertEquals("Paul", new String(hex.toBytes(), "utf-8"));
    }
    
    public void test_4() throws Exception {
        String sql = "0x41";
        SQLHexExpr hex = (SQLHexExpr) new MySqlExprParser(sql).expr();
        Assert.assertEquals("A", new String(hex.toBytes(), "utf-8"));
    }
    
    public void test_5() throws Exception {
        String sql = "0x636174";
        SQLHexExpr hex = (SQLHexExpr) new MySqlExprParser(sql).expr();
        Assert.assertEquals("cat", new String(hex.toBytes(), "utf-8"));
    }
}
