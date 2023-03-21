package com.alibaba.druid.sql.dialect.starrocks.parser;

import com.alibaba.druid.sql.ast.statement.SQLDeleteStatement;
import com.alibaba.druid.sql.dialect.db2.parser.DB2StatementParser;
import junit.framework.TestCase;

public class StarRocksCreateTableParserTest extends TestCase {

    public void testCreateTable() {
        final String[] caseList = new String[]{
                // 普通建表语句
                "CREATE TABLE IF NOT EXISTS `detailDemo` (\n" +
                        "    `recruit_date`  DATE           NOT NULL COMMENT \"YYYY-MM-DD\",\n" +
                        "    `region_num`    TINYINT        COMMENT \"range [-128, 127]\",\n" +
                        "    `num_plate`     SMALLINT       COMMENT \"range [-32768, 32767] \",\n" +
                        "    `tel`           INT            COMMENT \"range [-2147483648, 2147483647]\",\n" +
                        "    `id`            BIGINT         COMMENT \"range [-2^63 + 1 ~ 2^63 - 1]\",\n" +
                        "    `password`      LARGEINT       COMMENT \"range [-2^127 + 1 ~ 2^127 - 1]\",\n" +
                        "    `name`          CHAR(\n" +
                        "20\n" +
                        ")       NOT NULL COMMENT \"range char(m),m in (1-255)\",\n" +
                        "    `profile`       VARCHAR(\n" +
                        "500\n" +
                        ")   NOT NULL COMMENT \"upper limit value 1048576 bytes\",\n" +
                        "    `hobby`         STRING         NOT NULL COMMENT \"upper limit value 65533 bytes\",\n" +
                        "    `leave_time`    DATETIME       COMMENT \"YYYY-MM-DD HH:MM:SS\",\n" +
                        "    `channel`       FLOAT          COMMENT \"4 bytes\",\n" +
                        "    `income`        DOUBLE         COMMENT \"8 bytes\",\n" +
                        "    `account`       DECIMAL(\n" +
                        "12\n" +
                        ",\n" +
                        "4\n" +
                        ")  COMMENT \"\",\n" +
                        "    `ispass`        BOOLEAN        COMMENT \"true/false\"\n" +
                        ") ENGINE=OLAPCREATE TABLE IF NOT EXISTS `detailDemo` (\n" +
                        "    `recruit_date`  DATE           NOT NULL COMMENT \"YYYY-MM-DD\",\n" +
                        "    `region_num`    TINYINT        COMMENT \"range [-128, 127]\",\n" +
                        "    `num_plate`     SMALLINT       COMMENT \"range [-32768, 32767] \",\n" +
                        "    `tel`           INT            COMMENT \"range [-2147483648, 2147483647]\",\n" +
                        "    `id`            BIGINT         COMMENT \"range [-2^63 + 1 ~ 2^63 - 1]\",\n" +
                        "    `password`      LARGEINT       COMMENT \"range [-2^127 + 1 ~ 2^127 - 1]\",\n" +
                        "    `name`          CHAR(\n" +
                        "20\n" +
                        ")       NOT NULL COMMENT \"range char(m),m in (1-255)\",\n" +
                        "    `profile`       VARCHAR(\n" +
                        "500\n" +
                        ")   NOT NULL COMMENT \"upper limit value 1048576 bytes\",\n" +
                        "    `hobby`         STRING         NOT NULL COMMENT \"upper limit value 65533 bytes\",\n" +
                        "    `leave_time`    DATETIME       COMMENT \"YYYY-MM-DD HH:MM:SS\",\n" +
                        "    `channel`       FLOAT          COMMENT \"4 bytes\",\n" +
                        "    `income`        DOUBLE         COMMENT \"8 bytes\",\n" +
                        "    `account`       DECIMAL(\n" +
                        "12\n" +
                        ",\n" +
                        "4\n" +
                        ")  COMMENT \"\",\n" +
                        "    `ispass`        BOOLEAN        COMMENT \"true/false\"\n" +
                        ")\n" +
                        "DISTRIBUTED BY HASH(`recruit_date`, `region_num`) BUCKETS \n" +
                        "8\n",
                // 指定引擎，数据模型，LESS THAN分区，properties参数的全建表语句
                "delete from test aaa where 1 = 1 and aaa.id = 2",

                // 分区类型为 Fixed Range 的全建表语句
                "delete from test aaa where exists ( select 1 from b where b.status = 'a' )",
        };

        for (int i = 0; i < caseList.length; i++) {
            final String sql = caseList[i];
            final DB2StatementParser parser = new DB2StatementParser(sql);
            final SQLDeleteStatement parsed = parser.parseDeleteStatement();
            final String result = parsed.toUnformattedString().replaceAll("\\s+", " ").toLowerCase();
            assertEquals("第 " + (i + 1) + "个用例验证失败", sql, result);
        }

    }
}
