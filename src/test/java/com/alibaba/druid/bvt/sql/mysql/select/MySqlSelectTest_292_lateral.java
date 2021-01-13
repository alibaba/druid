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


public class MySqlSelectTest_292_lateral extends MysqlTest {

    public void test_0() throws Exception {
        String sql = "SELECT * FROM t CROSS JOIN LATERAL (VALUES 1) ";

        SQLStatement stmt = SQLUtils
                .parseSingleStatement(sql, DbType.mysql);

        assertEquals("SELECT *\n" +
                "FROM t\n" +
                "\tCROSS JOIN LATERAL((\n" +
                "\t\tVALUES (1)\n" +
                "\t))", stmt.toString());

        System.out.println(stmt.toString());
    }

    public void test_1() throws Exception {
        String sql = "SELECT * FROM t FULL JOIN LATERAL (VALUES 1) ON true";

        SQLStatement stmt = SQLUtils
                .parseSingleStatement(sql, DbType.mysql);

        assertEquals("SELECT *\n" +
                "FROM t\n" +
                "\tFULL JOIN LATERAL((\n" +
                "\t\tVALUES (1)\n" +
                "\t)) ON true", stmt.toString());

        System.out.println(stmt.toString());
    }

    public void test_2() throws Exception {
        String sql = "SELECT * FROM t, LATERAL (VALUES 1) a(x)";

        SQLStatement stmt = SQLUtils
                .parseSingleStatement(sql, DbType.mysql);

        assertEquals("SELECT *\n" +
                "FROM t, LATERAL((\n" +
                "\t\tVALUES (1)\n" +
                "\t))  AS a (x)", stmt.toString());

        System.out.println(stmt.toString());
    }

    public void test_3() throws Exception {
        String sql = "SELECT numbers, n, a\n" +
                "FROM (\n" +
                "  VALUES\n" +
                "    (ARRAY[2, 5]),\n" +
                "    (ARRAY[7, 8, 9])\n" +
                ") AS x (numbers)\n" +
                "CROSS JOIN UNNEST(numbers) WITH ORDINALITY AS t (n, a);";

        SQLStatement stmt = SQLUtils
                .parseSingleStatement(sql, DbType.mysql);

        assertEquals("SELECT numbers, n, a\n" +
                "FROM (\n" +
                "\tVALUES (ARRAY[2, 5]), \n" +
                "\t(ARRAY[7, 8, 9])\n" +
                ") AS x (numbers)\n" +
                "\tCROSS JOIN UNNEST(numbers) WITH ORDINALITY AS t (n, a);", stmt.toString());

        System.out.println(stmt.toString());
    }


}