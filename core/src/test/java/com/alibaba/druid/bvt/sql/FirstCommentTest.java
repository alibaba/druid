package com.alibaba.druid.bvt.sql;

import com.alibaba.druid.sql.dialect.mysql.parser.MySqlLexer;
import com.alibaba.druid.sql.parser.Lexer;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class FirstCommentTest {
    @Test
    public void test_first_commnet() throws Exception {
        String sql = "/*test sql*/select age from user where name = 'xx';";
        Lexer lexer = new MySqlLexer(sql);
        lexer.setKeepComments(true);
        lexer.nextToken();
        List<String> comments = lexer.readAndResetComments();
        assertEquals("/*test sql*/", comments.get(0));
    }
}
