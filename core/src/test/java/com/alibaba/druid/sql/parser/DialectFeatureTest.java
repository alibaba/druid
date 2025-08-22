package com.alibaba.druid.sql.parser;

import java.util.Arrays;

import static org.junit.*;
import org.junit.Test;

public class DialectFeatureTest {
    @Test
    public void parserFeatureDuplicateTest() {
        assertEquals(DialectFeature.ParserFeature.values().length,
                Arrays.stream(DialectFeature.ParserFeature.values()).map(
                        DialectFeature.ParserFeature::getMask).distinct().count());
    }

    @Test
    public void parserLexerDuplicateTest() {
        assertEquals(DialectFeature.LexerFeature.values().length,
                Arrays.stream(DialectFeature.LexerFeature.values()).map(
                        DialectFeature.LexerFeature::getMask).distinct().count());
    }
}
