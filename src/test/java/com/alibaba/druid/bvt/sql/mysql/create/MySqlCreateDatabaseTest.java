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
package com.alibaba.druid.bvt.sql.mysql.create;

import com.alibaba.druid.sql.MysqlTest;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.mysql.parser.MySqlStatementParser;
import com.alibaba.druid.sql.dialect.mysql.visitor.MySqlSchemaStatVisitor;
import org.junit.Assert;
import org.junit.Test;

public class MySqlCreateDatabaseTest extends MysqlTest {

    @Test
    public void test_one() throws Exception {
        String sql = "create database if not exists a";

        MySqlStatementParser parser = new MySqlStatementParser(sql);
        SQLStatement stmt = parser.parseStatement();

        MySqlSchemaStatVisitor visitor = new MySqlSchemaStatVisitor();
        stmt.accept(visitor);

        String output = SQLUtils.toMySqlString(stmt);
        Assert.assertEquals("CREATE DATABASE IF NOT EXISTS a", output);
    }

    // for ads
    @Test
    public void test_2() throws Exception {
        String sql = "create database test_cascade for 'ALIYUN$test@aliyun.com' options(resourceType=ecu ecu_type=c1 ecu_count=2)";

        MySqlStatementParser parser = new MySqlStatementParser(sql);
        SQLStatement stmt = parser.parseStatement();

        String output = SQLUtils.toMySqlString(stmt);
        Assert.assertEquals("CREATE DATABASE test_cascade FOR 'ALIYUN$test@aliyun.com' OPTIONS (ecu_type=c1 ecu_count=2 resourceType=ecu )", output);
    }

    // for ads
    @Test
    public void test_3() throws Exception {
        String sql = "CREATE EXTERNAL TABLE IF NOT EXISTS ots_0.ots_table_0 (pk VARCHAR, a BIGINT, b BIGINT) "
                    + "STORED BY 'OTS' WITH (column_mapping = 'pk:pk,a:col1,b:col2', serializer = 'default') "
                    + "COMMENT 'test_ots_table_0'";

        MySqlStatementParser parser = new MySqlStatementParser(sql);
        SQLStatement stmt = parser.parseStatement();

        String output = SQLUtils.toMySqlString(stmt);
        Assert.assertEquals("CREATE EXTERNAL TABLE IF NOT EXISTS ots_0.ots_table_0 (\n"
                            + "\tpk VARCHAR,\n"
                            + "\ta BIGINT,\n"
                            + "\tb BIGINT\n"
                            + ") COMMENT 'test_ots_table_0'\n"
                            + " STORED BY 'OTS'\n"
                            + " WITH (column_mapping = 'pk:pk,a:col1,b:col2', serializer = 'default')", output);
    }
}
