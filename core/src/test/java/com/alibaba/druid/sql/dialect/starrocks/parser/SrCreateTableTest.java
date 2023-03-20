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
                "    PARTITION p20220311 VALUES [('2022-03-11'), ('2022-03-12')),\n" +
                "    PARTITION p20220312 VALUES [('2022-03-12'), ('2022-03-13')),\n" +
                "    PARTITION p20220313 VALUES [('2022-03-13'), ('2022-03-14')),\n" +
                "    PARTITION p20220314 VALUES [('2022-03-14'), ('2022-03-15')),\n" +
                "    PARTITION p20220315 VALUES [('2022-03-15'), ('2022-03-16'))\n" +
                ")\n" +
                "DISTRIBUTED BY HASH(`recruit_date`, `region_num`) BUCKETS \n" +
                "8\n" +
                "PROPERTIES (\n" +
                "    \"replication_num\" = \"1\" \n" +
                ");";

        String sql2 = "CREATE TABLE IF NOT EXISTS `detailDemo` (\n" +
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
                "    PARTITION partition_name1 VALUES LESS THAN MAXVALUE|(\"value1\", \"value2\"),\n" +
                "    PARTITION partition_name2 VALUES LESS THAN MAXVALUE|(\"value1\", \"value2\")\n" +
                ")\n" +
                "DISTRIBUTED BY HASH(`recruit_date`, `region_num`) BUCKETS \n" +
                "8\n" +
                "PROPERTIES (\n" +
                "    \"replication_num\" = \"1\" \n" +
                ");";

        String sql3 = "CREATE TABLE IF NOT EXISTS `detailDemo` (\n" +
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
                "    PARTITION p1 VALUES LESS THAN (\"20210102\"),\n" +
                "    PARTITION p2 VALUES LESS THAN (\"20210103\"),\n" +
                "    PARTITION p3 VALUES LESS THAN MAXVALUE\n" +
                ")\n" +
                "DISTRIBUTED BY HASH(`recruit_date`, `region_num`) BUCKETS \n" +
                "8\n" +
                "PROPERTIES (\n" +
                "    \"replication_num\" = \"1\" \n" +
                ");";

        String sql4 = "CREATE TABLE IF NOT EXISTS `detailDemo` (\n" +
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
                "PARTITION BY RANGE(`recruit_date`, `region_num`)\n" +
                "(\n" +
                "    PARTITION partition_name1 VALUES [(\"k1-lower1\", \"k2-lower1\", \"k3-lower1\"), (\"k1-upper1\", \"k2-upper1\", \"k3-upper1\")),\n" +
                "    PARTITION partition_name2 VALUES [(\"k1-lower1-2\", \"k2-lower1-2\"), (\"k1-upper1-2\", \"k2-upper1-2\", \"k3-upper1-2\"))\n" +
                ")\n" +
                "DISTRIBUTED BY HASH(`recruit_date`, `region_num`) BUCKETS \n" +
                "8\n" +
                "PROPERTIES (\n" +
                "    \"replication_num\" = \"1\" \n" +
                ");";


//        String sql5 = "CREATE TABLE example_db.table_hash\n" +
//                "(\n" +
//                "    k1 TINYINT,\n" +
//                "    k2 DECIMAL(\n" +
//                "10\n" +
//                ", \n" +
//                "2\n" +
//                ") DEFAULT \"10.5\",\n" +
//                "    INDEX k1_idx (k1)  COMMENT 'xxxxxx'\n" +
//                ")\n" +
//                "ENGINE = olap\n" +
//                "AGGREGATE KEY(k1, k2)\n" +
//                "DISTRIBUTED BY HASH(k1) BUCKETS \n" +
//                "10\n" +
//                "\n" +
//                "PROPERTIES (\"storage_type\" = \"column\");";

        String sql5 = "CREATE TABLE IF NOT EXISTS `detailDemo` (\n" +
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
                "    START (\"2018-01-01\") END (\"2023-01-01\") EVERY (INTERVAL \n" +
                "1\n" +
                " YEAR)\n" +
                ")\n" +
                "DISTRIBUTED BY HASH(`recruit_date`, `region_num`) BUCKETS \n" +
                "8\n" +
                "PROPERTIES (\n" +
                "     \"storage_medium\" = \"[SSD|HDD]\",\n" +
                "    [ \"storage_cooldown_time\" = \"yyyy-MM-dd HH:mm:ss\", ]\n" +
                "    [ \"replication_num\" = \"3\" ]\n" +
                ");";

        String dml = "SELECT t1.c1, t1.c2, t1.c2 FROM t1 LEFT ANTI JOIN t2 ON t1.id = t2.id;";



//        StarRocksStatementParser starRocksStatementParserDML = new StarRocksStatementParser(dml, SQLParserFeature.KeepComments);
//        StarRocksStatementParser starRocksStatementParser = new StarRocksStatementParser(sql, SQLParserFeature.KeepComments);
//        StarRocksStatementParser starRocksStatementParser2 = new StarRocksStatementParser(sql2, SQLParserFeature.KeepComments);
//        StarRocksStatementParser starRocksStatementParser3 = new StarRocksStatementParser(sql3, SQLParserFeature.KeepComments);
//        StarRocksStatementParser starRocksStatementParser4 = new StarRocksStatementParser(sql4, SQLParserFeature.KeepComments);
        StarRocksStatementParser starRocksStatementParser5 = new StarRocksStatementParser(sql5, SQLParserFeature.KeepComments);

//        List<SQLStatement> sqlStatements = starRocksStatementParser.parseStatementList();
//        List<SQLStatement> sqlStatements2 = starRocksStatementParser2.parseStatementList();
//        List<SQLStatement> sqlStatements3 = starRocksStatementParser3.parseStatementList();
//        List<SQLStatement> sqlStatements4 = starRocksStatementParser4.parseStatementList();
        List<SQLStatement> sqlStatements5 = starRocksStatementParser5.parseStatementList();
//        List<SQLStatement> sqlStatements = starRocksStatementParserDML.parseStatementList();
//        for (SQLStatement sqlStatement : sqlStatements) {
//            System.out.println(sqlStatement.toString());
//        }

//        System.out.println("大小为:" + sqlStatements.size());
//        System.out.println("sql1:");
//        for (SQLStatement sqlStatement : sqlStatements) {
//            System.out.println(sqlStatement.toString());
//        }

//        System.out.println("大小为:" + sqlStatements2.size());
//        System.out.println("sql2:");
//        for (SQLStatement sqlStatement : sqlStatements2) {
//            System.out.println(sqlStatement.toString());
//        }
//
//        System.out.println("大小为:" + sqlStatements3.size());
//        System.out.println("sql3:");
//        for (SQLStatement sqlStatement : sqlStatements3) {
//            System.out.println(sqlStatement.toString());
//        }
//
//        System.out.println("大小为:" + sqlStatements4.size());
//        System.out.println("sql4:");
//        for (SQLStatement sqlStatement : sqlStatements4) {
//            System.out.println(sqlStatement.toString());
//        }

        System.out.println("大小为:" + sqlStatements5.size());
        System.out.println("sql5:");
        for (SQLStatement sqlStatement : sqlStatements5) {
            System.out.println(sqlStatement.toString());
        }



    }
}
