package com.alibaba.druid.sql.dialect.starrocks.parser;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import junit.framework.TestCase;

public class StarRocksStatementParserTest extends TestCase {
    public void testParseCreate() {
        for (int i = 0; i < StarRocksCreateTableParserTest.caseList.length; i++) {
            final String sql = StarRocksCreateTableParserTest.caseList[i];
            final StarRocksStatementParser starRocksStatementParser = new StarRocksStatementParser(sql);
            final SQLStatement parsed = starRocksStatementParser.parseCreate();
            final String result = parsed.toString();
            assertEquals("第 " + (i + 1) + "个用例验证失败", sql, result);
        }
    }

    public void testParseBySQLUtil() {
        for (int i = 0; i < StarRocksCreateTableParserTest.caseList.length; i++) {
            final String sql = StarRocksCreateTableParserTest.caseList[i];
            final SQLStatement parsed = SQLUtils.parseSingleStatement(sql, DbType.starrocks);
            final String result = parsed.toString();
            assertEquals("第 " + (i + 1) + "个用例验证失败", sql, result);
        }
    }
}