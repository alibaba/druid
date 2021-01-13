/*
 * Copyright 1999-2017 Alibaba Group Holding Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.alibaba.druid.bvt.sql.mysql.select;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.MysqlTest;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;


public class MySqlSelectTest_275 extends MysqlTest {

    public void test_0() throws Exception {
        String sql = "(select id from test4dmp.test_odps where id >100 order by id) order by id2 limit 10\n" +
                "\n";

        SQLStatement stmt = SQLUtils
                .parseSingleStatement(sql, DbType.mysql);

        assertEquals("(SELECT id\n" +
                "FROM test4dmp.test_odps\n" +
                "WHERE id > 100\n" +
                "ORDER BY id)\n" +
                "ORDER BY id2\n" +
                "LIMIT 10", stmt.toString());
    }


    public void test_1() throws Exception {
        String sql = "(select id from test4dmp.test_odps where id >100) order by id limit 10\n" +
                "\n";

        SQLStatement stmt = SQLUtils
                .parseSingleStatement(sql, DbType.mysql);

        assertEquals("(SELECT id\n" +
                "FROM test4dmp.test_odps\n" +
                "WHERE id > 100\n" +
                "ORDER BY id\n" +
                "LIMIT 10)", stmt.toString());
    }



}