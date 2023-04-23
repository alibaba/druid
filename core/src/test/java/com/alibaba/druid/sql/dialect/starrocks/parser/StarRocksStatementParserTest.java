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

    public void testParseCreateResource() {
        String sql = "CREATE EXTERNAL RESOURCE \"spark0\"\n" +
                "PROPERTIES (\n" +
                "  'spark.master' = 'yarn', \n" +
                "  'spark.executor.memory' = '1g', \n" +
                "  'working_dir' = 'hdfs://127.0.0.1:10000/tmp/doris', \n" +
                "  'spark.submit.deployMode' = 'cluster', \n" +
                "  'broker' = 'broker0', \n" +
                "  'type' = 'spark', \n" +
                "  'spark.yarn.queue' = 'queue0', \n" +
                "  'spark.hadoop.yarn.resourcemanager.address' = '127.0.0.1:9999', \n" +
                "  'broker.password' = 'password0', \n" +
                "  'broker.username' = 'user0', \n" +
                "  'spark.hadoop.fs.defaultFS' = 'hdfs://127.0.0.1:10000', \n" +
                "  'spark.jars' = 'xxx.jar,yyy.jar', \n" +
                "  'spark.files' = '/tmp/aaa,/tmp/bbb'\n" +
                ");";
        SQLStatement stmt = SQLUtils.parseSingleStatement(sql, DbType.starrocks);
        assertEquals(sql, stmt.toString());
    }
}