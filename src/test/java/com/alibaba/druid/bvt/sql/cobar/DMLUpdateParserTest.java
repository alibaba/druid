package com.alibaba.druid.bvt.sql.cobar;

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.mysql.parser.MySqlStatementParser;
import com.alibaba.druid.sql.parser.Token;

import junit.framework.Assert;
import junit.framework.TestCase;


public class DMLUpdateParserTest extends TestCase {
    public void test_update_0() throws Exception {
        String sql = "upDate LOw_PRIORITY IGNORE test.t1 sEt t1.col1=?, col2=DefaulT";
        MySqlStatementParser parser = new MySqlStatementParser(sql);
        SQLStatement stmt = parser.parseStatementList().get(0);
        parser.match(Token.EOF);
        String output = SQLUtils.toMySqlString(stmt);
        Assert.assertEquals("UPDATE LOW_PRIORITY IGNORE test.t1\nSET t1.col1 = ?, col2 = DEFAULT", output);
    }
    
    public void test_update_1() throws Exception {
        String sql = "upDate  IGNORE (t1) set col2=DefaulT order bY t1.col2 ";
        MySqlStatementParser parser = new MySqlStatementParser(sql);
        SQLStatement stmt = parser.parseStatementList().get(0);
        parser.match(Token.EOF);
        String output = SQLUtils.toMySqlString(stmt);
        Assert.assertEquals("UPDATE IGNORE t1\nSET col2 = DEFAULT\nORDER BY t1.col2", output);
    }
    
    public void test_update_2() throws Exception {
        String sql = "upDate   (test.t1) SET col2=DefaulT order bY t1.col2 limit ? offset 1";
        MySqlStatementParser parser = new MySqlStatementParser(sql);
        SQLStatement stmt = parser.parseStatementList().get(0);
        parser.match(Token.EOF);
        String output = SQLUtils.toMySqlString(stmt);
        Assert.assertEquals("UPDATE test.t1\nSET col2 = DEFAULT\nORDER BY t1.col2\nLIMIT 1, ?", output);
    }
    
    public void test_update_3() throws Exception {
        String sql = "upDate LOW_PRIORITY  t1, test.t2 SET col2=DefaulT , col2='123''4'";
        MySqlStatementParser parser = new MySqlStatementParser(sql);
        SQLStatement stmt = parser.parseStatementList().get(0);
        parser.match(Token.EOF);
        String output = SQLUtils.toMySqlString(stmt);
        Assert.assertEquals("UPDATE LOW_PRIORITY t1, test.t2\nSET col2 = DEFAULT, col2 = '123''4'", output);
    }
    
    public void test_update_4() throws Exception {
        String sql = "upDate LOW_PRIORITY  t1, test.t2 SET col2:=DefaulT , col2='123''4' where id='a'";
        MySqlStatementParser parser = new MySqlStatementParser(sql);
        SQLStatement stmt = parser.parseStatementList().get(0);
        parser.match(Token.EOF);
        String output = SQLUtils.toMySqlString(stmt);
        Assert.assertEquals("UPDATE LOW_PRIORITY t1, test.t2\nSET col2 = DEFAULT, col2 = '123''4'\nWHERE id = 'a'", output);
    }
}
