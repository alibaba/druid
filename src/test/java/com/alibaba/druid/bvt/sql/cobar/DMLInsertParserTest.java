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
    
    public void testInsert_3() throws Exception {
        String sql = "insErt LOW_PRIORITY t1 valueS (12e-2,1,2), (?),(default)";
        MySqlStatementParser parser = new MySqlStatementParser(sql);
        SQLStatement stmt = parser.parseStatementList().get(0);
        parser.match(Token.EOF);
        String output = SQLUtils.toMySqlString(stmt);
        Assert.assertEquals("INSERT LOW_PRIORITY INTO t1\nVALUES (0.12, 1, 2), (?), (DEFAULT)", output);
    }
    
    public void testInsert_4() throws Exception {
        String sql = "insErt LOW_PRIORITY t1 select id from t1";
        MySqlStatementParser parser = new MySqlStatementParser(sql);
        SQLStatement stmt = parser.parseStatementList().get(0);
        parser.match(Token.EOF);
        String output = SQLUtils.toMySqlString(stmt);
        Assert.assertEquals("INSERT LOW_PRIORITY INTO t1\nSELECT id\nFROM t1", output);
    }
    
    public void testInsert_5() throws Exception {
        String sql = "insErt LOW_PRIORITY t1 (select id from t1) oN dupLicatE key UPDATE ex.col1=?, col2=12";
        MySqlStatementParser parser = new MySqlStatementParser(sql);
        SQLStatement stmt = parser.parseStatementList().get(0);
        parser.match(Token.EOF);
        String output = SQLUtils.toMySqlString(stmt);
        Assert.assertEquals("INSERT LOW_PRIORITY INTO t1\nSELECT id\nFROM t1 ON DUPLICATE KEY UPDATE ex.col1 = ?, col2 = 12", output);
    }
    
    public void testInsert_6() throws Exception {
        String sql = "insErt LOW_PRIORITY t1 (t1.col1) valueS (123),('12''34')";
        MySqlStatementParser parser = new MySqlStatementParser(sql);
        SQLStatement stmt = parser.parseStatementList().get(0);
        parser.match(Token.EOF);
        String output = SQLUtils.toMySqlString(stmt);
        Assert.assertEquals("INSERT LOW_PRIORITY INTO t1 (t1.col1)\nVALUES (123), ('12''34')", output);
    }
    
    public void testInsert_7() throws Exception {
        String sql = "insErt LOW_PRIORITY t1 (col1, t1.col2) VALUE (123,'123\\'4') oN dupLicatE key UPDATE ex.col1=?";
        MySqlStatementParser parser = new MySqlStatementParser(sql);
        SQLStatement stmt = parser.parseStatementList().get(0);
        parser.match(Token.EOF);
        String output = SQLUtils.toMySqlString(stmt);
        Assert.assertEquals("INSERT LOW_PRIORITY INTO t1 (col1, t1.col2)\nVALUES (123, '123''4') ON DUPLICATE KEY UPDATE ex.col1 = ?", output);
    }
    
    public void testInsert_8() throws Exception {
        String sql = "insErt LOW_PRIORITY t1 (col1, t1.col2) select id from t3 oN dupLicatE key UPDATE ex.col1=?";
        MySqlStatementParser parser = new MySqlStatementParser(sql);
        SQLStatement stmt = parser.parseStatementList().get(0);
        parser.match(Token.EOF);
        String output = SQLUtils.toMySqlString(stmt);
        Assert.assertEquals("INSERT LOW_PRIORITY INTO t1 (col1, t1.col2)\nSELECT id\nFROM t3 ON DUPLICATE KEY UPDATE ex.col1 = ?", output);
    }
    
    public void testInsert_9() throws Exception {
        String sql = "insErt LOW_PRIORITY IGNORE intO t1 (col1) ( select id from t3) ";
        MySqlStatementParser parser = new MySqlStatementParser(sql);
        SQLStatement stmt = parser.parseStatementList().get(0);
        parser.match(Token.EOF);
        String output = SQLUtils.toMySqlString(stmt);
        Assert.assertEquals("INSERT LOW_PRIORITY IGNORE INTO t1 (col1)\nSELECT id\nFROM t3", output);
    }
}
