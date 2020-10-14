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

public class MySqlSelectTest_302_agg
        extends MysqlTest {

    public void test_0() throws Exception {
        String sql = "SELECT (CASE WHEN (`test4dmp`.`sum`(1) OVER (PARTITION BY 1) = 1) THEN 1 END) `case when sum(1) OVER (PARTITION BY 1 ) =1 then 1 end`\n" +
                "FROM\n" +
                "  test";

        SQLStatement stmt = SQLUtils
                .parseSingleStatement(sql, DbType.mysql);

        assertEquals("SELECT CASE \n" +
                "\t\tWHEN `test4dmp`.`sum`(1) OVER (PARTITION BY 1 ) = 1 THEN 1\n" +
                "\tEND AS `case when sum(1) OVER (PARTITION BY 1 ) =1 then 1 end`\n" +
                "FROM test", stmt.toString());
    }

    public void test_1() throws Exception {
        String sql = "SELECT (CASE WHEN (`test4dmp`.`sum`(1) = 1) THEN 1 END) " +
                "FROM\n" +
                "  test";

        SQLStatement stmt = SQLUtils
                .parseSingleStatement(sql, DbType.mysql);

        assertEquals("SELECT CASE \n" +
                "\t\tWHEN `test4dmp`.`sum`(1) = 1 THEN 1\n" +
                "\tEND\n" +
                "FROM test", stmt.toString());
    }

    public void test_2() throws Exception {
        String sql = "SELECT sum(1) from " +
                "  test";

        SQLStatement stmt = SQLUtils
                .parseSingleStatement(sql, DbType.mysql);

        assertEquals("SELECT sum(1)\n" +
                "FROM test", stmt.toString());
    }

    public void test_3() throws Exception {
        String sql = "SELECT db.sum(1) from " +
                "  test";

        SQLStatement stmt = SQLUtils
                .parseSingleStatement(sql, DbType.mysql);

        assertEquals("SELECT db.sum(1)\n" +
                "FROM test", stmt.toString());
    }

}