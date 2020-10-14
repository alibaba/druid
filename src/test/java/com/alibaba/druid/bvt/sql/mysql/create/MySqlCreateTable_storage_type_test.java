package com.alibaba.druid.bvt.sql.mysql.create;

import com.alibaba.druid.sql.MysqlTest;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlCreateTableStatement;
import com.alibaba.druid.util.JdbcConstants;

import java.util.List;

public class MySqlCreateTable_storage_type_test extends MysqlTest {

    public void test_0() throws Exception {
        String sql = "CREATE TABLE `test1` (\n" +
                "  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',\n" +
                "  `c_tinyint` tinyint(4) DEFAULT '1' COMMENT 'tinyint',\n" +
                "  `c_smallint` smallint(6) DEFAULT 0 COMMENT 'smallint',\n" +
                "  `c_mediumint` mediumint(9) DEFAULT NULL COMMENT 'mediumint',\n" +
                "  `c_int` int(11) DEFAULT NULL COMMENT 'int',\n" +
                "  `c_bigint` bigint(20) DEFAULT NULL COMMENT 'bigint',\n" +
                "  `c_decimal` decimal(10,3) DEFAULT NULL COMMENT 'decimal',\n" +
                "  `c_date` date DEFAULT '0000-00-00' COMMENT 'date',\n" +
                "  `c_datetime` datetime DEFAULT '0000-00-00 00:00:00' COMMENT 'datetime',\n" +
                "  `c_timestamp` timestamp NULL DEFAULT NULL COMMENT 'timestamp'  ON UPDATE CURRENT_TIMESTAMP ,\n" +
                "  `c_time` time DEFAULT NULL COMMENT 'time',\n" +
                "  `c_char` char(10) DEFAULT NULL COMMENT 'char',\n" +
                "  `c_varchar` varchar(10) DEFAULT 'hello' COMMENT 'varchar',\n" +
                "  `c_blob` blob COMMENT 'blob',\n" +
                "  `c_text` text COMMENT 'text',\n" +
                "  `c_mediumtext` mediumtext COMMENT 'mediumtext',\n" +
                "  `c_longblob` longblob COMMENT 'longblob',\n" +
                "  PRIMARY KEY (`id`,`c_tinyint`),\n" +
                "  UNIQUE KEY `uk_a` (`c_varchar`,`c_mediumint`),\n" +
                "  KEY `k_c` (`c_tinyint`,`c_int`),\n" +
                "  KEY `k_d` (`c_char`,`c_bigint`)\n" +
                ") ENGINE=InnoDB AUTO_INCREMENT=1769503 DEFAULT CHARSET=utf8mb4 COMMENT='10000000' storage_type = 'oss'";

        List<SQLStatement> statementList = SQLUtils.parseStatements(sql, JdbcConstants.MYSQL, true);

        assertEquals(1, statementList.size());

        MySqlCreateTableStatement stmt = (MySqlCreateTableStatement) statementList.get(0);

        assertEquals("CREATE TABLE `test1` (\n" +
                "\t`id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',\n" +
                "\t`c_tinyint` tinyint(4) DEFAULT '1' COMMENT 'tinyint',\n" +
                "\t`c_smallint` smallint(6) DEFAULT 0 COMMENT 'smallint',\n" +
                "\t`c_mediumint` mediumint(9) DEFAULT NULL COMMENT 'mediumint',\n" +
                "\t`c_int` int(11) DEFAULT NULL COMMENT 'int',\n" +
                "\t`c_bigint` bigint(20) DEFAULT NULL COMMENT 'bigint',\n" +
                "\t`c_decimal` decimal(10, 3) DEFAULT NULL COMMENT 'decimal',\n" +
                "\t`c_date` date DEFAULT '0000-00-00' COMMENT 'date',\n" +
                "\t`c_datetime` datetime DEFAULT '0000-00-00 00:00:00' COMMENT 'datetime',\n" +
                "\t`c_timestamp` timestamp NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT 'timestamp',\n" +
                "\t`c_time` time DEFAULT NULL COMMENT 'time',\n" +
                "\t`c_char` char(10) DEFAULT NULL COMMENT 'char',\n" +
                "\t`c_varchar` varchar(10) DEFAULT 'hello' COMMENT 'varchar',\n" +
                "\t`c_blob` blob COMMENT 'blob',\n" +
                "\t`c_text` text COMMENT 'text',\n" +
                "\t`c_mediumtext` mediumtext COMMENT 'mediumtext',\n" +
                "\t`c_longblob` longblob COMMENT 'longblob',\n" +
                "\tPRIMARY KEY (`id`, `c_tinyint`),\n" +
                "\tUNIQUE KEY `uk_a` (`c_varchar`, `c_mediumint`),\n" +
                "\tKEY `k_c` (`c_tinyint`, `c_int`),\n" +
                "\tKEY `k_d` (`c_char`, `c_bigint`)\n" +
                ") ENGINE = InnoDB AUTO_INCREMENT = 1769503 CHARSET = utf8mb4 STORAGE_TYPE = 'oss' COMMENT '10000000'", stmt.toString());
    }

    public void test_1() throws Exception {
        String sql = "CREATE TABLE `test1` (\n" +
                "  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',\n" +
                "  `c_tinyint` tinyint(4) DEFAULT '1' COMMENT 'tinyint',\n" +
                "  `c_smallint` smallint(6) DEFAULT 0 COMMENT 'smallint',\n" +
                "  `c_mediumint` mediumint(9) DEFAULT NULL COMMENT 'mediumint',\n" +
                "  `c_int` int(11) DEFAULT NULL COMMENT 'int',\n" +
                "  `c_bigint` bigint(20) DEFAULT NULL COMMENT 'bigint',\n" +
                "  `c_decimal` decimal(10,3) DEFAULT NULL COMMENT 'decimal',\n" +
                "  `c_date` date DEFAULT '0000-00-00' COMMENT 'date',\n" +
                "  `c_datetime` datetime DEFAULT '0000-00-00 00:00:00' COMMENT 'datetime',\n" +
                "  `c_timestamp` timestamp NULL DEFAULT NULL COMMENT 'timestamp'  ON UPDATE CURRENT_TIMESTAMP ,\n" +
                "  `c_time` time DEFAULT NULL COMMENT 'time',\n" +
                "  `c_char` char(10) DEFAULT NULL COMMENT 'char',\n" +
                "  `c_varchar` varchar(10) DEFAULT 'hello' COMMENT 'varchar',\n" +
                "  `c_blob` blob COMMENT 'blob',\n" +
                "  `c_text` text COMMENT 'text',\n" +
                "  `c_mediumtext` mediumtext COMMENT 'mediumtext',\n" +
                "  `c_longblob` longblob COMMENT 'longblob',\n" +
                "  PRIMARY KEY (`id`,`c_tinyint`),\n" +
                "  UNIQUE KEY `uk_a` (`c_varchar`,`c_mediumint`),\n" +
                "  KEY `k_c` (`c_tinyint`,`c_int`),\n" +
                "  KEY `k_d` (`c_char`,`c_bigint`)\n" +
                ") ENGINE=InnoDB AUTO_INCREMENT=1769503 DEFAULT CHARSET=utf8mb4 COMMENT='10000000' storage_policy = 'hot'";

        List<SQLStatement> statementList = SQLUtils.parseStatements(sql, JdbcConstants.MYSQL, true);

        assertEquals(1, statementList.size());

        MySqlCreateTableStatement stmt = (MySqlCreateTableStatement) statementList.get(0);

        assertEquals("CREATE TABLE `test1` (\n" +
                "\t`id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',\n" +
                "\t`c_tinyint` tinyint(4) DEFAULT '1' COMMENT 'tinyint',\n" +
                "\t`c_smallint` smallint(6) DEFAULT 0 COMMENT 'smallint',\n" +
                "\t`c_mediumint` mediumint(9) DEFAULT NULL COMMENT 'mediumint',\n" +
                "\t`c_int` int(11) DEFAULT NULL COMMENT 'int',\n" +
                "\t`c_bigint` bigint(20) DEFAULT NULL COMMENT 'bigint',\n" +
                "\t`c_decimal` decimal(10, 3) DEFAULT NULL COMMENT 'decimal',\n" +
                "\t`c_date` date DEFAULT '0000-00-00' COMMENT 'date',\n" +
                "\t`c_datetime` datetime DEFAULT '0000-00-00 00:00:00' COMMENT 'datetime',\n" +
                "\t`c_timestamp` timestamp NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT 'timestamp',\n" +
                "\t`c_time` time DEFAULT NULL COMMENT 'time',\n" +
                "\t`c_char` char(10) DEFAULT NULL COMMENT 'char',\n" +
                "\t`c_varchar` varchar(10) DEFAULT 'hello' COMMENT 'varchar',\n" +
                "\t`c_blob` blob COMMENT 'blob',\n" +
                "\t`c_text` text COMMENT 'text',\n" +
                "\t`c_mediumtext` mediumtext COMMENT 'mediumtext',\n" +
                "\t`c_longblob` longblob COMMENT 'longblob',\n" +
                "\tPRIMARY KEY (`id`, `c_tinyint`),\n" +
                "\tUNIQUE KEY `uk_a` (`c_varchar`, `c_mediumint`),\n" +
                "\tKEY `k_c` (`c_tinyint`, `c_int`),\n" +
                "\tKEY `k_d` (`c_char`, `c_bigint`)\n" +
                ") ENGINE = InnoDB AUTO_INCREMENT = 1769503 CHARSET = utf8mb4 STORAGE_POLICY = 'hot' COMMENT '10000000'", stmt.toString());
    }

}
