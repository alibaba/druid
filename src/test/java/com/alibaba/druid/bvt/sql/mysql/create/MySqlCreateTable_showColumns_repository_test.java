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
package com.alibaba.druid.bvt.sql.mysql.create;

import com.alibaba.druid.sql.MysqlTest;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlCreateTableStatement;
import com.alibaba.druid.sql.repository.SchemaRepository;
import com.alibaba.druid.util.JdbcConstants;

public class MySqlCreateTable_showColumns_repository_test extends MysqlTest {
    private SchemaRepository repository = new SchemaRepository(JdbcConstants.MYSQL);


    public void test_0() throws Exception {
        repository.console("use sc00;");

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
                ") ENGINE=InnoDB AUTO_INCREMENT=1769503 DEFAULT CHARSET=utf8mb4 COMMENT='10000000'";


        repository.console(sql);

        MySqlCreateTableStatement createTableStmt = (MySqlCreateTableStatement) repository.findTable("test1").getStatement();
        assertEquals(21, createTableStmt.getTableElementList().size());

        //StringBuilder buf = new StringBuilder();
        //createTableStmt.showCoumns(buf);

        assertEquals("+--------------+---------------+------+-----+---------------------+-----------------------------+\n" +
                "| Field        | Type          | Null | Key | Default             | Extra                       |\n" +
                "+--------------+---------------+------+-----+---------------------+-----------------------------+\n" +
                "| id           | bigint(20)    | NO   | PRI | NULL                | auto_increment              |\n" +
                "| c_tinyint    | tinyint(4)    | YES  | PRI | 1                   |                             |\n" +
                "| c_smallint   | smallint(6)   | YES  |     | 0                   |                             |\n" +
                "| c_mediumint  | mediumint(9)  | YES  |     | NULL                |                             |\n" +
                "| c_int        | int(11)       | YES  |     | NULL                |                             |\n" +
                "| c_bigint     | bigint(20)    | YES  |     | NULL                |                             |\n" +
                "| c_decimal    | decimal(10,3) | YES  |     | NULL                |                             |\n" +
                "| c_date       | date          | YES  |     | 0000-00-00          |                             |\n" +
                "| c_datetime   | datetime      | YES  |     | 0000-00-00 00:00:00 |                             |\n" +
                "| c_timestamp  | timestamp     | YES  |     | NULL                | on update CURRENT_TIMESTAMP |\n" +
                "| c_time       | time          | YES  |     | NULL                |                             |\n" +
                "| c_char       | char(10)      | YES  | MUL | NULL                |                             |\n" +
                "| c_varchar    | varchar(10)   | YES  | MUL | hello               |                             |\n" +
                "| c_blob       | blob          | YES  |     | NULL                |                             |\n" +
                "| c_text       | text          | YES  |     | NULL                |                             |\n" +
                "| c_mediumtext | mediumtext    | YES  |     | NULL                |                             |\n" +
                "| c_longblob   | longblob      | YES  |     | NULL                |                             |\n" +
                "+--------------+---------------+------+-----+---------------------+-----------------------------+\n", repository.console("show columns from test1"));

        repository.console("alter table test1 drop column c_decimal;");
        assertEquals(20, createTableStmt.getTableElementList().size());

        assertEquals("+--------------+--------------+------+-----+---------------------+-----------------------------+\n" +
                "| Field        | Type         | Null | Key | Default             | Extra                       |\n" +
                "+--------------+--------------+------+-----+---------------------+-----------------------------+\n" +
                "| id           | bigint(20)   | NO   | PRI | NULL                | auto_increment              |\n" +
                "| c_tinyint    | tinyint(4)   | YES  | PRI | 1                   |                             |\n" +
                "| c_smallint   | smallint(6)  | YES  |     | 0                   |                             |\n" +
                "| c_mediumint  | mediumint(9) | YES  |     | NULL                |                             |\n" +
                "| c_int        | int(11)      | YES  |     | NULL                |                             |\n" +
                "| c_bigint     | bigint(20)   | YES  |     | NULL                |                             |\n" +
                "| c_date       | date         | YES  |     | 0000-00-00          |                             |\n" +
                "| c_datetime   | datetime     | YES  |     | 0000-00-00 00:00:00 |                             |\n" +
                "| c_timestamp  | timestamp    | YES  |     | NULL                | on update CURRENT_TIMESTAMP |\n" +
                "| c_time       | time         | YES  |     | NULL                |                             |\n" +
                "| c_char       | char(10)     | YES  | MUL | NULL                |                             |\n" +
                "| c_varchar    | varchar(10)  | YES  | MUL | hello               |                             |\n" +
                "| c_blob       | blob         | YES  |     | NULL                |                             |\n" +
                "| c_text       | text         | YES  |     | NULL                |                             |\n" +
                "| c_mediumtext | mediumtext   | YES  |     | NULL                |                             |\n" +
                "| c_longblob   | longblob     | YES  |     | NULL                |                             |\n" +
                "+--------------+--------------+------+-----+---------------------+-----------------------------+\n", repository.console("show columns from test1"));

        repository.console("alter table test1 add column c_decimal decimal(10,3) DEFAULT NULL COMMENT 'decimal';");
        assertEquals(21, createTableStmt.getTableElementList().size());

        assertEquals("+--------------+---------------+------+-----+---------------------+-----------------------------+\n" +
                "| Field        | Type          | Null | Key | Default             | Extra                       |\n" +
                "+--------------+---------------+------+-----+---------------------+-----------------------------+\n" +
                "| id           | bigint(20)    | NO   | PRI | NULL                | auto_increment              |\n" +
                "| c_tinyint    | tinyint(4)    | YES  | PRI | 1                   |                             |\n" +
                "| c_smallint   | smallint(6)   | YES  |     | 0                   |                             |\n" +
                "| c_mediumint  | mediumint(9)  | YES  |     | NULL                |                             |\n" +
                "| c_int        | int(11)       | YES  |     | NULL                |                             |\n" +
                "| c_bigint     | bigint(20)    | YES  |     | NULL                |                             |\n" +
                "| c_date       | date          | YES  |     | 0000-00-00          |                             |\n" +
                "| c_datetime   | datetime      | YES  |     | 0000-00-00 00:00:00 |                             |\n" +
                "| c_timestamp  | timestamp     | YES  |     | NULL                | on update CURRENT_TIMESTAMP |\n" +
                "| c_time       | time          | YES  |     | NULL                |                             |\n" +
                "| c_char       | char(10)      | YES  | MUL | NULL                |                             |\n" +
                "| c_varchar    | varchar(10)   | YES  | MUL | hello               |                             |\n" +
                "| c_blob       | blob          | YES  |     | NULL                |                             |\n" +
                "| c_text       | text          | YES  |     | NULL                |                             |\n" +
                "| c_mediumtext | mediumtext    | YES  |     | NULL                |                             |\n" +
                "| c_longblob   | longblob      | YES  |     | NULL                |                             |\n" +
                "| c_decimal    | decimal(10,3) | YES  |     | NULL                |                             |\n" +
                "+--------------+---------------+------+-----+---------------------+-----------------------------+\n", repository.console("show columns from test1"));

        repository.console("ALTER TABLE test1 CHANGE COLUMN c_decimal c_decimal_1 INT(11) NOT NULL DEFAULT NULL FIRST id;");
        assertEquals(21, createTableStmt.getTableElementList().size());
        //String sql = "ALTER TABLE `test`.`tb1` CHANGE COLUMN `fid` `fid` INT(11) NOT NULL DEFAULT NULL, ADD PRIMARY KEY (`fid`) ;";

        assertEquals("+--------------+--------------+------+-----+---------------------+-----------------------------+\n" +
                "| Field        | Type         | Null | Key | Default             | Extra                       |\n" +
                "+--------------+--------------+------+-----+---------------------+-----------------------------+\n" +
                "| c_decimal_1  | INT(11)      | NO   |     | NULL                |                             |\n" +
                "| id           | bigint(20)   | NO   | PRI | NULL                | auto_increment              |\n" +
                "| c_tinyint    | tinyint(4)   | YES  | PRI | 1                   |                             |\n" +
                "| c_smallint   | smallint(6)  | YES  |     | 0                   |                             |\n" +
                "| c_mediumint  | mediumint(9) | YES  |     | NULL                |                             |\n" +
                "| c_int        | int(11)      | YES  |     | NULL                |                             |\n" +
                "| c_bigint     | bigint(20)   | YES  |     | NULL                |                             |\n" +
                "| c_date       | date         | YES  |     | 0000-00-00          |                             |\n" +
                "| c_datetime   | datetime     | YES  |     | 0000-00-00 00:00:00 |                             |\n" +
                "| c_timestamp  | timestamp    | YES  |     | NULL                | on update CURRENT_TIMESTAMP |\n" +
                "| c_time       | time         | YES  |     | NULL                |                             |\n" +
                "| c_char       | char(10)     | YES  | MUL | NULL                |                             |\n" +
                "| c_varchar    | varchar(10)  | YES  | MUL | hello               |                             |\n" +
                "| c_blob       | blob         | YES  |     | NULL                |                             |\n" +
                "| c_text       | text         | YES  |     | NULL                |                             |\n" +
                "| c_mediumtext | mediumtext   | YES  |     | NULL                |                             |\n" +
                "| c_longblob   | longblob     | YES  |     | NULL                |                             |\n" +
                "+--------------+--------------+------+-----+---------------------+-----------------------------+\n", repository.console("show columns from test1"));

        repository.console("ALTER TABLE test1 DROP PRIMARY KEY;");
        assertEquals(20, createTableStmt.getTableElementList().size());

        assertEquals("CREATE TABLE `test1` (\n" +
                "\tc_decimal_1 INT(11) NOT NULL DEFAULT NULL,\n" +
                "\t`id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',\n" +
                "\t`c_tinyint` tinyint(4) DEFAULT '1' COMMENT 'tinyint',\n" +
                "\t`c_smallint` smallint(6) DEFAULT 0 COMMENT 'smallint',\n" +
                "\t`c_mediumint` mediumint(9) DEFAULT NULL COMMENT 'mediumint',\n" +
                "\t`c_int` int(11) DEFAULT NULL COMMENT 'int',\n" +
                "\t`c_bigint` bigint(20) DEFAULT NULL COMMENT 'bigint',\n" +
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
                "\tUNIQUE KEY `uk_a` (`c_varchar`, `c_mediumint`),\n" +
                "\tKEY `k_c` (`c_tinyint`, `c_int`),\n" +
                "\tKEY `k_d` (`c_char`, `c_bigint`)\n" +
                ") ENGINE = InnoDB AUTO_INCREMENT = 1769503 CHARSET = utf8mb4 COMMENT '10000000'", repository.console("show create table test1"));


        assertEquals("+--------------+--------------+------+-----+---------------------+-----------------------------+\n" +
                "| Field        | Type         | Null | Key | Default             | Extra                       |\n" +
                "+--------------+--------------+------+-----+---------------------+-----------------------------+\n" +
                "| c_decimal_1  | INT(11)      | NO   |     | NULL                |                             |\n" +
                "| id           | bigint(20)   | NO   |     | NULL                | auto_increment              |\n" +
                "| c_tinyint    | tinyint(4)   | YES  | MUL | 1                   |                             |\n" +
                "| c_smallint   | smallint(6)  | YES  |     | 0                   |                             |\n" +
                "| c_mediumint  | mediumint(9) | YES  |     | NULL                |                             |\n" +
                "| c_int        | int(11)      | YES  |     | NULL                |                             |\n" +
                "| c_bigint     | bigint(20)   | YES  |     | NULL                |                             |\n" +
                "| c_date       | date         | YES  |     | 0000-00-00          |                             |\n" +
                "| c_datetime   | datetime     | YES  |     | 0000-00-00 00:00:00 |                             |\n" +
                "| c_timestamp  | timestamp    | YES  |     | NULL                | on update CURRENT_TIMESTAMP |\n" +
                "| c_time       | time         | YES  |     | NULL                |                             |\n" +
                "| c_char       | char(10)     | YES  | MUL | NULL                |                             |\n" +
                "| c_varchar    | varchar(10)  | YES  | MUL | hello               |                             |\n" +
                "| c_blob       | blob         | YES  |     | NULL                |                             |\n" +
                "| c_text       | text         | YES  |     | NULL                |                             |\n" +
                "| c_mediumtext | mediumtext   | YES  |     | NULL                |                             |\n" +
                "| c_longblob   | longblob     | YES  |     | NULL                |                             |\n" +
                "+--------------+--------------+------+-----+---------------------+-----------------------------+\n", repository.console("show columns from test1"));

        repository.console("ALTER TABLE test1 DROP INDEX k_d;");
        assertEquals(19, createTableStmt.getTableElementList().size());

        assertEquals("CREATE TABLE `test1` (\n" +
                "\tc_decimal_1 INT(11) NOT NULL DEFAULT NULL,\n" +
                "\t`id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',\n" +
                "\t`c_tinyint` tinyint(4) DEFAULT '1' COMMENT 'tinyint',\n" +
                "\t`c_smallint` smallint(6) DEFAULT 0 COMMENT 'smallint',\n" +
                "\t`c_mediumint` mediumint(9) DEFAULT NULL COMMENT 'mediumint',\n" +
                "\t`c_int` int(11) DEFAULT NULL COMMENT 'int',\n" +
                "\t`c_bigint` bigint(20) DEFAULT NULL COMMENT 'bigint',\n" +
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
                "\tUNIQUE KEY `uk_a` (`c_varchar`, `c_mediumint`),\n" +
                "\tKEY `k_c` (`c_tinyint`, `c_int`)\n" +
                ") ENGINE = InnoDB AUTO_INCREMENT = 1769503 CHARSET = utf8mb4 COMMENT '10000000'", repository.console("show create table test1"));

        assertEquals("+--------------+--------------+------+-----+---------------------+-----------------------------+\n" +
                "| Field        | Type         | Null | Key | Default             | Extra                       |\n" +
                "+--------------+--------------+------+-----+---------------------+-----------------------------+\n" +
                "| c_decimal_1  | INT(11)      | NO   |     | NULL                |                             |\n" +
                "| id           | bigint(20)   | NO   |     | NULL                | auto_increment              |\n" +
                "| c_tinyint    | tinyint(4)   | YES  | MUL | 1                   |                             |\n" +
                "| c_smallint   | smallint(6)  | YES  |     | 0                   |                             |\n" +
                "| c_mediumint  | mediumint(9) | YES  |     | NULL                |                             |\n" +
                "| c_int        | int(11)      | YES  |     | NULL                |                             |\n" +
                "| c_bigint     | bigint(20)   | YES  |     | NULL                |                             |\n" +
                "| c_date       | date         | YES  |     | 0000-00-00          |                             |\n" +
                "| c_datetime   | datetime     | YES  |     | 0000-00-00 00:00:00 |                             |\n" +
                "| c_timestamp  | timestamp    | YES  |     | NULL                | on update CURRENT_TIMESTAMP |\n" +
                "| c_time       | time         | YES  |     | NULL                |                             |\n" +
                "| c_char       | char(10)     | YES  |     | NULL                |                             |\n" +
                "| c_varchar    | varchar(10)  | YES  | MUL | hello               |                             |\n" +
                "| c_blob       | blob         | YES  |     | NULL                |                             |\n" +
                "| c_text       | text         | YES  |     | NULL                |                             |\n" +
                "| c_mediumtext | mediumtext   | YES  |     | NULL                |                             |\n" +
                "| c_longblob   | longblob     | YES  |     | NULL                |                             |\n" +
                "+--------------+--------------+------+-----+---------------------+-----------------------------+\n", repository.console("show columns from test1"));


        repository.console("ALTER TABLE test1 DROP INDEX uk_a;");
        assertEquals(18, createTableStmt.getTableElementList().size());

        assertEquals("CREATE TABLE `test1` (\n" +
                "\tc_decimal_1 INT(11) NOT NULL DEFAULT NULL,\n" +
                "\t`id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',\n" +
                "\t`c_tinyint` tinyint(4) DEFAULT '1' COMMENT 'tinyint',\n" +
                "\t`c_smallint` smallint(6) DEFAULT 0 COMMENT 'smallint',\n" +
                "\t`c_mediumint` mediumint(9) DEFAULT NULL COMMENT 'mediumint',\n" +
                "\t`c_int` int(11) DEFAULT NULL COMMENT 'int',\n" +
                "\t`c_bigint` bigint(20) DEFAULT NULL COMMENT 'bigint',\n" +
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
                "\tKEY `k_c` (`c_tinyint`, `c_int`)\n" +
                ") ENGINE = InnoDB AUTO_INCREMENT = 1769503 CHARSET = utf8mb4 COMMENT '10000000'", repository.console("show create table test1"));

        assertEquals("+--------------+--------------+------+-----+---------------------+-----------------------------+\n" +
                "| Field        | Type         | Null | Key | Default             | Extra                       |\n" +
                "+--------------+--------------+------+-----+---------------------+-----------------------------+\n" +
                "| c_decimal_1  | INT(11)      | NO   |     | NULL                |                             |\n" +
                "| id           | bigint(20)   | NO   |     | NULL                | auto_increment              |\n" +
                "| c_tinyint    | tinyint(4)   | YES  | MUL | 1                   |                             |\n" +
                "| c_smallint   | smallint(6)  | YES  |     | 0                   |                             |\n" +
                "| c_mediumint  | mediumint(9) | YES  |     | NULL                |                             |\n" +
                "| c_int        | int(11)      | YES  |     | NULL                |                             |\n" +
                "| c_bigint     | bigint(20)   | YES  |     | NULL                |                             |\n" +
                "| c_date       | date         | YES  |     | 0000-00-00          |                             |\n" +
                "| c_datetime   | datetime     | YES  |     | 0000-00-00 00:00:00 |                             |\n" +
                "| c_timestamp  | timestamp    | YES  |     | NULL                | on update CURRENT_TIMESTAMP |\n" +
                "| c_time       | time         | YES  |     | NULL                |                             |\n" +
                "| c_char       | char(10)     | YES  |     | NULL                |                             |\n" +
                "| c_varchar    | varchar(10)  | YES  |     | hello               |                             |\n" +
                "| c_blob       | blob         | YES  |     | NULL                |                             |\n" +
                "| c_text       | text         | YES  |     | NULL                |                             |\n" +
                "| c_mediumtext | mediumtext   | YES  |     | NULL                |                             |\n" +
                "| c_longblob   | longblob     | YES  |     | NULL                |                             |\n" +
                "+--------------+--------------+------+-----+---------------------+-----------------------------+\n", repository.console("show columns from test1"));

        repository.console("CREATE TABLE test2 like test1");

        assertEquals("CREATE TABLE test2 (\n" +
                "\tc_decimal_1 INT(11) NOT NULL DEFAULT NULL,\n" +
                "\t`id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',\n" +
                "\t`c_tinyint` tinyint(4) DEFAULT '1' COMMENT 'tinyint',\n" +
                "\t`c_smallint` smallint(6) DEFAULT 0 COMMENT 'smallint',\n" +
                "\t`c_mediumint` mediumint(9) DEFAULT NULL COMMENT 'mediumint',\n" +
                "\t`c_int` int(11) DEFAULT NULL COMMENT 'int',\n" +
                "\t`c_bigint` bigint(20) DEFAULT NULL COMMENT 'bigint',\n" +
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
                "\tKEY `k_c` (`c_tinyint`, `c_int`)\n" +
                ") ENGINE = InnoDB AUTO_INCREMENT = 1769503 CHARSET = utf8mb4 COMMENT '10000000'", repository.console("show create table test2"));

        repository.console("CREATE TABLE test3 as select * from test2");
        assertEquals("CREATE TABLE test3 (\n" +
                "\tc_decimal_1 INT(11) NOT NULL DEFAULT NULL,\n" +
                "\tid bigint(20) NOT NULL COMMENT 'id',\n" +
                "\tc_tinyint tinyint(4) DEFAULT '1' COMMENT 'tinyint',\n" +
                "\tc_smallint smallint(6) DEFAULT 0 COMMENT 'smallint',\n" +
                "\tc_mediumint mediumint(9) DEFAULT NULL COMMENT 'mediumint',\n" +
                "\tc_int int(11) DEFAULT NULL COMMENT 'int',\n" +
                "\tc_bigint bigint(20) DEFAULT NULL COMMENT 'bigint',\n" +
                "\tc_date date DEFAULT '0000-00-00' COMMENT 'date',\n" +
                "\tc_datetime datetime DEFAULT '0000-00-00 00:00:00' COMMENT 'datetime',\n" +
                "\tc_timestamp timestamp NULL DEFAULT NULL COMMENT 'timestamp',\n" +
                "\tc_time time DEFAULT NULL COMMENT 'time',\n" +
                "\tc_char char(10) DEFAULT NULL COMMENT 'char',\n" +
                "\tc_varchar varchar(10) DEFAULT 'hello' COMMENT 'varchar',\n" +
                "\tc_blob blob COMMENT 'blob',\n" +
                "\tc_text text COMMENT 'text',\n" +
                "\tc_mediumtext mediumtext COMMENT 'mediumtext',\n" +
                "\tc_longblob longblob COMMENT 'longblob'\n" +
                ")", repository.console("show create table test3"));

        assertEquals("+--------------+--------------+------+-----+---------------------+-------+\n" +
                "| Field        | Type         | Null | Key | Default             | Extra |\n" +
                "+--------------+--------------+------+-----+---------------------+-------+\n" +
                "| c_decimal_1  | INT(11)      | NO   |     | NULL                |       |\n" +
                "| id           | bigint(20)   | NO   |     | NULL                |       |\n" +
                "| c_tinyint    | tinyint(4)   | YES  |     | 1                   |       |\n" +
                "| c_smallint   | smallint(6)  | YES  |     | 0                   |       |\n" +
                "| c_mediumint  | mediumint(9) | YES  |     | NULL                |       |\n" +
                "| c_int        | int(11)      | YES  |     | NULL                |       |\n" +
                "| c_bigint     | bigint(20)   | YES  |     | NULL                |       |\n" +
                "| c_date       | date         | YES  |     | 0000-00-00          |       |\n" +
                "| c_datetime   | datetime     | YES  |     | 0000-00-00 00:00:00 |       |\n" +
                "| c_timestamp  | timestamp    | YES  |     | NULL                |       |\n" +
                "| c_time       | time         | YES  |     | NULL                |       |\n" +
                "| c_char       | char(10)     | YES  |     | NULL                |       |\n" +
                "| c_varchar    | varchar(10)  | YES  |     | hello               |       |\n" +
                "| c_blob       | blob         | YES  |     | NULL                |       |\n" +
                "| c_text       | text         | YES  |     | NULL                |       |\n" +
                "| c_mediumtext | mediumtext   | YES  |     | NULL                |       |\n" +
                "| c_longblob   | longblob     | YES  |     | NULL                |       |\n" +
                "+--------------+--------------+------+-----+---------------------+-------+\n", repository.console("show columns from test3"));

        repository.console("alter TABLE test3 add UNIQUE(c_tinyint)");

        assertEquals("+--------------+--------------+------+-----+---------------------+-------+\n" +
                "| Field        | Type         | Null | Key | Default             | Extra |\n" +
                "+--------------+--------------+------+-----+---------------------+-------+\n" +
                "| c_decimal_1  | INT(11)      | NO   |     | NULL                |       |\n" +
                "| id           | bigint(20)   | NO   |     | NULL                |       |\n" +
                "| c_tinyint    | tinyint(4)   | YES  | UNI | 1                   |       |\n" +
                "| c_smallint   | smallint(6)  | YES  |     | 0                   |       |\n" +
                "| c_mediumint  | mediumint(9) | YES  |     | NULL                |       |\n" +
                "| c_int        | int(11)      | YES  |     | NULL                |       |\n" +
                "| c_bigint     | bigint(20)   | YES  |     | NULL                |       |\n" +
                "| c_date       | date         | YES  |     | 0000-00-00          |       |\n" +
                "| c_datetime   | datetime     | YES  |     | 0000-00-00 00:00:00 |       |\n" +
                "| c_timestamp  | timestamp    | YES  |     | NULL                |       |\n" +
                "| c_time       | time         | YES  |     | NULL                |       |\n" +
                "| c_char       | char(10)     | YES  |     | NULL                |       |\n" +
                "| c_varchar    | varchar(10)  | YES  |     | hello               |       |\n" +
                "| c_blob       | blob         | YES  |     | NULL                |       |\n" +
                "| c_text       | text         | YES  |     | NULL                |       |\n" +
                "| c_mediumtext | mediumtext   | YES  |     | NULL                |       |\n" +
                "| c_longblob   | longblob     | YES  |     | NULL                |       |\n" +
                "+--------------+--------------+------+-----+---------------------+-------+\n", repository.console("show columns from test3"));

        repository.console("alter TABLE test3 add PRIMARY KEY(id)");

        assertEquals("+--------------+--------------+------+-----+---------------------+-------+\n" +
                "| Field        | Type         | Null | Key | Default             | Extra |\n" +
                "+--------------+--------------+------+-----+---------------------+-------+\n" +
                "| c_decimal_1  | INT(11)      | NO   |     | NULL                |       |\n" +
                "| id           | bigint(20)   | NO   | PRI | NULL                |       |\n" +
                "| c_tinyint    | tinyint(4)   | YES  | UNI | 1                   |       |\n" +
                "| c_smallint   | smallint(6)  | YES  |     | 0                   |       |\n" +
                "| c_mediumint  | mediumint(9) | YES  |     | NULL                |       |\n" +
                "| c_int        | int(11)      | YES  |     | NULL                |       |\n" +
                "| c_bigint     | bigint(20)   | YES  |     | NULL                |       |\n" +
                "| c_date       | date         | YES  |     | 0000-00-00          |       |\n" +
                "| c_datetime   | datetime     | YES  |     | 0000-00-00 00:00:00 |       |\n" +
                "| c_timestamp  | timestamp    | YES  |     | NULL                |       |\n" +
                "| c_time       | time         | YES  |     | NULL                |       |\n" +
                "| c_char       | char(10)     | YES  |     | NULL                |       |\n" +
                "| c_varchar    | varchar(10)  | YES  |     | hello               |       |\n" +
                "| c_blob       | blob         | YES  |     | NULL                |       |\n" +
                "| c_text       | text         | YES  |     | NULL                |       |\n" +
                "| c_mediumtext | mediumtext   | YES  |     | NULL                |       |\n" +
                "| c_longblob   | longblob     | YES  |     | NULL                |       |\n" +
                "+--------------+--------------+------+-----+---------------------+-------+\n", repository.console("show columns from test3"));

        repository.console("CREATE TABLE test4 as select a.* from test2 a");
        assertEquals("CREATE TABLE test4 (\n" +
                "\tc_decimal_1 INT(11) NOT NULL DEFAULT NULL,\n" +
                "\tid bigint(20) NOT NULL COMMENT 'id',\n" +
                "\tc_tinyint tinyint(4) DEFAULT '1' COMMENT 'tinyint',\n" +
                "\tc_smallint smallint(6) DEFAULT 0 COMMENT 'smallint',\n" +
                "\tc_mediumint mediumint(9) DEFAULT NULL COMMENT 'mediumint',\n" +
                "\tc_int int(11) DEFAULT NULL COMMENT 'int',\n" +
                "\tc_bigint bigint(20) DEFAULT NULL COMMENT 'bigint',\n" +
                "\tc_date date DEFAULT '0000-00-00' COMMENT 'date',\n" +
                "\tc_datetime datetime DEFAULT '0000-00-00 00:00:00' COMMENT 'datetime',\n" +
                "\tc_timestamp timestamp NULL DEFAULT NULL COMMENT 'timestamp',\n" +
                "\tc_time time DEFAULT NULL COMMENT 'time',\n" +
                "\tc_char char(10) DEFAULT NULL COMMENT 'char',\n" +
                "\tc_varchar varchar(10) DEFAULT 'hello' COMMENT 'varchar',\n" +
                "\tc_blob blob COMMENT 'blob',\n" +
                "\tc_text text COMMENT 'text',\n" +
                "\tc_mediumtext mediumtext COMMENT 'mediumtext',\n" +
                "\tc_longblob longblob COMMENT 'longblob'\n" +
                ")", repository.console("show create table test4"));

        repository.console("CREATE TABLE test5 as select a.* from test2 a union all select * from test3");
        assertEquals("CREATE TABLE test5 (\n" +
                "\tc_decimal_1 INT(11) NOT NULL DEFAULT NULL,\n" +
                "\tid bigint(20) NOT NULL COMMENT 'id',\n" +
                "\tc_tinyint tinyint(4) DEFAULT '1' COMMENT 'tinyint',\n" +
                "\tc_smallint smallint(6) DEFAULT 0 COMMENT 'smallint',\n" +
                "\tc_mediumint mediumint(9) DEFAULT NULL COMMENT 'mediumint',\n" +
                "\tc_int int(11) DEFAULT NULL COMMENT 'int',\n" +
                "\tc_bigint bigint(20) DEFAULT NULL COMMENT 'bigint',\n" +
                "\tc_date date DEFAULT '0000-00-00' COMMENT 'date',\n" +
                "\tc_datetime datetime DEFAULT '0000-00-00 00:00:00' COMMENT 'datetime',\n" +
                "\tc_timestamp timestamp NULL DEFAULT NULL COMMENT 'timestamp',\n" +
                "\tc_time time DEFAULT NULL COMMENT 'time',\n" +
                "\tc_char char(10) DEFAULT NULL COMMENT 'char',\n" +
                "\tc_varchar varchar(10) DEFAULT 'hello' COMMENT 'varchar',\n" +
                "\tc_blob blob COMMENT 'blob',\n" +
                "\tc_text text COMMENT 'text',\n" +
                "\tc_mediumtext mediumtext COMMENT 'mediumtext',\n" +
                "\tc_longblob longblob COMMENT 'longblob'\n" +
                ")", repository.console("show create table test5"));

        repository.console("CREATE TABLE sc01.test6 as select a.id, c_tinyint, 'a' f3, 3 f4 from test2 a");
        assertEquals("CREATE TABLE test6 (\n" +
                "\tid bigint(20) NOT NULL COMMENT 'id',\n" +
                "\tc_tinyint tinyint(4) DEFAULT '1' COMMENT 'tinyint',\n" +
                "\tf3 char,\n" +
                "\tf4 bigint\n" +
                ")", repository.console("show create table sc01.test6"));

        assertEquals("test1\n" +
                "test2\n" +
                "test3\n" +
                "test4\n" +
                "test5\n", repository.console("show tables"));

        assertEquals("test1\n" +
                "test2\n" +
                "test3\n" +
                "test4\n" +
                "test5\n", repository.console("show tables"));

        assertEquals("test6\n", repository.console("show tables from sc01"));

        repository.console("DROP /*!40005 TEMPORARY */ TABLE IF EXISTS `sc01`.`test6`;");

        assertEquals("\n", repository.console("show tables from sc01"));

        assertEquals("CREATE TABLE `test1` (\n" +
                "\tc_decimal_1 INT(11) NOT NULL DEFAULT NULL,\n" +
                "\t`id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',\n" +
                "\t`c_tinyint` tinyint(4) DEFAULT '1' COMMENT 'tinyint',\n" +
                "\t`c_smallint` smallint(6) DEFAULT 0 COMMENT 'smallint',\n" +
                "\t`c_mediumint` mediumint(9) DEFAULT NULL COMMENT 'mediumint',\n" +
                "\t`c_int` int(11) DEFAULT NULL COMMENT 'int',\n" +
                "\t`c_bigint` bigint(20) DEFAULT NULL COMMENT 'bigint',\n" +
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
                "\tKEY `k_c` (`c_tinyint`, `c_int`)\n" +
                ") ENGINE = InnoDB AUTO_INCREMENT = 1769503 CHARSET = utf8mb4 COMMENT '10000000'"

                , repository.console("show create table test1"));


        repository.console(" alter table test1 change c_tinyint c_tinyint_x tinyint(4) DEFAULT '1' COMMENT 'tinyint x' first;");

        assertEquals("CREATE TABLE `test1` (\n" +
                        "\tc_tinyint_x tinyint(4) DEFAULT '1' COMMENT 'tinyint x',\n" +
                        "\tc_decimal_1 INT(11) NOT NULL DEFAULT NULL,\n" +
                        "\t`id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',\n" +
                        "\t`c_smallint` smallint(6) DEFAULT 0 COMMENT 'smallint',\n" +
                        "\t`c_mediumint` mediumint(9) DEFAULT NULL COMMENT 'mediumint',\n" +
                        "\t`c_int` int(11) DEFAULT NULL COMMENT 'int',\n" +
                        "\t`c_bigint` bigint(20) DEFAULT NULL COMMENT 'bigint',\n" +
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
                        "\tKEY `k_c` (c_tinyint_x, `c_int`)\n" +
                        ") ENGINE = InnoDB AUTO_INCREMENT = 1769503 CHARSET = utf8mb4 COMMENT '10000000'"

                , repository.console("show create table test1"));

        repository.console(" alter table test1 drop c_tinyint_x");

        assertEquals("CREATE TABLE `test1` (\n" +
                        "\tc_decimal_1 INT(11) NOT NULL DEFAULT NULL,\n" +
                        "\t`id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',\n" +
                        "\t`c_smallint` smallint(6) DEFAULT 0 COMMENT 'smallint',\n" +
                        "\t`c_mediumint` mediumint(9) DEFAULT NULL COMMENT 'mediumint',\n" +
                        "\t`c_int` int(11) DEFAULT NULL COMMENT 'int',\n" +
                        "\t`c_bigint` bigint(20) DEFAULT NULL COMMENT 'bigint',\n" +
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
                        "\tKEY `k_c` (`c_int`)\n" +
                        ") ENGINE = InnoDB AUTO_INCREMENT = 1769503 CHARSET = utf8mb4 COMMENT '10000000'"

                , repository.console("show create table test1"));

        repository.console(" alter table test1 add index name_idx(c_char,c_date);");

        assertEquals("CREATE TABLE `test1` (\n" +
                        "\tc_decimal_1 INT(11) NOT NULL DEFAULT NULL,\n" +
                        "\t`id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',\n" +
                        "\t`c_smallint` smallint(6) DEFAULT 0 COMMENT 'smallint',\n" +
                        "\t`c_mediumint` mediumint(9) DEFAULT NULL COMMENT 'mediumint',\n" +
                        "\t`c_int` int(11) DEFAULT NULL COMMENT 'int',\n" +
                        "\t`c_bigint` bigint(20) DEFAULT NULL COMMENT 'bigint',\n" +
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
                        "\tKEY `k_c` (`c_int`),\n" +
                        "\tINDEX name_idx(c_char, c_date)\n" +
                        ") ENGINE = InnoDB AUTO_INCREMENT = 1769503 CHARSET = utf8mb4 COMMENT '10000000'"

                , repository.console("show create table test1"));


        assertEquals("+--------------+--------------+------+-----+---------------------+-----------------------------+\n" +
                        "| Field        | Type         | Null | Key | Default             | Extra                       |\n" +
                        "+--------------+--------------+------+-----+---------------------+-----------------------------+\n" +
                        "| c_decimal_1  | INT(11)      | NO   |     | NULL                |                             |\n" +
                        "| id           | bigint(20)   | NO   |     | NULL                | auto_increment              |\n" +
                        "| c_smallint   | smallint(6)  | YES  |     | 0                   |                             |\n" +
                        "| c_mediumint  | mediumint(9) | YES  |     | NULL                |                             |\n" +
                        "| c_int        | int(11)      | YES  | MUL | NULL                |                             |\n" +
                        "| c_bigint     | bigint(20)   | YES  |     | NULL                |                             |\n" +
                        "| c_date       | date         | YES  |     | 0000-00-00          |                             |\n" +
                        "| c_datetime   | datetime     | YES  |     | 0000-00-00 00:00:00 |                             |\n" +
                        "| c_timestamp  | timestamp    | YES  |     | NULL                | on update CURRENT_TIMESTAMP |\n" +
                        "| c_time       | time         | YES  |     | NULL                |                             |\n" +
                        "| c_char       | char(10)     | YES  |     | NULL                |                             |\n" +
                        "| c_varchar    | varchar(10)  | YES  |     | hello               |                             |\n" +
                        "| c_blob       | blob         | YES  |     | NULL                |                             |\n" +
                        "| c_text       | text         | YES  |     | NULL                |                             |\n" +
                        "| c_mediumtext | mediumtext   | YES  |     | NULL                |                             |\n" +
                        "| c_longblob   | longblob     | YES  |     | NULL                |                             |\n" +
                        "+--------------+--------------+------+-----+---------------------+-----------------------------+\n"

                , repository.console("show columns from test1"));


        repository.console(" drop index name_idx on test1");

        assertEquals("CREATE TABLE `test1` (\n" +
                        "\tc_decimal_1 INT(11) NOT NULL DEFAULT NULL,\n" +
                        "\t`id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',\n" +
                        "\t`c_smallint` smallint(6) DEFAULT 0 COMMENT 'smallint',\n" +
                        "\t`c_mediumint` mediumint(9) DEFAULT NULL COMMENT 'mediumint',\n" +
                        "\t`c_int` int(11) DEFAULT NULL COMMENT 'int',\n" +
                        "\t`c_bigint` bigint(20) DEFAULT NULL COMMENT 'bigint',\n" +
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
                        "\tKEY `k_c` (`c_int`)\n" +
                        ") ENGINE = InnoDB AUTO_INCREMENT = 1769503 CHARSET = utf8mb4 COMMENT '10000000'"

                , repository.console("show create table test1"));

        repository.console("alter table test1 modify c_char char(32) after id");

        assertEquals("CREATE TABLE `test1` (\n" +
                        "\tc_decimal_1 INT(11) NOT NULL DEFAULT NULL,\n" +
                        "\t`id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',\n" +
                        "\tc_char char(32),\n" +
                        "\t`c_smallint` smallint(6) DEFAULT 0 COMMENT 'smallint',\n" +
                        "\t`c_mediumint` mediumint(9) DEFAULT NULL COMMENT 'mediumint',\n" +
                        "\t`c_int` int(11) DEFAULT NULL COMMENT 'int',\n" +
                        "\t`c_bigint` bigint(20) DEFAULT NULL COMMENT 'bigint',\n" +
                        "\t`c_date` date DEFAULT '0000-00-00' COMMENT 'date',\n" +
                        "\t`c_datetime` datetime DEFAULT '0000-00-00 00:00:00' COMMENT 'datetime',\n" +
                        "\t`c_timestamp` timestamp NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT 'timestamp',\n" +
                        "\t`c_time` time DEFAULT NULL COMMENT 'time',\n" +
                        "\t`c_varchar` varchar(10) DEFAULT 'hello' COMMENT 'varchar',\n" +
                        "\t`c_blob` blob COMMENT 'blob',\n" +
                        "\t`c_text` text COMMENT 'text',\n" +
                        "\t`c_mediumtext` mediumtext COMMENT 'mediumtext',\n" +
                        "\t`c_longblob` longblob COMMENT 'longblob',\n" +
                        "\tKEY `k_c` (`c_int`)\n" +
                        ") ENGINE = InnoDB AUTO_INCREMENT = 1769503 CHARSET = utf8mb4 COMMENT '10000000'"

                , repository.console("show create table test1"));

        assertEquals("+--------------+--------------+------+-----+---------------------+-----------------------------+\n" +
                        "| Field        | Type         | Null | Key | Default             | Extra                       |\n" +
                        "+--------------+--------------+------+-----+---------------------+-----------------------------+\n" +
                        "| c_decimal_1  | INT(11)      | NO   |     | NULL                |                             |\n" +
                        "| id           | bigint(20)   | NO   |     | NULL                | auto_increment              |\n" +
                        "| c_char       | char(32)     | YES  |     | NULL                |                             |\n" +
                        "| c_smallint   | smallint(6)  | YES  |     | 0                   |                             |\n" +
                        "| c_mediumint  | mediumint(9) | YES  |     | NULL                |                             |\n" +
                        "| c_int        | int(11)      | YES  | MUL | NULL                |                             |\n" +
                        "| c_bigint     | bigint(20)   | YES  |     | NULL                |                             |\n" +
                        "| c_date       | date         | YES  |     | 0000-00-00          |                             |\n" +
                        "| c_datetime   | datetime     | YES  |     | 0000-00-00 00:00:00 |                             |\n" +
                        "| c_timestamp  | timestamp    | YES  |     | NULL                | on update CURRENT_TIMESTAMP |\n" +
                        "| c_time       | time         | YES  |     | NULL                |                             |\n" +
                        "| c_varchar    | varchar(10)  | YES  |     | hello               |                             |\n" +
                        "| c_blob       | blob         | YES  |     | NULL                |                             |\n" +
                        "| c_text       | text         | YES  |     | NULL                |                             |\n" +
                        "| c_mediumtext | mediumtext   | YES  |     | NULL                |                             |\n" +
                        "| c_longblob   | longblob     | YES  |     | NULL                |                             |\n" +
                        "+--------------+--------------+------+-----+---------------------+-----------------------------+\n"

                , repository.console("show columns from test1"));


        repository.console("alter table test1 add CONSTRAINT unique name4_idx(id); ");

        assertEquals("+--------------+--------------+------+-----+---------------------+-----------------------------+\n" +
                        "| Field        | Type         | Null | Key | Default             | Extra                       |\n" +
                        "+--------------+--------------+------+-----+---------------------+-----------------------------+\n" +
                        "| c_decimal_1  | INT(11)      | NO   |     | NULL                |                             |\n" +
                        "| id           | bigint(20)   | NO   | UNI | NULL                | auto_increment              |\n" +
                        "| c_char       | char(32)     | YES  |     | NULL                |                             |\n" +
                        "| c_smallint   | smallint(6)  | YES  |     | 0                   |                             |\n" +
                        "| c_mediumint  | mediumint(9) | YES  |     | NULL                |                             |\n" +
                        "| c_int        | int(11)      | YES  | MUL | NULL                |                             |\n" +
                        "| c_bigint     | bigint(20)   | YES  |     | NULL                |                             |\n" +
                        "| c_date       | date         | YES  |     | 0000-00-00          |                             |\n" +
                        "| c_datetime   | datetime     | YES  |     | 0000-00-00 00:00:00 |                             |\n" +
                        "| c_timestamp  | timestamp    | YES  |     | NULL                | on update CURRENT_TIMESTAMP |\n" +
                        "| c_time       | time         | YES  |     | NULL                |                             |\n" +
                        "| c_varchar    | varchar(10)  | YES  |     | hello               |                             |\n" +
                        "| c_blob       | blob         | YES  |     | NULL                |                             |\n" +
                        "| c_text       | text         | YES  |     | NULL                |                             |\n" +
                        "| c_mediumtext | mediumtext   | YES  |     | NULL                |                             |\n" +
                        "| c_longblob   | longblob     | YES  |     | NULL                |                             |\n" +
                        "+--------------+--------------+------+-----+---------------------+-----------------------------+\n"

                , repository.console("show columns from test1"));

        repository.console("alter table test1 add FULLTEXT name4_idx_c_longblob(c_longblob); ");

        assertEquals("CREATE TABLE `test1` (\n" +
                        "\tc_decimal_1 INT(11) NOT NULL DEFAULT NULL,\n" +
                        "\t`id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',\n" +
                        "\tc_char char(32),\n" +
                        "\t`c_smallint` smallint(6) DEFAULT 0 COMMENT 'smallint',\n" +
                        "\t`c_mediumint` mediumint(9) DEFAULT NULL COMMENT 'mediumint',\n" +
                        "\t`c_int` int(11) DEFAULT NULL COMMENT 'int',\n" +
                        "\t`c_bigint` bigint(20) DEFAULT NULL COMMENT 'bigint',\n" +
                        "\t`c_date` date DEFAULT '0000-00-00' COMMENT 'date',\n" +
                        "\t`c_datetime` datetime DEFAULT '0000-00-00 00:00:00' COMMENT 'datetime',\n" +
                        "\t`c_timestamp` timestamp NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT 'timestamp',\n" +
                        "\t`c_time` time DEFAULT NULL COMMENT 'time',\n" +
                        "\t`c_varchar` varchar(10) DEFAULT 'hello' COMMENT 'varchar',\n" +
                        "\t`c_blob` blob COMMENT 'blob',\n" +
                        "\t`c_text` text COMMENT 'text',\n" +
                        "\t`c_mediumtext` mediumtext COMMENT 'mediumtext',\n" +
                        "\t`c_longblob` longblob COMMENT 'longblob',\n" +
                        "\tKEY `k_c` (`c_int`),\n" +
                        "\tUNIQUE name4_idx (id),\n" +
                        "\tFULLTEXT INDEX name4_idx_c_longblob(c_longblob)\n" +
                        ") ENGINE = InnoDB AUTO_INCREMENT = 1769503 CHARSET = utf8mb4 COMMENT '10000000'"

                , repository.console("show create table test1"));


        repository.console("alter table test1 add key name5_idx(c_bigint); ");

        assertEquals("CREATE TABLE `test1` (\n" +
                        "\tc_decimal_1 INT(11) NOT NULL DEFAULT NULL,\n" +
                        "\t`id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',\n" +
                        "\tc_char char(32),\n" +
                        "\t`c_smallint` smallint(6) DEFAULT 0 COMMENT 'smallint',\n" +
                        "\t`c_mediumint` mediumint(9) DEFAULT NULL COMMENT 'mediumint',\n" +
                        "\t`c_int` int(11) DEFAULT NULL COMMENT 'int',\n" +
                        "\t`c_bigint` bigint(20) DEFAULT NULL COMMENT 'bigint',\n" +
                        "\t`c_date` date DEFAULT '0000-00-00' COMMENT 'date',\n" +
                        "\t`c_datetime` datetime DEFAULT '0000-00-00 00:00:00' COMMENT 'datetime',\n" +
                        "\t`c_timestamp` timestamp NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT 'timestamp',\n" +
                        "\t`c_time` time DEFAULT NULL COMMENT 'time',\n" +
                        "\t`c_varchar` varchar(10) DEFAULT 'hello' COMMENT 'varchar',\n" +
                        "\t`c_blob` blob COMMENT 'blob',\n" +
                        "\t`c_text` text COMMENT 'text',\n" +
                        "\t`c_mediumtext` mediumtext COMMENT 'mediumtext',\n" +
                        "\t`c_longblob` longblob COMMENT 'longblob',\n" +
                        "\tKEY `k_c` (`c_int`),\n" +
                        "\tUNIQUE name4_idx (id),\n" +
                        "\tFULLTEXT INDEX name4_idx_c_longblob(c_longblob),\n" +
                        "\tKEY name5_idx (c_bigint)\n" +
                        ") ENGINE = InnoDB AUTO_INCREMENT = 1769503 CHARSET = utf8mb4 COMMENT '10000000'"

                , repository.console("show create table test1"));

        repository.console("alter table test1 drop index name4_idx");
        assertEquals("CREATE TABLE `test1` (\n" +
                        "\tc_decimal_1 INT(11) NOT NULL DEFAULT NULL,\n" +
                        "\t`id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',\n" +
                        "\tc_char char(32),\n" +
                        "\t`c_smallint` smallint(6) DEFAULT 0 COMMENT 'smallint',\n" +
                        "\t`c_mediumint` mediumint(9) DEFAULT NULL COMMENT 'mediumint',\n" +
                        "\t`c_int` int(11) DEFAULT NULL COMMENT 'int',\n" +
                        "\t`c_bigint` bigint(20) DEFAULT NULL COMMENT 'bigint',\n" +
                        "\t`c_date` date DEFAULT '0000-00-00' COMMENT 'date',\n" +
                        "\t`c_datetime` datetime DEFAULT '0000-00-00 00:00:00' COMMENT 'datetime',\n" +
                        "\t`c_timestamp` timestamp NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT 'timestamp',\n" +
                        "\t`c_time` time DEFAULT NULL COMMENT 'time',\n" +
                        "\t`c_varchar` varchar(10) DEFAULT 'hello' COMMENT 'varchar',\n" +
                        "\t`c_blob` blob COMMENT 'blob',\n" +
                        "\t`c_text` text COMMENT 'text',\n" +
                        "\t`c_mediumtext` mediumtext COMMENT 'mediumtext',\n" +
                        "\t`c_longblob` longblob COMMENT 'longblob',\n" +
                        "\tKEY `k_c` (`c_int`),\n" +
                        "\tFULLTEXT INDEX name4_idx_c_longblob(c_longblob),\n" +
                        "\tKEY name5_idx (c_bigint)\n" +
                        ") ENGINE = InnoDB AUTO_INCREMENT = 1769503 CHARSET = utf8mb4 COMMENT '10000000'"

                , repository.console("show create table test1"));

        repository.console("alter table test1 add primary key name5_pk(c_bigint); ");
        assertEquals("CREATE TABLE `test1` (\n" +
                        "\tc_decimal_1 INT(11) NOT NULL DEFAULT NULL,\n" +
                        "\t`id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',\n" +
                        "\tc_char char(32),\n" +
                        "\t`c_smallint` smallint(6) DEFAULT 0 COMMENT 'smallint',\n" +
                        "\t`c_mediumint` mediumint(9) DEFAULT NULL COMMENT 'mediumint',\n" +
                        "\t`c_int` int(11) DEFAULT NULL COMMENT 'int',\n" +
                        "\t`c_bigint` bigint(20) DEFAULT NULL COMMENT 'bigint',\n" +
                        "\t`c_date` date DEFAULT '0000-00-00' COMMENT 'date',\n" +
                        "\t`c_datetime` datetime DEFAULT '0000-00-00 00:00:00' COMMENT 'datetime',\n" +
                        "\t`c_timestamp` timestamp NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT 'timestamp',\n" +
                        "\t`c_time` time DEFAULT NULL COMMENT 'time',\n" +
                        "\t`c_varchar` varchar(10) DEFAULT 'hello' COMMENT 'varchar',\n" +
                        "\t`c_blob` blob COMMENT 'blob',\n" +
                        "\t`c_text` text COMMENT 'text',\n" +
                        "\t`c_mediumtext` mediumtext COMMENT 'mediumtext',\n" +
                        "\t`c_longblob` longblob COMMENT 'longblob',\n" +
                        "\tKEY `k_c` (`c_int`),\n" +
                        "\tFULLTEXT INDEX name4_idx_c_longblob(c_longblob),\n" +
                        "\tKEY name5_idx (c_bigint),\n" +
                        "\tPRIMARY KEY (c_bigint)\n" +
                        ") ENGINE = InnoDB AUTO_INCREMENT = 1769503 CHARSET = utf8mb4 COMMENT '10000000'"

                , repository.console("show create table test1"));

        repository.console("alter table test1 drop primary key;");
        assertEquals("CREATE TABLE `test1` (\n" +
                        "\tc_decimal_1 INT(11) NOT NULL DEFAULT NULL,\n" +
                        "\t`id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',\n" +
                        "\tc_char char(32),\n" +
                        "\t`c_smallint` smallint(6) DEFAULT 0 COMMENT 'smallint',\n" +
                        "\t`c_mediumint` mediumint(9) DEFAULT NULL COMMENT 'mediumint',\n" +
                        "\t`c_int` int(11) DEFAULT NULL COMMENT 'int',\n" +
                        "\t`c_bigint` bigint(20) DEFAULT NULL COMMENT 'bigint',\n" +
                        "\t`c_date` date DEFAULT '0000-00-00' COMMENT 'date',\n" +
                        "\t`c_datetime` datetime DEFAULT '0000-00-00 00:00:00' COMMENT 'datetime',\n" +
                        "\t`c_timestamp` timestamp NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT 'timestamp',\n" +
                        "\t`c_time` time DEFAULT NULL COMMENT 'time',\n" +
                        "\t`c_varchar` varchar(10) DEFAULT 'hello' COMMENT 'varchar',\n" +
                        "\t`c_blob` blob COMMENT 'blob',\n" +
                        "\t`c_text` text COMMENT 'text',\n" +
                        "\t`c_mediumtext` mediumtext COMMENT 'mediumtext',\n" +
                        "\t`c_longblob` longblob COMMENT 'longblob',\n" +
                        "\tKEY `k_c` (`c_int`),\n" +
                        "\tFULLTEXT INDEX name4_idx_c_longblob(c_longblob),\n" +
                        "\tKEY name5_idx (c_bigint)\n" +
                        ") ENGINE = InnoDB AUTO_INCREMENT = 1769503 CHARSET = utf8mb4 COMMENT '10000000'"

                , repository.console("show create table test1"));

        repository.console("GRANT ALL PRIVILEGES ON *.* TO 'idbtmp_1208408_9'@'%' IDENTIFIED BY PASSWORD '*5E8A11FF574FCBC6440EB285D490AEE96B16E250' WITH GRANT OPTION");

        repository.console("alter table test1 add unique key name_idx_23 (c_char(10))");
        assertEquals("CREATE TABLE `test1` (\n" +
                        "\tc_decimal_1 INT(11) NOT NULL DEFAULT NULL,\n" +
                        "\t`id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',\n" +
                        "\tc_char char(32),\n" +
                        "\t`c_smallint` smallint(6) DEFAULT 0 COMMENT 'smallint',\n" +
                        "\t`c_mediumint` mediumint(9) DEFAULT NULL COMMENT 'mediumint',\n" +
                        "\t`c_int` int(11) DEFAULT NULL COMMENT 'int',\n" +
                        "\t`c_bigint` bigint(20) DEFAULT NULL COMMENT 'bigint',\n" +
                        "\t`c_date` date DEFAULT '0000-00-00' COMMENT 'date',\n" +
                        "\t`c_datetime` datetime DEFAULT '0000-00-00 00:00:00' COMMENT 'datetime',\n" +
                        "\t`c_timestamp` timestamp NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT 'timestamp',\n" +
                        "\t`c_time` time DEFAULT NULL COMMENT 'time',\n" +
                        "\t`c_varchar` varchar(10) DEFAULT 'hello' COMMENT 'varchar',\n" +
                        "\t`c_blob` blob COMMENT 'blob',\n" +
                        "\t`c_text` text COMMENT 'text',\n" +
                        "\t`c_mediumtext` mediumtext COMMENT 'mediumtext',\n" +
                        "\t`c_longblob` longblob COMMENT 'longblob',\n" +
                        "\tKEY `k_c` (`c_int`),\n" +
                        "\tFULLTEXT INDEX name4_idx_c_longblob(c_longblob),\n" +
                        "\tKEY name5_idx (c_bigint),\n" +
                        "\tUNIQUE KEY name_idx_23 (c_char(10))\n" +
                        ") ENGINE = InnoDB AUTO_INCREMENT = 1769503 CHARSET = utf8mb4 COMMENT '10000000'"

                , repository.console("show create table test1"));


        assertEquals("+--------------+--------------+------+-----+---------------------+-----------------------------+\n" +
                        "| Field        | Type         | Null | Key | Default             | Extra                       |\n" +
                        "+--------------+--------------+------+-----+---------------------+-----------------------------+\n" +
                        "| c_decimal_1  | INT(11)      | NO   |     | NULL                |                             |\n" +
                        "| id           | bigint(20)   | NO   |     | NULL                | auto_increment              |\n" +
                        "| c_char       | char(32)     | YES  | UNI | NULL                |                             |\n" +
                        "| c_smallint   | smallint(6)  | YES  |     | 0                   |                             |\n" +
                        "| c_mediumint  | mediumint(9) | YES  |     | NULL                |                             |\n" +
                        "| c_int        | int(11)      | YES  | MUL | NULL                |                             |\n" +
                        "| c_bigint     | bigint(20)   | YES  | MUL | NULL                |                             |\n" +
                        "| c_date       | date         | YES  |     | 0000-00-00          |                             |\n" +
                        "| c_datetime   | datetime     | YES  |     | 0000-00-00 00:00:00 |                             |\n" +
                        "| c_timestamp  | timestamp    | YES  |     | NULL                | on update CURRENT_TIMESTAMP |\n" +
                        "| c_time       | time         | YES  |     | NULL                |                             |\n" +
                        "| c_varchar    | varchar(10)  | YES  |     | hello               |                             |\n" +
                        "| c_blob       | blob         | YES  |     | NULL                |                             |\n" +
                        "| c_text       | text         | YES  |     | NULL                |                             |\n" +
                        "| c_mediumtext | mediumtext   | YES  |     | NULL                |                             |\n" +
                        "| c_longblob   | longblob     | YES  |     | NULL                |                             |\n" +
                        "+--------------+--------------+------+-----+---------------------+-----------------------------+\n"

                , repository.console("show columns from test1"));

        repository.console("alter table test1 drop c_char;");
        assertEquals("CREATE TABLE `test1` (\n" +
                        "\tc_decimal_1 INT(11) NOT NULL DEFAULT NULL,\n" +
                        "\t`id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',\n" +
                        "\t`c_smallint` smallint(6) DEFAULT 0 COMMENT 'smallint',\n" +
                        "\t`c_mediumint` mediumint(9) DEFAULT NULL COMMENT 'mediumint',\n" +
                        "\t`c_int` int(11) DEFAULT NULL COMMENT 'int',\n" +
                        "\t`c_bigint` bigint(20) DEFAULT NULL COMMENT 'bigint',\n" +
                        "\t`c_date` date DEFAULT '0000-00-00' COMMENT 'date',\n" +
                        "\t`c_datetime` datetime DEFAULT '0000-00-00 00:00:00' COMMENT 'datetime',\n" +
                        "\t`c_timestamp` timestamp NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT 'timestamp',\n" +
                        "\t`c_time` time DEFAULT NULL COMMENT 'time',\n" +
                        "\t`c_varchar` varchar(10) DEFAULT 'hello' COMMENT 'varchar',\n" +
                        "\t`c_blob` blob COMMENT 'blob',\n" +
                        "\t`c_text` text COMMENT 'text',\n" +
                        "\t`c_mediumtext` mediumtext COMMENT 'mediumtext',\n" +
                        "\t`c_longblob` longblob COMMENT 'longblob',\n" +
                        "\tKEY `k_c` (`c_int`),\n" +
                        "\tFULLTEXT INDEX name4_idx_c_longblob(c_longblob),\n" +
                        "\tKEY name5_idx (c_bigint)\n" +
                        ") ENGINE = InnoDB AUTO_INCREMENT = 1769503 CHARSET = utf8mb4 COMMENT '10000000'"

                , repository.console("show create table test1"));

        repository.console("rename table test1 to test1_xx");

        assertEquals("ERROR 1146 (42S02): Table 'test1' doesn't exist\n"

                , repository.console("show create table test1"));

        assertEquals("ERROR 1146 (42S02): Table 'test1' doesn't exist\n"

                , repository.console("show columns from test1"));

        assertEquals("CREATE TABLE test1_xx (\n" +
                        "\tc_decimal_1 INT(11) NOT NULL DEFAULT NULL,\n" +
                        "\t`id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',\n" +
                        "\t`c_smallint` smallint(6) DEFAULT 0 COMMENT 'smallint',\n" +
                        "\t`c_mediumint` mediumint(9) DEFAULT NULL COMMENT 'mediumint',\n" +
                        "\t`c_int` int(11) DEFAULT NULL COMMENT 'int',\n" +
                        "\t`c_bigint` bigint(20) DEFAULT NULL COMMENT 'bigint',\n" +
                        "\t`c_date` date DEFAULT '0000-00-00' COMMENT 'date',\n" +
                        "\t`c_datetime` datetime DEFAULT '0000-00-00 00:00:00' COMMENT 'datetime',\n" +
                        "\t`c_timestamp` timestamp NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT 'timestamp',\n" +
                        "\t`c_time` time DEFAULT NULL COMMENT 'time',\n" +
                        "\t`c_varchar` varchar(10) DEFAULT 'hello' COMMENT 'varchar',\n" +
                        "\t`c_blob` blob COMMENT 'blob',\n" +
                        "\t`c_text` text COMMENT 'text',\n" +
                        "\t`c_mediumtext` mediumtext COMMENT 'mediumtext',\n" +
                        "\t`c_longblob` longblob COMMENT 'longblob',\n" +
                        "\tKEY `k_c` (`c_int`),\n" +
                        "\tFULLTEXT INDEX name4_idx_c_longblob(c_longblob),\n" +
                        "\tKEY name5_idx (c_bigint)\n" +
                        ") ENGINE = InnoDB AUTO_INCREMENT = 1769503 CHARSET = utf8mb4 COMMENT '10000000'"

                , repository.console("show create table test1_xx"));


        assertEquals("CREATE TABLE test5 (\n" +
                        "\tc_decimal_1 INT(11) NOT NULL DEFAULT NULL,\n" +
                        "\tid bigint(20) NOT NULL COMMENT 'id',\n" +
                        "\tc_tinyint tinyint(4) DEFAULT '1' COMMENT 'tinyint',\n" +
                        "\tc_smallint smallint(6) DEFAULT 0 COMMENT 'smallint',\n" +
                        "\tc_mediumint mediumint(9) DEFAULT NULL COMMENT 'mediumint',\n" +
                        "\tc_int int(11) DEFAULT NULL COMMENT 'int',\n" +
                        "\tc_bigint bigint(20) DEFAULT NULL COMMENT 'bigint',\n" +
                        "\tc_date date DEFAULT '0000-00-00' COMMENT 'date',\n" +
                        "\tc_datetime datetime DEFAULT '0000-00-00 00:00:00' COMMENT 'datetime',\n" +
                        "\tc_timestamp timestamp NULL DEFAULT NULL COMMENT 'timestamp',\n" +
                        "\tc_time time DEFAULT NULL COMMENT 'time',\n" +
                        "\tc_char char(10) DEFAULT NULL COMMENT 'char',\n" +
                        "\tc_varchar varchar(10) DEFAULT 'hello' COMMENT 'varchar',\n" +
                        "\tc_blob blob COMMENT 'blob',\n" +
                        "\tc_text text COMMENT 'text',\n" +
                        "\tc_mediumtext mediumtext COMMENT 'mediumtext',\n" +
                        "\tc_longblob longblob COMMENT 'longblob'\n" +
                        ")"

                , repository.console("show create table test5"));

        repository.console("create table test5(fid bigint)");
        assertEquals("CREATE TABLE test5 (\n" +
                        "\tfid bigint\n" +
                        ")"

                , repository.console("show create table test5"));


        repository.console("CREATE TABLE tddl5_00.ljh_test1(be_id VARCHAR(20) NOT NULL DEFAULT ''  COMMENT '企业标识')ENGINE = InnoDB, CHARACTER SET = utf8, COMMENT = '应用层-汇总销售及销售毛利表'");

        assertEquals("+-------+-------------+------+-----+---------+-------+\n" +
                        "| Field | Type        | Null | Key | Default | Extra |\n" +
                        "+-------+-------------+------+-----+---------+-------+\n" +
                        "| be_id | VARCHAR(20) | NO   |     | ''      |       |\n" +
                        "+-------+-------------+------+-----+---------+-------+\n"
                , repository.console("show columns from tddl5_00.ljh_test1"));
    }
}
