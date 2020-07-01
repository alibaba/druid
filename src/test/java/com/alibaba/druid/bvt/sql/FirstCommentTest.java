package com.alibaba.druid.bvt.sql;

import java.util.List;

import org.junit.Assert;

import com.alibaba.druid.sql.dialect.mysql.parser.MySqlLexer;
import com.alibaba.druid.sql.parser.Lexer;

import junit.framework.TestCase;

public class FirstCommentTest extends TestCase {
    public void test_first_commnet() throws Exception {
        String sql = "/*test sql*/select age from user where name = 'xx';";
        Lexer lexer = new MySqlLexer(sql);
        lexer.setKeepComments(true);
        lexer.nextToken();
        List<String> comments = lexer.readAndResetComments();
        Assert.assertEquals("/*test sql*/", comments.get(0));
    }
}
