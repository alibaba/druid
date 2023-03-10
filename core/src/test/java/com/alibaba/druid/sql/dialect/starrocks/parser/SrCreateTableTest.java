package com.alibaba.druid.sql.dialect.starrocks.parser;


import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.parser.SQLParserFeature;
import org.junit.Test;

import java.util.List;

/**
* @author Ekko
* @date 2023/2/23 10:44
*/
public class SrCreateTableTest{


    @Test
    public void createTableTest() {




        String sql = "CREATE TABLE IF NOT EXISTS `detailDemo` (\n" +
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
                ") ENGINE=OLAP\n" +
                "DUPLICATE KEY(`recruit_date`, `region_num`)\n" +
                "PARTITION BY RANGE(`recruit_date`)\n" +
                "(\n" +
                "    PARTITION p1 VALUES LESS THAN (\"2021-01-02\"),\n" +
                "    PARTITION p2 VALUES LESS THAN (\"2021-01-03\"),\n" +
                "    PARTITION p20220313 VALUES [('2022-03-13'), ('2022-03-14')),\n" +
                ")";


        StarRocksStatementParser starRocksStatementParser = new StarRocksStatementParser(sql, SQLParserFeature.KeepComments);
        List<SQLStatement> sqlStatements = starRocksStatementParser.parseStatementList();
        System.out.println("大小为:" + sqlStatements.size());
        for (SQLStatement sqlStatement : sqlStatements) {
            System.out.println(sqlStatement.toString());
        }

    }
}
