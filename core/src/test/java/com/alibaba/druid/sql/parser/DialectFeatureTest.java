package com.alibaba.druid.sql.parser;

import java.util.Arrays;

import org.junit.Assert;
import org.junit.Test;

public class DialectFeatureTest {
    @Test
    public void parserFeatureDuplicateTest() {
        Assert.assertEquals(DialectFeature.ParserFeature.values().length,
                Arrays.stream(DialectFeature.ParserFeature.values()).map(
                        DialectFeature.ParserFeature::getMask).distinct().count());
    }

    @Test
    public void parserLexerDuplicateTest() {
        Assert.assertEquals(DialectFeature.LexerFeature.values().length,
                Arrays.stream(DialectFeature.LexerFeature.values()).map(
                        DialectFeature.LexerFeature::getMask).distinct().count());
    }
}
