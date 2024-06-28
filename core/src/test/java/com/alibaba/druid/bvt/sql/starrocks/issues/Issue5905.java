package com.alibaba.druid.bvt.sql.starrocks.issues;

import java.util.List;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.SQLParseAssertUtil;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.parser.SQLParserUtils;
import com.alibaba.druid.sql.parser.SQLStatementParser;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * @author lizongbo
 * @see <a href="https://github.com/alibaba/druid/issues/5905>Issue来源</a>
 * @see <a href="https://docs.starrocks.io/zh/docs/sql-reference/sql-statements/data-definition/CREATE_TABLE/">CREATE TABLE</a>
 */
public class Issue5905 {

    @Test
    public void test_parse_create() {
        for (DbType dbType : new DbType[]{DbType.starrocks}) {
            for (String sql : new String[]{
                "CREATE TABLE example_db.table_range\n"
                    + "(\n"
                    + "    k1 DATE,\n"
                    + "    k2 INT,\n"
                    + "    k3 SMALLINT,\n"
                    + "    v1 VARCHAR(2048),\n"
                    + "    v2 DATETIME DEFAULT \"2014-02-04 15:36:00\"\n"
                    + ")\n"
                    + "ENGINE = olap\n"
                    + "DUPLICATE KEY(k1, k2, k3)\n"
                    + "PARTITION BY RANGE (k1)\n"
                    + "(\n"
                    + "    PARTITION p1 VALUES LESS THAN (\"2014-01-01\"),\n"
                    + "    PARTITION p2 VALUES LESS THAN (\"2014-06-01\"),\n"
                    + "    PARTITION p3 VALUES LESS THAN (\"2014-12-01\")\n"
                    + ")\n"
                    + "DISTRIBUTED BY HASH(k2)\n"
                    + "PROPERTIES(\n"
                    + "    \"storage_medium\" = \"SSD\",\n"
                    + "    \"storage_cooldown_time\" = \"2030-06-04 00:00:00\"\n"
                    + ");",
                "CREATE TABLE `ads_hot_area_info_new` (\n"
                    + " `publish_month` datetime NULL COMMENT \"月份\",\n"
                    + " `id` varchar(255) NOT NULL COMMENT \"id\",\n"
                    + " INDEX leader_tag_index (`id`) USING BITMAP\n"
                    + ") ENGINE=OLAP \n"
                    + "UNIQUE KEY(`publish_month`)\n"
                    + "COMMENT \"OLAP\"\n"
                    + "DISTRIBUTED BY HASH(`publish_time`) BUCKETS 3 ;",
                "CREATE TABLE example_db.table_hash\n"
                    + "(\n"
                    + "    k1 TINYINT,\n"
                    + "    k2 DECIMAL(10, 2) DEFAULT \"10.5\",\n"
                    + "    v1 CHAR(10) REPLACE,\n"
                    + "    v2 INT SUM\n"
                    + ")\n"
                    + "ENGINE = olap\n"
                    + "AGGREGATE KEY(k1, k2)\n"
                    + "COMMENT \"my first starrocks table\"\n"
                    + "DISTRIBUTED BY HASH(k1)\n"
                    + "PROPERTIES (\"storage_type\" = \"column\");",
                "CREATE TABLE example_db.table_hash\n"
                    + "(\n"
                    + "    k1 BIGINT,\n"
                    + "    k2 LARGEINT,\n"
                    + "    v1 VARCHAR(2048) REPLACE,\n"
                    + "    v2 SMALLINT DEFAULT \"10\"\n"
                    + ")\n"
                    + "ENGINE = olap\n"
                    + "UNIQUE KEY(k1, k2)\n"
                    + "DISTRIBUTED BY HASH (k1, k2)\n"
                    + "PROPERTIES(\n"
                    + "    \"storage_type\" = \"column\",\n"
                    + "    \"storage_medium\" = \"SSD\",\n"
                    + "    \"storage_cooldown_time\" = \"2021-06-04 00:00:00\"\n"
                    + ");",
"CREATE TABLE example_db.table_hash\n"
    + "(\n"
    + "    k1 BIGINT,\n"
    + "    k2 LARGEINT,\n"
    + "    v1 VARCHAR(2048) REPLACE,\n"
    + "    v2 SMALLINT DEFAULT \"10\"\n"
    + ")\n"
    + "ENGINE = olap\n"
    + "PRIMARY KEY(k1, k2)\n"
    + "DISTRIBUTED BY HASH (k1, k2)\n"
    + "PROPERTIES(\n"
    + "    \"storage_type\" = \"column\",\n"
    + "    \"storage_medium\" = \"SSD\",\n"
    + "    \"storage_cooldown_time\" = \"2022-06-04 00:00:00\"\n"
    + ");",
                "CREATE EXTERNAL TABLE example_db.table_mysql\n"
                    + "(\n"
                    + "    k1 DATE,\n"
                    + "    k2 INT,\n"
                    + "    k3 SMALLINT,\n"
                    + "    k4 VARCHAR(2048),\n"
                    + "    k5 DATETIME\n"
                    + ")\n"
                    + "ENGINE = mysql\n"
                    + "PROPERTIES\n"
                    + "(\n"
                    + "    \"host\" = \"127.0.0.1\",\n"
                    + "    \"port\" = \"8239\",\n"
                    + "    \"user\" = \"mysql_user\",\n"
                    + "    \"password\" = \"mysql_passwd\",\n"
                    + "    \"database\" = \"mysql_db_test\",\n"
                    + "    \"table\" = \"mysql_table_test\"\n"
                    + ");",
                "CREATE TABLE example_db.example_table\n"
                    + "(\n"
                    + "    k1 TINYINT,\n"
                    + "    k2 DECIMAL(10, 2) DEFAULT \"10.5\",\n"
                    + "    v1 HLL HLL_UNION,\n"
                    + "    v2 HLL HLL_UNION\n"
                    + ")\n"
                    + "ENGINE = olap\n"
                    + "AGGREGATE KEY(k1, k2)\n"
                    + "DISTRIBUTED BY HASH(k1)\n"
                    + "PROPERTIES (\"storage_type\" = \"column\");",
                "CREATE TABLE example_db.example_table\n"
                    + "(\n"
                    + "    k1 TINYINT,\n"
                    + "    k2 DECIMAL(10, 2) DEFAULT \"10.5\",\n"
                    + "    v1 BITMAP BITMAP_UNION,\n"
                    + "    v2 BITMAP BITMAP_UNION\n"
                    + ")\n"
                    + "ENGINE = olap\n"
                    + "AGGREGATE KEY(k1, k2)\n"
                    + "DISTRIBUTED BY HASH(k1)\n"
                    + "PROPERTIES (\"storage_type\" = \"column\");",
                "CREATE TABLE `t1` (\n"
                    + "    `id` int(11) COMMENT \"\",\n"
                    + "    `value` varchar(8) COMMENT \"\"\n"
                    + ") ENGINE = OLAP\n"
                    + "DUPLICATE KEY(`id`)\n"
                    + "DISTRIBUTED BY HASH(`id`)\n"
                    + "PROPERTIES (\n"
                    + "    \"colocate_with\" = \"t1\"\n"
                    + ");\n"
                    + "\n"
                    + "CREATE TABLE `t2` (\n"
                    + "    `id` int(11) COMMENT \"\",\n"
                    + "    `value` varchar(8) COMMENT \"\"\n"
                    + ") ENGINE = OLAP\n"
                    + "DUPLICATE KEY(`id`)\n"
                    + "DISTRIBUTED BY HASH(`id`)\n"
                    + "PROPERTIES (\n"
                    + "    \"colocate_with\" = \"t1\"\n"
                    + ");",
                "CREATE TABLE example_db.table_hash\n"
                    + "(\n"
                    + "    k1 TINYINT,\n"
                    + "    k2 DECIMAL(10, 2) DEFAULT \"10.5\",\n"
                    + "    v1 CHAR(10) REPLACE,\n"
                    + "    v2 INT SUM,\n"
                    + "    INDEX k1_idx (k1) USING BITMAP COMMENT 'xxxxxx'\n"
                    + ")\n"
                    + "ENGINE = olap\n"
                    + "AGGREGATE KEY(k1, k2)\n"
                    + "COMMENT \"my first starrocks table\"\n"
                    + "DISTRIBUTED BY HASH(k1)\n"
                    + "PROPERTIES (\"storage_type\" = \"column\");",
                "CREATE EXTERNAL TABLE example_db.table_hive\n"
                    + "(\n"
                    + "    k1 TINYINT,\n"
                    + "    k2 VARCHAR(50),\n"
                    + "    v INT\n"
                    + ")\n"
                    + "ENGINE = hive\n"
                    + "PROPERTIES\n"
                    + "(\n"
                    + "    \"resource\" = \"hive0\",\n"
                    + "    \"database\" = \"hive_db_name\",\n"
                    + "    \"table\" = \"hive_table_name\"\n"
                    + ");",
                "create table users (\n"
                    + "    user_id bigint NOT NULL,\n"
                    + "    name string NOT NULL,\n"
                    + "    email string NULL,\n"
                    + "    address string NULL,\n"
                    + "    age tinyint NULL,\n"
                    + "    sex tinyint NULL,\n"
                    + "    last_active datetime,\n"
                    + "    property0 tinyint NOT NULL,\n"
                    + "    property1 tinyint NOT NULL,\n"
                    + "    property2 tinyint NOT NULL,\n"
                    + "    property3 tinyint NOT NULL\n"
                    + ") \n"
                    + "PRIMARY KEY (`user_id`)\n"
                    + "DISTRIBUTED BY HASH(`user_id`)\n"
                    + "ORDER BY(`address`,`last_active`)\n"
                    + "PROPERTIES(\n"
                    + "    \"replication_num\" = \"3\",\n"
                    + "    \"enable_persistent_index\" = \"true\"\n"
                    + ");",
            }) {
                SQLStatementParser parser = SQLParserUtils.createSQLStatementParser(sql, dbType);
                System.out.println("当前原始sql========"+sql);
                List<SQLStatement> statementList = parser.parseStatementList();
                //assertEquals(1, statementList.size());
                SQLParseAssertUtil.assertParseSql(sql, dbType);
            }
        }
    }
}
