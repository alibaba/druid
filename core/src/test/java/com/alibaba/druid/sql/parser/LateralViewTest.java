package com.alibaba.druid.sql.parser;

import com.alibaba.druid.sql.testutil.ParserTestUtils;
import junit.framework.TestCase;

public class LateralViewTest extends TestCase {
    private static final SQLParserFeature[] FORMAT_DEFAULT_FEATURES = {
            SQLParserFeature.KeepComments,
            SQLParserFeature.EnableSQLBinaryOpExprGroup
    };

    public String format(String sql) {
        return ParserTestUtils.formatFirstStatement(sql, "hive", FORMAT_DEFAULT_FEATURES);
    }

    public void test1() {
        String sql = "SELECT DISTINCT t3 FROM d1.t1 t2 LATERAL VIEW explode( split(c1,',')) l1 AS t3";
        String formatted = "SELECT DISTINCT t3\n" +
                "FROM d1.t1 t2\n" +
                "\tLATERAL VIEW explode(split(c1, ',')) l1 AS t3";
        assertEquals(format(sql), formatted);
    }

    public void test2() {
        String sql = "SELECT DISTINCT t3 FROM d1.t1 t2 LATERAL VIEW explode( split(c1,',')) AS t3";
        String formatted = "SELECT DISTINCT t3\n" +
                "FROM d1.t1 t2\n" +
                "\tLATERAL VIEW explode(split(c1, ',')) AS t3";
        assertEquals(format(sql), formatted);
    }
}
