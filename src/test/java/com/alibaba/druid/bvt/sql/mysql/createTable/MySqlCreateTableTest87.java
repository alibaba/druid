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
package com.alibaba.druid.bvt.sql.mysql.createTable;

import com.alibaba.druid.sql.MysqlTest;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.mysql.parser.MySqlStatementParser;
import com.alibaba.druid.sql.dialect.mysql.visitor.MySqlSchemaStatVisitor;
import com.alibaba.druid.sql.parser.SQLParserFeature;

public class MySqlCreateTableTest87 extends MysqlTest {

    public void test_one() throws Exception {
        String sql = "CREATE TABLE `test_4` (\n" +
                "  `id` bigint(20) zerofill   unsigNed NOT NULL AUTO_INCREMENT COMMENT 'id',\n" +
                "  `c_tinyint` tinyint(4) DEFAULT '1' COMMENT 'tinyint',\n" +
                "  PRIMARY KEY (`id`)\n" +
                ") ENGINE=InnoDB AUTO_INCREMENT=1769531 DEFAULT CHARSET=utf8mb4 COMMENT='10000000'";

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
            assertEquals("CREATE TABLE `test_4` (\n" +
                    "\t`id` bigint(20) UNSIGNED ZEROFILL NOT NULL AUTO_INCREMENT COMMENT 'id',\n" +
                    "\t`c_tinyint` tinyint(4) DEFAULT '1' COMMENT 'tinyint',\n" +
                    "\tPRIMARY KEY (`id`)\n" +
                    ") ENGINE = InnoDB AUTO_INCREMENT = 1769531 CHARSET = utf8mb4 COMMENT '10000000'", output);
        }
        
        {
            String output = SQLUtils.toMySqlString(stmt, SQLUtils.DEFAULT_LCASE_FORMAT_OPTION);
            assertEquals("create table `test_4` (\n" +
                    "\t`id` bigint(20) unsigned zerofill not null auto_increment comment 'id',\n" +
                    "\t`c_tinyint` tinyint(4) default '1' comment 'tinyint',\n" +
                    "\tprimary key (`id`)\n" +
                    ") engine = InnoDB auto_increment = 1769531 charset = utf8mb4 comment '10000000'", output);
        }
    }
}
