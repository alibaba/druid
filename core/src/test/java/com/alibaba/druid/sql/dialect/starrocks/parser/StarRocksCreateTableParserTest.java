package com.alibaba.druid.sql.dialect.starrocks.parser;

import com.alibaba.druid.sql.ast.statement.SQLCreateTableStatement;
import com.alibaba.druid.sql.parser.SQLCreateTableParser;
import junit.framework.TestCase;

public class StarRocksCreateTableParserTest extends TestCase {
    static final String[] caseList = new String[]{
            // 1.普通建表语句
            "CREATE TABLE IF NOT EXISTS `detailDemo` (\n" +
                    "\t`recruit_date` DATE NOT NULL COMMENT 'YYYY-MM-DD',\n" +
                    "\t`region_num` TINYINT COMMENT 'range [-128, 127]',\n" +
                    "\t`num_plate` SMALLINT COMMENT 'range [-32768, 32767] ',\n" +
                    "\t`tel` INT COMMENT 'range [-2147483648, 2147483647]',\n" +
                    "\t`id` BIGINT COMMENT 'range [-2^63 + 1 ~ 2^63 - 1]',\n" +
                    "\t`password` LARGEINT COMMENT 'range [-2^127 + 1 ~ 2^127 - 1]',\n" +
                    "\t`name` CHAR(20) NOT NULL COMMENT 'range char(m),m in (1-255)',\n" +
                    "\t`profile` VARCHAR(500) NOT NULL COMMENT 'upper limit value 1048576 bytes',\n" +
                    "\t`hobby` STRING NOT NULL COMMENT 'upper limit value 65533 bytes',\n" +
                    "\t`leave_time` DATETIME COMMENT 'YYYY-MM-DD HH:MM:SS',\n" +
                    "\t`channel` FLOAT COMMENT '4 bytes',\n" +
                    "\t`income` DOUBLE COMMENT '8 bytes',\n" +
                    "\t`account` DECIMAL(12, 4) COMMENT '\"\"',\n" +
                    "\t`ispass` BOOLEAN COMMENT 'true/false'\n" +
                    ")\n" +
                    "DISTRIBUTED BY HASH(`recruit_date`, `region_num`) BUCKETS 8\n",

            // 2.指定引擎，数据模型，PARTITION 分区为 LESS THAN，properties参数的建表语句
            "CREATE TABLE IF NOT EXISTS `detailDemo` (\n" +
                    "\t`recruit_date` DATE NOT NULL COMMENT 'YYYY-MM-DD',\n" +
                    "\t`region_num` TINYINT COMMENT 'range [-128, 127]',\n" +
                    "\t`num_plate` SMALLINT COMMENT 'range [-32768, 32767] ',\n" +
                    "\t`tel` INT COMMENT 'range [-2147483648, 2147483647]',\n" +
                    "\t`id` BIGINT COMMENT 'range [-2^63 + 1 ~ 2^63 - 1]',\n" +
                    "\t`password` LARGEINT COMMENT 'range [-2^127 + 1 ~ 2^127 - 1]',\n" +
                    "\t`name` CHAR(20) NOT NULL COMMENT 'range char(m),m in (1-255)',\n" +
                    "\t`profile` VARCHAR(500) NOT NULL COMMENT 'upper limit value 1048576 bytes',\n" +
                    "\t`hobby` STRING NOT NULL COMMENT 'upper limit value 65533 bytes',\n" +
                    "\t`leave_time` DATETIME COMMENT 'YYYY-MM-DD HH:MM:SS',\n" +
                    "\t`channel` FLOAT COMMENT '4 bytes',\n" +
                    "\t`income` DOUBLE COMMENT '8 bytes',\n" +
                    "\t`account` DECIMAL(12, 4) COMMENT '\"\"',\n" +
                    "\t`ispass` BOOLEAN COMMENT 'true/false'\n" +
                    ") ENGINE = OLAP\n" +
                    "DUPLICATE KEY (`recruit_date`, `region_num`)\n" +
                    "COMMENT 'detailDemo detailDemo '\n" +
                    "PARTITION BY RANGE(`recruit_date`)\n" +
                    "(\n" +
                    "  PARTITION p1 VALUES LESS THAN (\"2021-01-02\"), \n" +
                    "  PARTITION p2 VALUES LESS THAN (\"2021-01-03\"), \n" +
                    "  PARTITION p3 VALUES LESS THAN MAXVALUE\n" +
                    ")\n" +
                    "DISTRIBUTED BY HASH(`recruit_date`, `region_num`) BUCKETS 8\n" +
                    "PROPERTIES (\n" +
                    "\t\"replication_num\" = \"1\"\n" +
                    ")",

            // 3.LESS THAN分区含有 MAXVALUE| 值的建表语句
            "CREATE TABLE IF NOT EXISTS `detailDemo` (\n" +
                    "\t`recruit_date` DATE NOT NULL COMMENT 'YYYY-MM-DD',\n" +
                    "\t`region_num` TINYINT COMMENT 'range [-128, 127]',\n" +
                    "\t`num_plate` SMALLINT COMMENT 'range [-32768, 32767] ',\n" +
                    "\t`tel` INT COMMENT 'range [-2147483648, 2147483647]',\n" +
                    "\t`id` BIGINT COMMENT 'range [-2^63 + 1 ~ 2^63 - 1]',\n" +
                    "\t`password` LARGEINT COMMENT 'range [-2^127 + 1 ~ 2^127 - 1]',\n" +
                    "\t`name` CHAR(20) NOT NULL COMMENT 'range char(m),m in (1-255)',\n" +
                    "\t`profile` VARCHAR(500) NOT NULL COMMENT 'upper limit value 1048576 bytes',\n" +
                    "\t`hobby` STRING NOT NULL COMMENT 'upper limit value 65533 bytes',\n" +
                    "\t`leave_time` DATETIME COMMENT 'YYYY-MM-DD HH:MM:SS',\n" +
                    "\t`channel` FLOAT COMMENT '4 bytes',\n" +
                    "\t`income` DOUBLE COMMENT '8 bytes',\n" +
                    "\t`account` DECIMAL(12, 4) COMMENT '\"\"',\n" +
                    "\t`ispass` BOOLEAN COMMENT 'true/false'\n" +
                    ") ENGINE = OLAP\n" +
                    "DUPLICATE KEY (`recruit_date`, `region_num`)\n" +
                    "PARTITION BY RANGE(`recruit_date`)\n" +
                    "(\n" +
                    "  PARTITION partition_name1 VALUES LESS THAN MAXVALUE | (\"value1\", \"value2\"), \n" +
                    "  PARTITION partition_name2 VALUES LESS THAN MAXVALUE | (\"value1\", \"value2\")\n" +
                    ")\n" +
                    "DISTRIBUTED BY HASH(`recruit_date`, `region_num`) BUCKETS 8\n" +
                    "PROPERTIES (\n" +
                    "\t\"replication_num\" = \"1\"\n" +
                    ")",

            // 4.分区类型为 Fixed Range的建表语句
            "CREATE TABLE IF NOT EXISTS `detailDemo` (\n" +
                    "\t`recruit_date` DATE NOT NULL COMMENT 'YYYY-MM-DD',\n" +
                    "\t`region_num` TINYINT COMMENT 'range [-128, 127]',\n" +
                    "\t`num_plate` SMALLINT COMMENT 'range [-32768, 32767] ',\n" +
                    "\t`tel` INT COMMENT 'range [-2147483648, 2147483647]',\n" +
                    "\t`id` BIGINT COMMENT 'range [-2^63 + 1 ~ 2^63 - 1]',\n" +
                    "\t`password` LARGEINT COMMENT 'range [-2^127 + 1 ~ 2^127 - 1]',\n" +
                    "\t`name` CHAR(20) NOT NULL COMMENT 'range char(m),m in (1-255)',\n" +
                    "\t`profile` VARCHAR(500) NOT NULL COMMENT 'upper limit value 1048576 bytes',\n" +
                    "\t`hobby` STRING NOT NULL COMMENT 'upper limit value 65533 bytes',\n" +
                    "\t`leave_time` DATETIME COMMENT 'YYYY-MM-DD HH:MM:SS',\n" +
                    "\t`channel` FLOAT COMMENT '4 bytes',\n" +
                    "\t`income` DOUBLE COMMENT '8 bytes',\n" +
                    "\t`account` DECIMAL(12, 4) COMMENT '\"\"',\n" +
                    "\t`ispass` BOOLEAN COMMENT 'true/false'\n" +
                    ") ENGINE = OLAP\n" +
                    "DUPLICATE KEY (`recruit_date`, `region_num`)\n" +
                    "PARTITION BY RANGE(`recruit_date`)\n" +
                    "(\n" +
                    "  PARTITION p202101 VALUES [(\"20210101\"),(\"20210201\")),\n" +
                    "  PARTITION p202102 VALUES [(\"20210201\"),(\"20210301\")),\n" +
                    "  PARTITION p202103 VALUES [(\"20210301\"),(MAXVALUE))\n" +
                    ")\n" +
                    "DISTRIBUTED BY HASH(`recruit_date`, `region_num`) BUCKETS 8\n" +
                    "PROPERTIES (\n" +
                    "\t\"replication_num\" = \"1\"\n" +
                    ")",

            // 5.分区类型为 Fixed Range, 多分段的建表语句
            "CREATE TABLE IF NOT EXISTS `detailDemo` (\n" +
                    "\t`recruit_date` DATE NOT NULL COMMENT 'YYYY-MM-DD',\n" +
                    "\t`region_num` TINYINT COMMENT 'range [-128, 127]',\n" +
                    "\t`num_plate` SMALLINT COMMENT 'range [-32768, 32767] ',\n" +
                    "\t`tel` INT COMMENT 'range [-2147483648, 2147483647]',\n" +
                    "\t`id` BIGINT COMMENT 'range [-2^63 + 1 ~ 2^63 - 1]',\n" +
                    "\t`password` LARGEINT COMMENT 'range [-2^127 + 1 ~ 2^127 - 1]',\n" +
                    "\t`name` CHAR(20) NOT NULL COMMENT 'range char(m),m in (1-255)',\n" +
                    "\t`profile` VARCHAR(500) NOT NULL COMMENT 'upper limit value 1048576 bytes',\n" +
                    "\t`hobby` STRING NOT NULL COMMENT 'upper limit value 65533 bytes',\n" +
                    "\t`leave_time` DATETIME COMMENT 'YYYY-MM-DD HH:MM:SS',\n" +
                    "\t`channel` FLOAT COMMENT '4 bytes',\n" +
                    "\t`income` DOUBLE COMMENT '8 bytes',\n" +
                    "\t`account` DECIMAL(12, 4) COMMENT '\"\"',\n" +
                    "\t`ispass` BOOLEAN COMMENT 'true/false'\n" +
                    ") ENGINE = OLAP\n" +
                    "DUPLICATE KEY (`recruit_date`, `region_num`)\n" +
                    "PARTITION BY RANGE(`recruit_date`, `region_num`, `num_plate`)\n" +
                    "(\n" +
                    "  PARTITION partition_name1 VALUES [(\"k1-lower1\", \"k2-lower1\", \"k3-lower1\"),(\"k1-upper1\", \"k2-upper1\", \"k3-upper1\")),\n" +
                    "  PARTITION partition_name2 VALUES [(\"k1-lower1-2\", \"k2-lower1-2\"),(\"k1-upper1-2\", \"k2-upper1-2\", \"k3-upper1-2\"))\n" +
                    ")\n" +
                    "DISTRIBUTED BY HASH(`recruit_date`, `region_num`) BUCKETS 8\n" +
                    "PROPERTIES (\n" +
                    "\t\"replication_num\" = \"1\"\n" +
                    ")",

            // 6. 多种 PROPERTIES 参数类型的建表语句
            "CREATE TABLE IF NOT EXISTS `detailDemo` (\n" +
                    "\t`recruit_date` DATE NOT NULL COMMENT 'YYYY-MM-DD',\n" +
                    "\t`region_num` TINYINT COMMENT 'range [-128, 127]',\n" +
                    "\t`num_plate` SMALLINT COMMENT 'range [-32768, 32767] ',\n" +
                    "\t`tel` INT COMMENT 'range [-2147483648, 2147483647]',\n" +
                    "\t`id` BIGINT COMMENT 'range [-2^63 + 1 ~ 2^63 - 1]',\n" +
                    "\t`password` LARGEINT COMMENT 'range [-2^127 + 1 ~ 2^127 - 1]',\n" +
                    "\t`name` CHAR(20) NOT NULL COMMENT 'range char(m),m in (1-255)',\n" +
                    "\t`profile` VARCHAR(500) NOT NULL COMMENT 'upper limit value 1048576 bytes',\n" +
                    "\t`hobby` STRING NOT NULL COMMENT 'upper limit value 65533 bytes',\n" +
                    "\t`leave_time` DATETIME COMMENT 'YYYY-MM-DD HH:MM:SS',\n" +
                    "\t`channel` FLOAT COMMENT '4 bytes',\n" +
                    "\t`income` DOUBLE COMMENT '8 bytes',\n" +
                    "\t`account` DECIMAL(12, 4) COMMENT '\"\"',\n" +
                    "\t`ispass` BOOLEAN COMMENT 'true/false'\n" +
                    ") ENGINE = OLAP\n" +
                    "DUPLICATE KEY (`recruit_date`, `region_num`)\n" +
                    "PARTITION BY RANGE(`recruit_date`)\n" +
                    "(\n" +
                    "  PARTITION p202101 VALUES [(\"20210101\"),(\"20210201\")),\n" +
                    "  PARTITION p202102 VALUES [(\"20210201\"),(\"20210301\")),\n" +
                    "  PARTITION p202103 VALUES [(\"20210301\"),(MAXVALUE))\n" +
                    ")\n" +
                    "DISTRIBUTED BY HASH(`recruit_date`, `region_num`) BUCKETS 8\n" +
                    "PROPERTIES (\n" +
                    "\t\"storage_medium\" = \"[SSD|HDD]\",\n" +
                    "\t\"dynamic_partition.enable\" = \"true|false\",\n" +
                    "\t\"dynamic_partition.time_unit\" = \"DAY|WEEK|MONTH\",\n" +
                    "\t\"dynamic_partition.start\" = \"${integer_value}\",\n" +
                    "\t[\"storage_cooldown_time\" = \"yyyy-MM-dd HH:mm:ss\",]\n" +
                    "\t[\"replication_num\" = \"3\"]\n" +
                    ")",

            // 7.含有 Bitmap 索引和聚合函数的建表语句
            "CREATE TABLE d0.table_hash (\n" +
                    "\tk1 TINYINT,\n" +
                    "\tk2 DECIMAL(10, 2) DEFAULT \"10.5\",\n" +
                    "\tv1 CHAR(10) REPLACE,\n" +
                    "\tv2 INT SUM,\n" +
                    "\tINDEX index_name(column_name) USING BITMAP COMMENT '22'\n" +
                    ") ENGINE = olap\n" +
                    "AGGREGATE KEY (k1, k2)\n" +
                    "DISTRIBUTED BY HASH(k1) BUCKETS 10\n" +
                    "PROPERTIES (\n" +
                    "\t\"storage_type\" = \"column\"\n" +
                    ")",

            // 8. 外部表
            "CREATE EXTERNAL TABLE example_db.table_mysql (\n" +
                    "\tk1 DATE,\n" +
                    "\tk2 INT,\n" +
                    "\tk3 SMALLINT,\n" +
                    "\tk4 VARCHAR(2048),\n" +
                    "\tk5 DATETIME\n" +
                    ") ENGINE = mysql\n" +
                    "\n" +
                    "PROPERTIES (\n" +
                    "\t\"odbc_catalog_resource\" = \"mysql_resource\",\n" +
                    "\t\"database\" = \"mysql_db_test\",\n" +
                    "\t\"table\" = \"mysql_table_test\"\n" +
                    ")",

            // 9. 数据模型列只有一列
            "CREATE TABLE `olap_5e61d03d605641ebafd100c809dbf15c` (\n" +
                    "\t`a` int(11) NULL,\n" +
                    "\t`b` text NULL,\n" +
                    "\t`c` text NULL\n" +
                    ") ENGINE = OLAP\n" +
                    "DUPLICATE KEY (`a`)\n" +
                    "COMMENT 'OLAP'\n" +
                    "DISTRIBUTED BY RANDOM BUCKETS 10\n" +
                    "PROPERTIES (\n" +
                    "\t\"replication_allocation\" = \"tag.location.default: 1\",\n" +
                    "\t\"in_memory\" = \"false\",\n" +
                    "\t\"storage_format\" = \"V2\",\n" +
                    "\t\"disable_auto_compaction\" = \"false\"\n" +
                    ")"
    };

    public void testCreateTable() {
        for (int i = 0; i < caseList.length; i++) {
            final String sql = caseList[i];
            final StarRocksStatementParser starRocksStatementParser = new StarRocksStatementParser(sql);
            final SQLCreateTableParser sqlCreateTableParser = starRocksStatementParser.getSQLCreateTableParser();
            final SQLCreateTableStatement parsed = sqlCreateTableParser.parseCreateTable();
            final String result = parsed.toString();
            assertEquals("第 " + (i + 1) + "个用例验证失败", sql, result);
        }
    }
}
