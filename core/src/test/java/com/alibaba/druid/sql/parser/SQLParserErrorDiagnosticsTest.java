package com.alibaba.druid.sql.parser;

import com.alibaba.druid.sql.dialect.mysql.parser.MySqlStatementParser;
import org.junit.jupiter.api.Test;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.*;

public class SQLParserErrorDiagnosticsTest {
    private static final Pattern LOCATION_PATTERN = Pattern.compile("pos (\\d+), line (\\d+), column (\\d+)");

    @Test
    public void test_notSupportedDiagnostics_includeTokenAndLocationContext() {
        String message = parseFailureMessage("SELECT *\nFORM a");

        assertTrue(message.startsWith("not supported. "));
        assertTrue(message.contains("token IDENTIFIER FORM"));
        assertTrue(message.contains("line 2"));
        assertTrue(message.contains("column "));
        assertNotNull(extractLocation(message));
    }

    @Test
    public void test_equivalentMalformedInput_keepsComparableLocationContext() {
        String first = parseFailureMessage("SELECT *\nFORM a");
        String second = parseFailureMessage("SELECT *\nFORM b");

        String firstLocation = extractLocation(first);
        String secondLocation = extractLocation(second);

        assertNotNull(firstLocation);
        assertNotNull(secondLocation);
        assertEquals(firstLocation, secondLocation);
    }

    private static String parseFailureMessage(String sql) {
        MySqlStatementParser parser = new MySqlStatementParser(sql);
        try {
            parser.parseStatementList();
            fail("expected ParserException");
            return null;
        } catch (ParserException ex) {
            return ex.getMessage();
        }
    }

    private static String extractLocation(String message) {
        Matcher matcher = LOCATION_PATTERN.matcher(message);
        if (matcher.find()) {
            return matcher.group();
        }
        return null;
    }
}
