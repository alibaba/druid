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
import com.alibaba.druid.sql.repository.SchemaRepository;
import com.alibaba.druid.util.JdbcConstants;

public class MySqlCreateTable_showColumns_test_4 extends MysqlTest {

    public void test_0() throws Exception {

        SchemaRepository repository = new SchemaRepository(JdbcConstants.MYSQL);
        String sql = "create table tmp_eric (pk int key, ia int unique);";

        SQLStatement stmt = SQLUtils.parseSingleMysqlStatement(sql);
        assertEquals("CREATE TABLE tmp_eric (\n" +
                "\tpk int PRIMARY KEY,\n" +
                "\tia int UNIQUE\n" +
                ");", stmt.toString());
    }
}
