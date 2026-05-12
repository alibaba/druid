package com.alibaba.druid.sql.parser;

import com.alibaba.druid.sql.parser.DialectFeature.LexerFeature;
import com.alibaba.druid.sql.parser.DialectFeature.ParserFeature;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class DialectFeatureNamingAliasTest {
    @Test
    public void testParserFeatureAliasCompatibility() {
        DialectFeature feature = new DialectFeature();
        feature.unconfigFeature(ParserFeature.UDJ, ParserFeature.UserDefinedJoin);

        assertFalse(feature.isEnabled(ParserFeature.UDJ));
        assertFalse(feature.isEnabled(ParserFeature.UserDefinedJoin));
        assertEquals(ParserFeature.UDJ.getMask(), ParserFeature.UserDefinedJoin.getMask());

        feature.configFeature(ParserFeature.UserDefinedJoin, true);
        assertTrue(feature.isEnabled(ParserFeature.UDJ));
        assertTrue(feature.isEnabled(ParserFeature.UserDefinedJoin));

        feature.configFeature(ParserFeature.UDJ, false);
        assertFalse(feature.isEnabled(ParserFeature.UDJ));
        assertFalse(feature.isEnabled(ParserFeature.UserDefinedJoin));
    }

    @Test
    public void testLexerFeatureAliasCompatibility() {
        DialectFeature feature = new DialectFeature();
        feature.unconfigFeature(LexerFeature.ScanString2PutDoubleBackslash, LexerFeature.ScanStringDoubleBackslash);

        assertFalse(feature.isEnabled(LexerFeature.ScanString2PutDoubleBackslash));
        assertFalse(feature.isEnabled(LexerFeature.ScanStringDoubleBackslash));
        assertEquals(LexerFeature.ScanString2PutDoubleBackslash.getMask(), LexerFeature.ScanStringDoubleBackslash.getMask());

        feature.configFeature(LexerFeature.ScanStringDoubleBackslash, true);
        assertTrue(feature.isEnabled(LexerFeature.ScanString2PutDoubleBackslash));
        assertTrue(feature.isEnabled(LexerFeature.ScanStringDoubleBackslash));

        feature.configFeature(LexerFeature.ScanString2PutDoubleBackslash, false);
        assertFalse(feature.isEnabled(LexerFeature.ScanString2PutDoubleBackslash));
        assertFalse(feature.isEnabled(LexerFeature.ScanStringDoubleBackslash));
    }
}
