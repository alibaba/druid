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
import com.alibaba.druid.stat.TableStat.Column;

public class MySqlCreateTableTest79_collate extends MysqlTest {

    public void test_one() throws Exception {
        String sql = "CREATE TABLE tb_custom_vip_show_message (custom_vip_show_message_seq INT(11) NOT NULL AUTO_INCREMENT,show_channel_type TINYINT(4) NOT NULL COMMENT '通道类型',PRIMARY KEY (custom_vip_show_message_seq))COMMENT='自定VIP显示表' COLLATE='utf8_general_ci' ENGINE=InnoDB;";

        MySqlStatementParser parser = new MySqlStatementParser(sql);
        SQLStatement stmt = parser.parseCreateTable();

        MySqlSchemaStatVisitor visitor = new MySqlSchemaStatVisitor();
        stmt.accept(visitor);
        
        Column column = visitor.getColumn("tb_custom_vip_show_message", "custom_vip_show_message_seq");
        assertNotNull(column);
        assertEquals("INT", column.getDataType());

        {
            String output = SQLUtils.toMySqlString(stmt);
            assertEquals("CREATE TABLE tb_custom_vip_show_message (\n" +
                    "\tcustom_vip_show_message_seq INT(11) NOT NULL AUTO_INCREMENT,\n" +
                    "\tshow_channel_type TINYINT(4) NOT NULL COMMENT '通道类型',\n" +
                    "\tPRIMARY KEY (custom_vip_show_message_seq)\n" +
                    ") ENGINE = InnoDB COMMENT '自定VIP显示表' COLLATE utf8_general_ci", output);
        }
        
        {
            String output = SQLUtils.toMySqlString(stmt, SQLUtils.DEFAULT_LCASE_FORMAT_OPTION);
            assertEquals("create table tb_custom_vip_show_message (\n" +
                    "\tcustom_vip_show_message_seq INT(11) not null auto_increment,\n" +
                    "\tshow_channel_type TINYINT(4) not null comment '通道类型',\n" +
                    "\tprimary key (custom_vip_show_message_seq)\n" +
                    ") engine = InnoDB comment '自定VIP显示表' collate utf8_general_ci", output);
        }
    }
}
