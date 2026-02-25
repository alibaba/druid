package com.alibaba.druid.sql.parser;

import com.alibaba.druid.sql.parser.DialectFeature.LexerFeature;
import com.alibaba.druid.sql.parser.DialectFeature.ParserFeature;
import org.junit.Assert;
import org.junit.Test;

public class DialectFeatureNamingAliasTest {
    @Test
    public void testParserFeatureAliasCompatibility() {
        DialectFeature feature = new DialectFeature();
        feature.unconfigFeature(ParserFeature.UDJ, ParserFeature.UserDefinedJoin);

        Assert.assertFalse(feature.isEnabled(ParserFeature.UDJ));
        Assert.assertFalse(feature.isEnabled(ParserFeature.UserDefinedJoin));
        Assert.assertEquals(ParserFeature.UDJ.getMask(), ParserFeature.UserDefinedJoin.getMask());

        feature.configFeature(ParserFeature.UserDefinedJoin, true);
        Assert.assertTrue(feature.isEnabled(ParserFeature.UDJ));
        Assert.assertTrue(feature.isEnabled(ParserFeature.UserDefinedJoin));

        feature.configFeature(ParserFeature.UDJ, false);
        Assert.assertFalse(feature.isEnabled(ParserFeature.UDJ));
        Assert.assertFalse(feature.isEnabled(ParserFeature.UserDefinedJoin));
    }

    @Test
    public void testLexerFeatureAliasCompatibility() {
        DialectFeature feature = new DialectFeature();
        feature.unconfigFeature(LexerFeature.ScanString2PutDoubleBackslash, LexerFeature.ScanStringDoubleBackslash);

        Assert.assertFalse(feature.isEnabled(LexerFeature.ScanString2PutDoubleBackslash));
        Assert.assertFalse(feature.isEnabled(LexerFeature.ScanStringDoubleBackslash));
        Assert.assertEquals(LexerFeature.ScanString2PutDoubleBackslash.getMask(), LexerFeature.ScanStringDoubleBackslash.getMask());

        feature.configFeature(LexerFeature.ScanStringDoubleBackslash, true);
        Assert.assertTrue(feature.isEnabled(LexerFeature.ScanString2PutDoubleBackslash));
        Assert.assertTrue(feature.isEnabled(LexerFeature.ScanStringDoubleBackslash));

        feature.configFeature(LexerFeature.ScanString2PutDoubleBackslash, false);
        Assert.assertFalse(feature.isEnabled(LexerFeature.ScanString2PutDoubleBackslash));
        Assert.assertFalse(feature.isEnabled(LexerFeature.ScanStringDoubleBackslash));
    }
}
