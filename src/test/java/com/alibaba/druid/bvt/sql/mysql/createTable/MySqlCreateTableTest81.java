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

public class MySqlCreateTableTest81 extends MysqlTest {

    public void test_one() throws Exception {
        String sql = "CREATE TABLE `admin` (\n" +
                "  `id` char(20) NOT NULL,\n" +
                "  `username` varchar(16) NOT NULL COMMENT '用户名',\n" +
                "  `password` varchar(32) NOT NULL COMMENT '密码',\n" +
                "  `permission` varchar(255) NOT NULL DEFAULT '' COMMENT '权限',\n" +
                "  PRIMARY KEY (`id`) USING BTREE\n" +
                ") ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='管理员';";

        MySqlStatementParser parser = new MySqlStatementParser(sql);
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
            assertEquals("CREATE TABLE `admin` (\n" +
                    "\t`id` char(20) NOT NULL,\n" +
                    "\t`username` varchar(16) NOT NULL COMMENT '用户名',\n" +
                    "\t`password` varchar(32) NOT NULL COMMENT '密码',\n" +
                    "\t`permission` varchar(255) NOT NULL DEFAULT '' COMMENT '权限',\n" +
                    "\tPRIMARY KEY USING BTREE (`id`)\n" +
                    ") ENGINE = InnoDB CHARSET = utf8 COMMENT '管理员'", output);
        }
        
        {
            String output = SQLUtils.toMySqlString(stmt, SQLUtils.DEFAULT_LCASE_FORMAT_OPTION);
            assertEquals("create table `admin` (\n" +
                    "\t`id` char(20) not null,\n" +
                    "\t`username` varchar(16) not null comment '用户名',\n" +
                    "\t`password` varchar(32) not null comment '密码',\n" +
                    "\t`permission` varchar(255) not null default '' comment '权限',\n" +
                    "\tprimary key using BTREE (`id`)\n" +
                    ") engine = InnoDB charset = utf8 comment '管理员'", output);
        }
    }
}
