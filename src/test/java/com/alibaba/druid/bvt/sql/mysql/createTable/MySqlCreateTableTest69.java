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

public class MySqlCreateTableTest69 extends MysqlTest {

    @Test
    public void test_one() throws Exception {
        String sql = "CREATE TABLE t1 ("
                + "  s1 INT,"
                + "  s2 INT AS (EXP(s1)) STORED"
                + ")"
                + "PARTITION BY LIST (s2) ("
                + "  PARTITION p1 VALUES IN (1)"
                + ");";

        MySqlStatementParser parser = new MySqlStatementParser(sql);
        SQLStatement stmt = parser.parseCreateTable();

        MySqlSchemaStatVisitor visitor = new MySqlSchemaStatVisitor();
        stmt.accept(visitor);

        {
            String output = SQLUtils.toMySqlString(stmt);
            Assert.assertEquals("CREATE TABLE t1 (\n" +
                    "\ts1 INT,\n" +
                    "\ts2 INT AS (EXP(s1)) STORED\n" +
                    ")\n" +
                    "PARTITION BY LIST (s2) (\n" +
                    "\tPARTITION p1 VALUES IN (1)\n" +
                    ")", output);
        }
        
        {
            String output = SQLUtils.toMySqlString(stmt, SQLUtils.DEFAULT_LCASE_FORMAT_OPTION);
            Assert.assertEquals("create table t1 (\n" +
                    "\ts1 INT,\n" +
                    "\ts2 INT as (EXP(s1)) stored\n" +
                    ")\n" +
                    "partition by list (s2) (\n" +
                    "\tpartition p1 values in (1)\n" +
                    ")", output);
            }
    }
}
