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
        String[] ddlList = new String[] {
                "CREATE EXTERNAL RESOURCE \"spark0\"\n" +
                        "PROPERTIES (\n" +
                        "\t'spark.master' = 'yarn',\n" +
                        "\t'spark.executor.memory' = '1g',\n" +
                        "\t'working_dir' = 'hdfs://127.0.0.1:10000/tmp/doris',\n" +
                        "\t'spark.submit.deployMode' = 'cluster',\n" +
                        "\t'broker' = 'broker0',\n" +
                        "\t'type' = 'spark',\n" +
                        "\t'spark.yarn.queue' = 'queue0',\n" +
                        "\t'spark.hadoop.yarn.resourcemanager.address' = '127.0.0.1:9999',\n" +
                        "\t'broker.password' = 'password0',\n" +
                        "\t'broker.username' = 'user0',\n" +
                        "\t'spark.hadoop.fs.defaultFS' = 'hdfs://127.0.0.1:10000',\n" +
                        "\t'spark.jars' = 'xxx.jar,yyy.jar',\n" +
                        "\t'spark.files' = '/tmp/aaa,/tmp/bbb'\n" +
                        ");",
                "CREATE RESOURCE \"spark0\"\n" +
                        "PROPERTIES (\n" +
                        "\t'spark.master' = 'yarn',\n" +
                        "\t'spark.executor.memory' = '1g',\n" +
                        "\t'working_dir' = 'hdfs://127.0.0.1:10000/tmp/doris',\n" +
                        "\t'spark.submit.deployMode' = 'cluster',\n" +
                        "\t'broker' = 'broker0',\n" +
                        "\t'type' = 'spark',\n" +
                        "\t'spark.yarn.queue' = 'queue0',\n" +
                        "\t'spark.hadoop.yarn.resourcemanager.address' = '127.0.0.1:9999',\n" +
                        "\t'broker.password' = 'password0',\n" +
                        "\t'broker.username' = 'user0',\n" +
                        "\t'spark.hadoop.fs.defaultFS' = 'hdfs://127.0.0.1:10000',\n" +
                        "\t'spark.jars' = 'xxx.jar,yyy.jar',\n" +
                        "\t'spark.files' = '/tmp/aaa,/tmp/bbb'\n" +
                        ");",
        };

        for (String ddl : ddlList) {
            SQLStatement stmt = SQLUtils.parseSingleStatement(ddl, DbType.starrocks);
            assertEquals(ddl, stmt.toString());
        }
    }
}