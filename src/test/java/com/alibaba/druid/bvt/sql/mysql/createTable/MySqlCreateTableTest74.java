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
import org.junit.Assert;
import org.junit.Test;

public class MySqlCreateTableTest74 extends MysqlTest {

    @Test
    public void test_one() throws Exception {
        String sql = "create table test.simple_test (\n" +
                "col_key bigint(20) not null auto_increment,\n" +
                "col1 varchar(45) null,\n" +
                "col2 tinyint(4) null,\n" +
                "col3 datetime null,\n" +
                "col4 timestamp null default current_timestamp on update current_timestamp,\n" +
                "primary key (col_key),\n" +
                "unique index v1_unique (col_key asc))";

        MySqlStatementParser parser = new MySqlStatementParser(sql);
        SQLStatement stmt = parser.parseCreateTable();

        MySqlSchemaStatVisitor visitor = new MySqlSchemaStatVisitor();
        stmt.accept(visitor);
        
        Column column = visitor.getColumn("test.simple_test","col_key");
        Assert.assertNotNull(column);
        Assert.assertEquals("bigint", column.getDataType());

        {
            String output = SQLUtils.toMySqlString(stmt);
            Assert.assertEquals("CREATE TABLE test.simple_test (\n" +
                    "\tcol_key bigint(20) NOT NULL AUTO_INCREMENT,\n" +
                    "\tcol1 varchar(45) NULL,\n" +
                    "\tcol2 tinyint(4) NULL,\n" +
                    "\tcol3 datetime NULL,\n" +
                    "\tcol4 timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,\n" +
                    "\tPRIMARY KEY (col_key),\n" +
                    "\tUNIQUE INDEX v1_unique (col_key ASC)\n" +
                    ")", output);
        }
        
        {
            String output = SQLUtils.toMySqlString(stmt, SQLUtils.DEFAULT_LCASE_FORMAT_OPTION);
            Assert.assertEquals("create table test.simple_test (\n" +
                    "\tcol_key bigint(20) not null auto_increment,\n" +
                    "\tcol1 varchar(45) null,\n" +
                    "\tcol2 tinyint(4) null,\n" +
                    "\tcol3 datetime null,\n" +
                    "\tcol4 timestamp null default current_timestamp on update current_timestamp,\n" +
                    "\tprimary key (col_key),\n" +
                    "\tunique index v1_unique (col_key asc)\n" +
                    ")", output);
        }
    }
}
