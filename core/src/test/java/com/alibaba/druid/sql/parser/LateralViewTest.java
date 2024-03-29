package com.alibaba.druid.sql.parser;

import com.alibaba.druid.sql.ast.SQLStatement;
import junit.framework.TestCase;

import java.util.List;

public class LateralViewTest extends TestCase {

    private final static SQLParserFeature[] FORMAT_DEFAULT_FEATURES = {
            SQLParserFeature.KeepComments,
            SQLParserFeature.EnableSQLBinaryOpExprGroup
    };

    public String format(String sql) {
        SQLStatementParser parser = SQLParserUtils.createSQLStatementParser(sql, "hive", FORMAT_DEFAULT_FEATURES);
        List<SQLStatement> statementList = parser.parseStatementList();
        return statementList.get(0).toString();
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
