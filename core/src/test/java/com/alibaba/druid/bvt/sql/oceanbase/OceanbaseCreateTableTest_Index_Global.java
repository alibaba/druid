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
package com.alibaba.druid.bvt.sql.oceanbase;

import com.alibaba.druid.sql.MysqlTest;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.mysql.parser.MySqlStatementParser;
import com.alibaba.druid.sql.dialect.mysql.visitor.MySqlSchemaStatVisitor;
import org.junit.Assert;

import java.util.List;

public class OceanbaseCreateTableTest_Index_Global extends MysqlTest {
    public void test_0() throws Exception {
        String sql = "CREATE TABLE `test_01` (\n" +
                "  `id` varchar(16) NOT NULL,\n" +
                "  `name` varchar(128) DEFAULT NULL,\n" +
                "  PRIMARY KEY (`id`),\n" +
                "  KEY `idx_name` (`name`) BLOCK_SIZE 16384 GLOBAL\n" +
                ") DEFAULT CHARSET = utf8mb4;";

        MySqlStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> stmtList = parser.parseStatementList();
        SQLStatement stmt = stmtList.get(0);

        {
            String result = SQLUtils.toMySqlString(stmt);
            Assert.assertEquals("CREATE TABLE `test_01` (\n" +
                            "\t`id` varchar(16) NOT NULL,\n" +
                            "\t`name` varchar(128) DEFAULT NULL,\n" +
                            "\tPRIMARY KEY (`id`),\n" +
                            "\tKEY `idx_name` (`name`) GLOBAL KEY_BLOCK_SIZE = 16384\n" +
                            ") CHARSET = utf8mb4;",
                    result);
        }
        {
            String result = SQLUtils.toMySqlString(stmt, SQLUtils.DEFAULT_LCASE_FORMAT_OPTION);
            Assert.assertEquals("create table `test_01` (\n" +
                            "\t`id` varchar(16) not null,\n" +
                            "\t`name` varchar(128) default null,\n" +
                            "\tprimary key (`id`),\n" +
                            "\tkey `idx_name` (`name`) global key_block_size = 16384\n" +
                            ") charset = utf8mb4;",
                    result);
        }

        Assert.assertEquals(1, stmtList.size());
    }
}
