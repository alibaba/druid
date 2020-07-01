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

import org.junit.Assert;
import org.junit.Test;

import com.alibaba.druid.sql.MysqlTest;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.mysql.parser.MySqlStatementParser;
import com.alibaba.druid.sql.dialect.mysql.visitor.MySqlSchemaStatVisitor;
import com.alibaba.druid.stat.TableStat.Column;

public class MySqlCreateTableTest71 extends MysqlTest {

    @Test
    public void test_one() throws Exception {
        String sql = "create table xx (id bigint unsigned not null comment 'aa' auto_increment," //
                + "gmt_create datetime not null comment '创建时间'," //
                + "gmt_modified datetime not null comment '修改时间', " //
                + "primary key (id)) comment='re'";

        MySqlStatementParser parser = new MySqlStatementParser(sql);
        SQLStatement stmt = parser.parseCreateTable();

        MySqlSchemaStatVisitor visitor = new MySqlSchemaStatVisitor();
        stmt.accept(visitor);
        
        Column column = visitor.getColumn("xx", "id");
        Assert.assertNotNull(column);
        Assert.assertEquals("bigint", column.getDataType());

        {
            String output = SQLUtils.toMySqlString(stmt);
            Assert.assertEquals("CREATE TABLE xx ("
                    + "\n\tid bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT 'aa',"
                    + "\n\tgmt_create datetime NOT NULL COMMENT '创建时间',"
                    + "\n\tgmt_modified datetime NOT NULL COMMENT '修改时间',"
                    + "\n\tPRIMARY KEY (id)"
                    + "\n) COMMENT 're'", output);
        }
        
        {
            String output = SQLUtils.toMySqlString(stmt, SQLUtils.DEFAULT_LCASE_FORMAT_OPTION);
            Assert.assertEquals("create table xx ("
                    + "\n\tid bigint unsigned not null auto_increment comment 'aa',"
                    + "\n\tgmt_create datetime not null comment '创建时间',"
                    + "\n\tgmt_modified datetime not null comment '修改时间',"
                    + "\n\tprimary key (id)"
                    + "\n) comment 're'", output);
        }
    }
}
