/*
 * Copyright 1999-2018 Alibaba Group Holding Ltd.
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
package com.alibaba.druid.bvt.sql.mysql.select;

import com.alibaba.druid.sql.MysqlTest;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlCreateTableStatement;
import com.alibaba.druid.sql.visitor.SchemaStatVisitor;
import com.alibaba.druid.util.JdbcConstants;

import java.util.List;

public class MySqlCreateTable_showColumns_test extends MysqlTest {

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
                ") ENGINE=InnoDB AUTO_INCREMENT=1769503 DEFAULT CHARSET=utf8mb4 COMMENT='10000000'";


        List<SQLStatement> statementList = SQLUtils.parseStatements(sql, JdbcConstants.MYSQL, true);

        assertEquals(1, statementList.size());

        MySqlCreateTableStatement stmt = (MySqlCreateTableStatement) statementList.get(0);


        StringBuilder buf = new StringBuilder();
        stmt.showCoumns(buf);

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
                "+--------------+---------------+------+-----+---------------------+-----------------------------+\n", buf.toString());
    }
}
