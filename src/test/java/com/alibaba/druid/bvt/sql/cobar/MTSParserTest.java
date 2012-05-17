package com.alibaba.druid.bvt.sql.cobar;

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.mysql.parser.MySqlStatementParser;
import com.alibaba.druid.sql.parser.Token;

import junit.framework.Assert;
import junit.framework.TestCase;

public class MTSParserTest extends TestCase {

    public void test_mts_0() throws Exception {
        String sql = "  savepoint xx";
        MySqlStatementParser parser = new MySqlStatementParser(sql);
        SQLStatement stmt = parser.parseStatementList().get(0);
        parser.match(Token.EOF);
        String output = SQLUtils.toMySqlString(stmt);
        Assert.assertEquals("SAVEPOINT xx", output);
    }

    public void test_mts_1() throws Exception {
        String sql = "  savepoint SAVEPOINT";
        MySqlStatementParser parser = new MySqlStatementParser(sql);
        SQLStatement stmt = parser.parseStatementList().get(0);
        parser.match(Token.EOF);
        String output = SQLUtils.toMySqlString(stmt);
        Assert.assertEquals("SAVEPOINT SAVEPOINT", output);
    }

    public void test_mts_2() throws Exception {
        String sql = "  savepoInt `select`";
        MySqlStatementParser parser = new MySqlStatementParser(sql);
        SQLStatement stmt = parser.parseStatementList().get(0);
        parser.match(Token.EOF);
        String output = SQLUtils.toMySqlString(stmt);
        Assert.assertEquals("SAVEPOINT `select`", output);
    }
    
    public void test_mts_3() throws Exception {
        String sql = "Release sAVEPOINT xx   ";
        MySqlStatementParser parser = new MySqlStatementParser(sql);
        SQLStatement stmt = parser.parseStatementList().get(0);
        parser.match(Token.EOF);
        String output = SQLUtils.toMySqlString(stmt);
        Assert.assertEquals("RELEASE SAVEPOINT xx", output);
    }
    
    public void test_rollback_0() throws Exception {
        String sql = "rollBack to x1";
        MySqlStatementParser parser = new MySqlStatementParser(sql);
        SQLStatement stmt = parser.parseStatementList().get(0);
        parser.match(Token.EOF);
        String output = SQLUtils.toMySqlString(stmt);
        Assert.assertEquals("ROLLBACK TO x1", output);
    }
    
    public void test_rollback_1() throws Exception {
        String sql = "rollBack work to x1";
        MySqlStatementParser parser = new MySqlStatementParser(sql);
        SQLStatement stmt = parser.parseStatementList().get(0);
        parser.match(Token.EOF);
        String output = SQLUtils.toMySqlString(stmt);
        Assert.assertEquals("ROLLBACK TO x1", output);
    }
    
    public void test_rollback_2() throws Exception {
        String sql = "rollBack work to savepoint x1";
        MySqlStatementParser parser = new MySqlStatementParser(sql);
        SQLStatement stmt = parser.parseStatementList().get(0);
        parser.match(Token.EOF);
        String output = SQLUtils.toMySqlString(stmt);
        Assert.assertEquals("ROLLBACK TO x1", output);
    }
    
    public void test_lockTable() throws Exception {
        String sql = "LOCK TABLES t1 READ;";
        MySqlStatementParser parser = new MySqlStatementParser(sql);
        SQLStatement stmt = parser.parseStatementList().get(0);
        parser.match(Token.EOF);
        String output = SQLUtils.toMySqlString(stmt);
        Assert.assertEquals("LOCK TABLES t1 READ", output);
    }
    
    public void test_lockTable_1() throws Exception {
        String sql = "LOCK TABLES t2 READ LOCAL;";
        MySqlStatementParser parser = new MySqlStatementParser(sql);
        SQLStatement stmt = parser.parseStatementList().get(0);
        parser.match(Token.EOF);
        String output = SQLUtils.toMySqlString(stmt);
        Assert.assertEquals("LOCK TABLES t2 READ LOCAL", output);
    }
}
