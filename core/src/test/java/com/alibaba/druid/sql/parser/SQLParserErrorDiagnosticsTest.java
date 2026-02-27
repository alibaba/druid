package com.alibaba.druid.sql.parser;

import com.alibaba.druid.sql.dialect.mysql.parser.MySqlStatementParser;
import org.junit.Assert;
import org.junit.Test;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SQLParserErrorDiagnosticsTest {
    private static final Pattern LOCATION_PATTERN = Pattern.compile("pos (\\d+), line (\\d+), column (\\d+)");

    @Test
    public void test_notSupportedDiagnostics_includeTokenAndLocationContext() {
        String message = parseFailureMessage("SELECT *\nFORM a");

        Assert.assertTrue(message.startsWith("not supported. "));
        Assert.assertTrue(message.contains("token IDENTIFIER FORM"));
        Assert.assertTrue(message.contains("line 2"));
        Assert.assertTrue(message.contains("column "));
        Assert.assertNotNull(extractLocation(message));
    }

    @Test
    public void test_equivalentMalformedInput_keepsComparableLocationContext() {
        String first = parseFailureMessage("SELECT *\nFORM a");
        String second = parseFailureMessage("SELECT *\nFORM b");

        String firstLocation = extractLocation(first);
        String secondLocation = extractLocation(second);

        Assert.assertNotNull(firstLocation);
        Assert.assertNotNull(secondLocation);
        Assert.assertEquals(firstLocation, secondLocation);
    }

    private static String parseFailureMessage(String sql) {
        MySqlStatementParser parser = new MySqlStatementParser(sql);
        try {
            parser.parseStatementList();
            Assert.fail("expected ParserException");
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
