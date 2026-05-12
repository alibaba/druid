package com.alibaba.druid.sql.parser;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class LexerScanModeDedupRegressionTest {
    @Test
    public void testScanString2AndScanString2dSharedEscapes() {
        String escapedContent = "a\\n\\t\\r\\0\\\\\\'\\\"\\Z\\%\\_b";

        ExposedLexer singleQuoted = new ExposedLexer("'" + escapedContent + "'");
        singleQuoted.config(SQLParserFeature.SupportUnicodeCodePoint, true);
        singleQuoted.disableDoubleBackslashForLikeEscapes();
        singleQuoted.scanSingleQuoteMode();

        ExposedLexer doubleQuoted = new ExposedLexer("\"" + escapedContent + "\"");
        doubleQuoted.config(SQLParserFeature.SupportUnicodeCodePoint, true);
        doubleQuoted.disableDoubleBackslashForLikeEscapes();
        doubleQuoted.scanDoubleQuoteMode();

        assertEquals(singleQuoted.stringVal(), doubleQuoted.stringVal());
        assertEquals(Token.LITERAL_CHARS, singleQuoted.token());
        assertEquals(Token.LITERAL_CHARS, doubleQuoted.token());
    }

    @Test
    public void testScanString2UnicodeEscapeWhenEnabled() {
        ExposedLexer lexer = new ExposedLexer("'A\\u0042C'");
        lexer.config(SQLParserFeature.SupportUnicodeCodePoint, true);
        lexer.scanSingleQuoteMode();

        assertEquals(Token.LITERAL_CHARS, lexer.token());
        assertEquals("ABC", lexer.stringVal());
    }

    @Test
    public void testScanString2dIdentifierBeforeDot() {
        Lexer lexer = new Lexer("\"col\".x");
        lexer.nextTokenValue();

        assertEquals(Token.IDENTIFIER, lexer.token());
        assertEquals("\"col\"", lexer.stringVal());
    }

    @Test
    public void testScanString2dUnclosedStringThrowsParserException() {
        ExposedLexer lexer = new ExposedLexer("\"abc");
        try {
            lexer.scanDoubleQuoteMode();
            fail();
        } catch (ParserException ex) {
            assertTrue(ex.getMessage().contains("unclosed str."));
        }
    }

    private static class ExposedLexer extends Lexer {
        ExposedLexer(String input) {
            super(input);
        }

        void scanSingleQuoteMode() {
            scanString2();
        }

        void scanDoubleQuoteMode() {
            scanString2_d();
        }

        void disableDoubleBackslashForLikeEscapes() {
            this.dialectFeature.configFeature(
                    DialectFeature.LexerFeature.ScanString2PutDoubleBackslash, false);
        }
    }
}
