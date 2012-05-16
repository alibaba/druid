package com.alibaba.druid.bvt.sql.cobar;

import org.junit.Assert;

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.mysql.parser.MySqlStatementParser;
import com.alibaba.druid.sql.parser.Token;

import junit.framework.TestCase;


public class DMLInsertParserTest extends TestCase {
    public void testInsert_0() throws Exception {
        String sql = "insErt HIGH_PRIORITY intO test.t1 seT t1.id1=?, id2 := '123'";
        MySqlStatementParser parser = new MySqlStatementParser(sql);
        SQLStatement stmt = parser.parseStatementList().get(0);
        parser.match(Token.EOF);
        String output = SQLUtils.toMySqlString(stmt);
        Assert.assertEquals("INSERT HIGH_PRIORITY INTO test.t1 (t1.id1, id2)\n" + //
        		"VALUES (?, '123')", output);
    }
    
    public void testInsert_1() throws Exception {
        String sql = "insErt  IGNORE test.t1 seT t1.id1:=? oN dupLicatE key UPDATE ex.col1=?, col2=12";
        MySqlStatementParser parser = new MySqlStatementParser(sql);
        SQLStatement stmt = parser.parseStatementList().get(0);
        parser.match(Token.EOF);
        String output = SQLUtils.toMySqlString(stmt);
        Assert.assertEquals("INSERT IGNORE INTO test.t1 (t1.id1)\nVALUES (?) " + //
        		"ON DUPLICATE KEY UPDATE ex.col1 = ?, col2 = 12", output);
    }
    
    public void testInsert_2() throws Exception {
        String sql = "insErt t1 value (123,?) oN dupLicatE key UPDATE ex.col1=?";
        MySqlStatementParser parser = new MySqlStatementParser(sql);
        SQLStatement stmt = parser.parseStatementList().get(0);
        parser.match(Token.EOF);
        String output = SQLUtils.toMySqlString(stmt);
        Assert.assertEquals("INSERT INTO t1\nVALUES (123, ?) ON DUPLICATE KEY UPDATE ex.col1 = ?", output);
    }
}
