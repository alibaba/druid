package com.alibaba.druid.sql.parser;

import org.junit.Assert;
import org.junit.Test;

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

        Assert.assertEquals(singleQuoted.stringVal(), doubleQuoted.stringVal());
        Assert.assertEquals(Token.LITERAL_CHARS, singleQuoted.token());
        Assert.assertEquals(Token.LITERAL_CHARS, doubleQuoted.token());
    }

    @Test
    public void testScanString2UnicodeEscapeWhenEnabled() {
        ExposedLexer lexer = new ExposedLexer("'A\\u0042C'");
        lexer.config(SQLParserFeature.SupportUnicodeCodePoint, true);
        lexer.scanSingleQuoteMode();

        Assert.assertEquals(Token.LITERAL_CHARS, lexer.token());
        Assert.assertEquals("ABC", lexer.stringVal());
    }

    @Test
    public void testScanString2dIdentifierBeforeDot() {
        Lexer lexer = new Lexer("\"col\".x");
        lexer.nextTokenValue();

        Assert.assertEquals(Token.IDENTIFIER, lexer.token());
        Assert.assertEquals("\"col\"", lexer.stringVal());
    }

    @Test
    public void testScanString2dUnclosedStringThrowsParserException() {
        ExposedLexer lexer = new ExposedLexer("\"abc");
        try {
            lexer.scanDoubleQuoteMode();
            Assert.fail();
        } catch (ParserException ex) {
            Assert.assertTrue(ex.getMessage().contains("unclosed str."));
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
