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
}
