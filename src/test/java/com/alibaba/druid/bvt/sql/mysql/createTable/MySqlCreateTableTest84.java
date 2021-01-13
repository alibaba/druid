/*
 * Copyright 1999-2017 Alibaba Group Holding Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.alibaba.druid.bvt.sql.mysql.createTable;

import com.alibaba.druid.sql.MysqlTest;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.mysql.parser.MySqlStatementParser;
import com.alibaba.druid.sql.dialect.mysql.visitor.MySqlSchemaStatVisitor;
import com.alibaba.druid.sql.parser.SQLParserFeature;
import com.alibaba.druid.sql.repository.SchemaRepository;
import com.alibaba.druid.util.JdbcConstants;

public class MySqlCreateTableTest84 extends MysqlTest {

    public void test_one() throws Exception {
        String sql = "CREATE TABLE `test` (\n" +
                "  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT 'id',\n" +
                "  `c_tinyint` tinyint(4) DEFAULT '1' COMMENT 'tinyint',\n" +
                "  `c_smallint` smallint(6) DEFAULT 0 COMMENT 'smallint',\n" +
                "  `c_mediumint` mediumint(9) DEFAULT NULL COMMENT 'mediumint',\n" +
                "  `c_int` int(11) DEFAULT NULL COMMENT 'int',\n" +
                "  `c_bigint` bigint(20) DEFAULT NULL COMMENT 'bigint',\n" +
                "  `c_decimal` decimal(10,3) DEFAULT NULL COMMENT 'decimal',\n" +
                "  `c_date` date DEFAULT '0000-00-00' COMMENT 'date',\n" +
                "  `c_datetime` datetime DEFAULT '0000-00-00 00:00:00' COMMENT 'datetime',\n" +
                "  `c_timestamp` timestamp NULL DEFAULT NULL COMMENT 'timestamp',\n" +
                "  `c_time` time DEFAULT NULL COMMENT 'time',\n" +
                "  `c_char` char(10) DEFAULT NULL COMMENT 'char',\n" +
                "  `c_varchar` varchar(10) DEFAULT 'hello' COMMENT 'varchar',\n" +
                "  `c_blob` blob COMMENT 'blob',\n" +
                "  `c_text` text COMMENT 'text',\n" +
                "  `c_mediumtext` mediumtext COMMENT 'mediumtext',\n" +
                "  `c_longblob` longblob COMMENT 'longblob',\n" +
                "  PRIMARY KEY (`id`),\n" +
                "  UNIQUE KEY `uk_a` (`c_tinyint`),\n" +
                "  KEY `k_b` (`c_smallint`),\n" +
                "  KEY `k_c` (`c_mediumint`,`c_int`)\n" +
                ") ENGINE=InnoDB AUTO_INCREMENT=1769503 DEFAULT CHARSET=utf8mb4 COMMENT='10000000';";

        MySqlStatementParser parser = new MySqlStatementParser(sql, SQLParserFeature.KeepComments);
        SQLStatement stmt = parser.parseCreateTable();

        MySqlSchemaStatVisitor visitor = new MySqlSchemaStatVisitor();
        stmt.accept(visitor);
//
//        Column column = visitor.getColumn("tb_custom_vip_show_message", "custom_vip_show_message_seq");
//        assertNotNull(column);
//        assertEquals("INT", column.getDataType());
        System.out.println(stmt);

        {
            String output = SQLUtils.toMySqlString(stmt);
            assertEquals("CREATE TABLE `test` (\n" +
                    "\t`id` bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT 'id',\n" +
                    "\t`c_tinyint` tinyint(4) DEFAULT '1' COMMENT 'tinyint',\n" +
                    "\t`c_smallint` smallint(6) DEFAULT 0 COMMENT 'smallint',\n" +
                    "\t`c_mediumint` mediumint(9) DEFAULT NULL COMMENT 'mediumint',\n" +
                    "\t`c_int` int(11) DEFAULT NULL COMMENT 'int',\n" +
                    "\t`c_bigint` bigint(20) DEFAULT NULL COMMENT 'bigint',\n" +
                    "\t`c_decimal` decimal(10, 3) DEFAULT NULL COMMENT 'decimal',\n" +
                    "\t`c_date` date DEFAULT '0000-00-00' COMMENT 'date',\n" +
                    "\t`c_datetime` datetime DEFAULT '0000-00-00 00:00:00' COMMENT 'datetime',\n" +
                    "\t`c_timestamp` timestamp NULL DEFAULT NULL COMMENT 'timestamp',\n" +
                    "\t`c_time` time DEFAULT NULL COMMENT 'time',\n" +
                    "\t`c_char` char(10) DEFAULT NULL COMMENT 'char',\n" +
                    "\t`c_varchar` varchar(10) DEFAULT 'hello' COMMENT 'varchar',\n" +
                    "\t`c_blob` blob COMMENT 'blob',\n" +
                    "\t`c_text` text COMMENT 'text',\n" +
                    "\t`c_mediumtext` mediumtext COMMENT 'mediumtext',\n" +
                    "\t`c_longblob` longblob COMMENT 'longblob',\n" +
                    "\tPRIMARY KEY (`id`),\n" +
                    "\tUNIQUE KEY `uk_a` (`c_tinyint`),\n" +
                    "\tKEY `k_b` (`c_smallint`),\n" +
                    "\tKEY `k_c` (`c_mediumint`, `c_int`)\n" +
                    ") ENGINE = InnoDB AUTO_INCREMENT = 1769503 CHARSET = utf8mb4 COMMENT '10000000'", output);
        }
        
        {
            String output = SQLUtils.toMySqlString(stmt, SQLUtils.DEFAULT_LCASE_FORMAT_OPTION);
            assertEquals("create table `test` (\n" +
                    "\t`id` bigint(20) unsigned not null auto_increment comment 'id',\n" +
                    "\t`c_tinyint` tinyint(4) default '1' comment 'tinyint',\n" +
                    "\t`c_smallint` smallint(6) default 0 comment 'smallint',\n" +
                    "\t`c_mediumint` mediumint(9) default null comment 'mediumint',\n" +
                    "\t`c_int` int(11) default null comment 'int',\n" +
                    "\t`c_bigint` bigint(20) default null comment 'bigint',\n" +
                    "\t`c_decimal` decimal(10, 3) default null comment 'decimal',\n" +
                    "\t`c_date` date default '0000-00-00' comment 'date',\n" +
                    "\t`c_datetime` datetime default '0000-00-00 00:00:00' comment 'datetime',\n" +
                    "\t`c_timestamp` timestamp null default null comment 'timestamp',\n" +
                    "\t`c_time` time default null comment 'time',\n" +
                    "\t`c_char` char(10) default null comment 'char',\n" +
                    "\t`c_varchar` varchar(10) default 'hello' comment 'varchar',\n" +
                    "\t`c_blob` blob comment 'blob',\n" +
                    "\t`c_text` text comment 'text',\n" +
                    "\t`c_mediumtext` mediumtext comment 'mediumtext',\n" +
                    "\t`c_longblob` longblob comment 'longblob',\n" +
                    "\tprimary key (`id`),\n" +
                    "\tunique key `uk_a` (`c_tinyint`),\n" +
                    "\tkey `k_b` (`c_smallint`),\n" +
                    "\tkey `k_c` (`c_mediumint`, `c_int`)\n" +
                    ") engine = InnoDB auto_increment = 1769503 charset = utf8mb4 comment '10000000'", output);
        }

        SchemaRepository repository = new SchemaRepository(JdbcConstants.MYSQL);
        repository.console(sql);
        assertEquals("CREATE TABLE `test` (\n" +
                "\t`id` bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT 'id',\n" +
                "\t`c_tinyint` tinyint(4) DEFAULT '1' COMMENT 'tinyint',\n" +
                "\t`c_smallint` smallint(6) DEFAULT 0 COMMENT 'smallint',\n" +
                "\t`c_mediumint` mediumint(9) DEFAULT NULL COMMENT 'mediumint',\n" +
                "\t`c_int` int(11) DEFAULT NULL COMMENT 'int',\n" +
                "\t`c_bigint` bigint(20) DEFAULT NULL COMMENT 'bigint',\n" +
                "\t`c_decimal` decimal(10, 3) DEFAULT NULL COMMENT 'decimal',\n" +
                "\t`c_date` date DEFAULT '0000-00-00' COMMENT 'date',\n" +
                "\t`c_datetime` datetime DEFAULT '0000-00-00 00:00:00' COMMENT 'datetime',\n" +
                "\t`c_timestamp` timestamp NULL DEFAULT NULL COMMENT 'timestamp',\n" +
                "\t`c_time` time DEFAULT NULL COMMENT 'time',\n" +
                "\t`c_char` char(10) DEFAULT NULL COMMENT 'char',\n" +
                "\t`c_varchar` varchar(10) DEFAULT 'hello' COMMENT 'varchar',\n" +
                "\t`c_blob` blob COMMENT 'blob',\n" +
                "\t`c_text` text COMMENT 'text',\n" +
                "\t`c_mediumtext` mediumtext COMMENT 'mediumtext',\n" +
                "\t`c_longblob` longblob COMMENT 'longblob',\n" +
                "\tPRIMARY KEY (`id`),\n" +
                "\tUNIQUE KEY `uk_a` (`c_tinyint`),\n" +
                "\tKEY `k_b` (`c_smallint`),\n" +
                "\tKEY `k_c` (`c_mediumint`, `c_int`)\n" +
                ") ENGINE = InnoDB AUTO_INCREMENT = 1769503 CHARSET = utf8mb4 COMMENT '10000000'"
                , repository.console("show create table test"));
    }
}
