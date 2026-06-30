package com.alibaba.druid.bvt.sql;

import com.alibaba.druid.sql.parser.DialectFeature;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.fail;

public class DialectFeatureBitMaskTest {
    private static final Set<String> DEPRECATED_PARSER_ALIASES = new HashSet<>(Arrays.asList(
            "UDJ" // deprecated alias for UserDefinedJoin
    ));

    private static final Set<String> DEPRECATED_LEXER_ALIASES = new HashSet<>(Arrays.asList(
            "ScanString2PutDoubleBackslash" // deprecated alias for ScanStringDoubleBackslash
    ));

    @Test
    public void testParserFeatureBitMasksAreUnique() {
        Map<Long, String> seen = new HashMap<>();
        for (DialectFeature.ParserFeature f : DialectFeature.ParserFeature.values()) {
            if (DEPRECATED_PARSER_ALIASES.contains(f.name())) {
                continue;
            }
            String prev = seen.put(f.getMask(), f.name());
            if (prev != null) {
                fail("ParserFeature bit collision: " + prev + " and " + f.name()
                        + " share mask 0x" + Long.toHexString(f.getMask()));
            }
        }
    }

    @Test
    public void testLexerFeatureBitMasksAreUnique() {
        Map<Long, String> seen = new HashMap<>();
        for (DialectFeature.LexerFeature f : DialectFeature.LexerFeature.values()) {
            if (DEPRECATED_LEXER_ALIASES.contains(f.name())) {
                continue;
            }
            String prev = seen.put(f.getMask(), f.name());
            if (prev != null) {
                fail("LexerFeature bit collision: " + prev + " and " + f.name()
                        + " share mask 0x" + Long.toHexString(f.getMask()));
            }
        }
    }
}
