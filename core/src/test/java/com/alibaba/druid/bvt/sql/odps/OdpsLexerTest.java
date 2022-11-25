package com.alibaba.druid.bvt.sql.odps;

import com.alibaba.druid.sql.dialect.odps.parser.OdpsLexer;
import com.alibaba.druid.sql.parser.ParserException;
import com.alibaba.druid.sql.parser.Token;
import junit.framework.TestCase;

public class OdpsLexerTest extends TestCase {
    public void test_0() throws Exception {
        String str = "DESC；";
        OdpsLexer lexer = new OdpsLexer(str);
        lexer.nextToken();
        assertEquals(Token.DESC, lexer.token());
        lexer.nextToken();
        assertEquals(Token.SEMI, lexer.token());
    }

    public void test_1() throws Exception {
        String str = "——\n" +
                "\n" +
                "drop table if exists tdl_idle_mem_portrait_pred_feats_20210831;";
        OdpsLexer lexer = new OdpsLexer(str);
        lexer.nextToken();
        assertEquals(Token.DROP, lexer.token());
    }

    public void test_2() throws Exception {
        String str = "drop table graph_embedding_dev.04_s1_${bizdate}_0108;";
        OdpsLexer lexer = new OdpsLexer(str);
        lexer.nextToken();
        assertEquals(Token.DROP, lexer.token());
        lexer.nextToken();
        assertEquals(Token.TABLE, lexer.token());
        lexer.nextToken();
        assertEquals(Token.IDENTIFIER, lexer.token());
        lexer.nextToken();
        assertEquals(Token.DOT, lexer.token());
        lexer.nextToken();
        assertEquals(Token.IDENTIFIER, lexer.token());
        assertEquals("04_s1_${bizdate}_0108", lexer.stringVal());
    }

    public void test_3() throws Exception {
        String str = "dm_claim_unwww_unsettled_dt@@{yyyyMMdd}_${rundt}";
        OdpsLexer lexer = new OdpsLexer(str);
        lexer.nextToken();
        assertEquals(Token.IDENTIFIER, lexer.token());
        lexer.nextToken();
        assertEquals(Token.VARIANT, lexer.token());
        assertEquals("@{yyyyMMdd}_${rundt}", lexer.stringVal());
    }

    public void test_4() throws Exception {
        String str = "${PN}_events";
        OdpsLexer lexer = new OdpsLexer(str);
        lexer.nextToken();
        assertEquals(Token.IDENTIFIER, lexer.token());
        assertEquals("${PN}_events", lexer.stringVal());
    }

    public void test_error_0() throws Exception {
        String str = "1 `aaa\n${PN}_events";
        OdpsLexer lexer = new OdpsLexer(str);
        lexer.nextToken();
        assertEquals(Token.LITERAL_INT, lexer.token());

        int pos = lexer.pos();
        try {
            lexer.nextToken();
        } catch (ParserException error) {
            lexer.skipToNextLineOrParameter(pos);
            lexer.nextToken();
        }
        assertEquals(Token.IDENTIFIER, lexer.token());
        assertEquals("${PN}_events", lexer.stringVal());
    }

    public void test_error_1() throws Exception {
        String str = "`aaa\n${PN}_events";
        OdpsLexer lexer = new OdpsLexer(str);

        int pos = lexer.pos();
        try {
            lexer.nextToken();
        } catch (ParserException error) {
            lexer.skipToNextLineOrParameter(pos);
            lexer.nextToken();
        }
        assertEquals(Token.IDENTIFIER, lexer.token());
        assertEquals("${PN}_events", lexer.stringVal());
    }
}
