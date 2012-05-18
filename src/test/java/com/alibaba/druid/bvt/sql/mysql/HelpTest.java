package com.alibaba.druid.bvt.sql.mysql;

import org.junit.Assert;

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.mysql.parser.MySqlStatementParser;
import com.alibaba.druid.sql.parser.Token;

import junit.framework.TestCase;

public class HelpTest extends TestCase {

    public void test_help_0() throws Exception {
        String sql = "HELP 'contents'";
        MySqlStatementParser parser = new MySqlStatementParser(sql);
        SQLStatement stmt = parser.parseStatementList().get(0);
        parser.match(Token.EOF);
        String output = SQLUtils.toMySqlString(stmt);
        Assert.assertEquals("HELP 'contents'", output);
    }

}
