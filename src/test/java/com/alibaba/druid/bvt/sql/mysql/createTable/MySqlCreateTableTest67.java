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

public class MySqlCreateTableTest67 extends MysqlTest {

    @Test
    public void test_one() throws Exception {
        String sql = "CREATE TABLE t1 ( a INT NOT NULL, PRIMARY KEY (a))"
                + " ENGINE=InnoDB TABLESPACE ts1                            "
                + " PARTITION BY RANGE (a) PARTITIONS 3 ("
                + " PARTITION P1 VALUES LESS THAN (2),"
                + " PARTITION P2 VALUES LESS THAN (4) TABLESPACE ts2,"
                + " PARTITION P3 VALUES LESS THAN (6) TABLESPACE ts3);";

        MySqlStatementParser parser = new MySqlStatementParser(sql);
        SQLStatement stmt = parser.parseCreateTable();

        MySqlSchemaStatVisitor visitor = new MySqlSchemaStatVisitor();
        stmt.accept(visitor);

        {
            String output = SQLUtils.toMySqlString(stmt);
            Assert.assertEquals("CREATE TABLE t1 (\n" +
                    "\ta INT NOT NULL,\n" +
                    "\tPRIMARY KEY (a)\n" +
                    ") ENGINE = InnoDB TABLESPACE ts1\n" +
                    "PARTITION BY RANGE (a) PARTITIONS 3 (\n" +
                    "\tPARTITION P1 VALUES LESS THAN (2),\n" +
                    "\tPARTITION P2 VALUES LESS THAN (4)\n" +
                    "\t\tTABLESPACE ts2,\n" +
                    "\tPARTITION P3 VALUES LESS THAN (6)\n" +
                    "\t\tTABLESPACE ts3\n" +
                    ")", output);
        }
        {
            String output = SQLUtils.toMySqlString(stmt, SQLUtils.DEFAULT_LCASE_FORMAT_OPTION);
            Assert.assertEquals("create table t1 (\n" +
                    "\ta INT not null,\n" +
                    "\tprimary key (a)\n" +
                    ") engine = InnoDB tablespace ts1\n" +
                    "partition by range (a) partitions 3 (\n" +
                    "\tpartition P1 values less than (2),\n" +
                    "\tpartition P2 values less than (4)\n" +
                    "\t\ttablespace ts2,\n" +
                    "\tpartition P3 values less than (6)\n" +
                    "\t\ttablespace ts3\n" +
                    ")", output);
        }
    }
}
