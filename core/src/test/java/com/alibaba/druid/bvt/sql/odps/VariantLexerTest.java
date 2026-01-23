package com.alibaba.druid.bvt.sql.odps;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.parser.Lexer;
import com.alibaba.druid.sql.parser.SQLParserUtils;
import com.alibaba.druid.sql.parser.Token;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class VariantLexerTest {
    @Test
    public void test_0() throws Exception {
        String sql = "DROP TABLE public.${mc_table_name}_tmp；";
        Lexer lexer = SQLParserUtils.createLexer(sql, DbType.odps);

        lexer.nextToken();
        assertEquals(Token.DROP, lexer.token());

        lexer.nextToken();
        assertEquals(Token.TABLE, lexer.token());

        lexer.nextToken();
        assertTrue(lexer.identifierEquals("public"));

        lexer.nextToken();
        assertEquals(Token.DOT, lexer.token());

        lexer.nextToken();
        assertEquals(Token.IDENTIFIER, lexer.token());
        assertEquals("${mc_table_name}_tmp", lexer.stringVal());

        lexer.nextToken();
        assertEquals(Token.SEMI, lexer.token());
    }
}
