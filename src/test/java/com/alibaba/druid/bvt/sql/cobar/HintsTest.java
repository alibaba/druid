package com.alibaba.druid.bvt.sql.cobar;

import org.junit.Assert;

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.mysql.parser.MySqlStatementParser;
import com.alibaba.druid.sql.parser.Token;

import junit.framework.TestCase;


public class HintsTest extends TestCase {
    public void test_hints_0() throws Exception {
        String sql = "CREATE /*!32302 TEMPORARY */ TABLE t (a INT);";
        MySqlStatementParser parser = new MySqlStatementParser(sql);
        SQLStatement stmt = parser.parseStatementList().get(0);
        parser.match(Token.EOF);
        String output = SQLUtils.toMySqlString(stmt);
        Assert.assertEquals("CREATE /*!32302 TEMPORARY */ TABLE t (\n\ta INT\n)", output);
    }
    
    public void test_hints_1() throws Exception {
        String sql = "SELECT /*! STRAIGHT_JOIN */ col1 FROM table1,table2";
        MySqlStatementParser parser = new MySqlStatementParser(sql);
        SQLStatement stmt = parser.parseStatementList().get(0);
        parser.match(Token.EOF);
        String output = SQLUtils.toMySqlString(stmt);
        Assert.assertEquals("SELECT /*! STRAIGHT_JOIN */ col1\nFROM table1, table2", output);
    }
}
