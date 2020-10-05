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

public class MySqlCreateTableTest90 extends MysqlTest {

    public void test_one() throws Exception {
        String sql = "CREATE TABLE `g_platform_payway` (\n" +
                "\t`id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'id', \n" +
                "\t`platform_code` varchar(45) NOT NULL, \n" +
                "\t`pay_way` varchar(32) NOT NULL, \n" +
                "\tPRIMARY KEY (`id`), \n" +
                "\tUNIQUE `uniq_playform_payway` USING BTREE (`platform_code`, `pay_way`) COMMENT '平台code和支付方式应该唯一'\n" +
                ") ENGINE = InnoDB CHARSET = utf8 COMMENT = '配置表'";

        MySqlStatementParser parser = new MySqlStatementParser(sql, SQLParserFeature.KeepComments);
        SQLStatement stmt = parser.parseCreateTable();

        MySqlSchemaStatVisitor visitor = new MySqlSchemaStatVisitor();
        stmt.accept(visitor);

        visitor.containsTable("t_share_like_info");
//        System.out.println("Tables : " + visitor.getTables());
        System.out.println("fields : " + visitor.getColumns());
//        System.out.println("coditions : " + visitor.getConditions());
//        System.out.println("orderBy : " + visitor.getOrderByColumns());

        System.out.println(stmt);

        {
            String output = SQLUtils.toMySqlString(stmt);
            assertEquals("CREATE TABLE `g_platform_payway` (\n" +
                    "\t`id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'id',\n" +
                    "\t`platform_code` varchar(45) NOT NULL,\n" +
                    "\t`pay_way` varchar(32) NOT NULL,\n" +
                    "\tPRIMARY KEY (`id`),\n" +
                    "\tUNIQUE `uniq_playform_payway` USING BTREE (`platform_code`, `pay_way`) COMMENT '平台code和支付方式应该唯一'\n" +
                    ") ENGINE = InnoDB CHARSET = utf8 COMMENT '配置表'", output);
        }
        
        {
            String output = SQLUtils.toMySqlString(stmt, SQLUtils.DEFAULT_LCASE_FORMAT_OPTION);
            assertEquals("create table `g_platform_payway` (\n" +
                    "\t`id` int(11) not null auto_increment comment 'id',\n" +
                    "\t`platform_code` varchar(45) not null,\n" +
                    "\t`pay_way` varchar(32) not null,\n" +
                    "\tprimary key (`id`),\n" +
                    "\tunique `uniq_playform_payway` using BTREE (`platform_code`, `pay_way`) comment '平台code和支付方式应该唯一'\n" +
                    ") engine = InnoDB charset = utf8 comment '配置表'", output);
        }
    }
}
