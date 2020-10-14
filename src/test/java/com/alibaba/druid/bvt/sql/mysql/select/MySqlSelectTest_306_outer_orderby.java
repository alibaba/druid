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
import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.expr.SQLCharExpr;
import com.alibaba.druid.sql.ast.expr.SQLMethodInvokeExpr;

public class MySqlSelectTest_306_outer_orderby
        extends MysqlTest {

    public void test_0() throws Exception {
        String sql = "SELECT * \n" +
                "FROM (\n" +
                "                SELECT orderstatus, clerk, sum(totalprice) sales\n" +
                "                FROM orders\n" +
                "                GROUP BY orderstatus, clerk\n" +
                "             )  \n" +
                "ORDER BY orderstatus, clerk";

        SQLStatement stmt = SQLUtils.parseSingleStatement(sql, DbType.mysql);

        assertEquals("SELECT *\n" +
                "FROM (\n" +
                "\tSELECT orderstatus, clerk, sum(totalprice) AS sales\n" +
                "\tFROM orders\n" +
                "\tGROUP BY orderstatus, clerk\n" +
                ")\n" +
                "ORDER BY orderstatus, clerk", stmt.toString());
    }

    public void test_1() throws Exception {
        String sql = "SELECT * \n" +
                "FROM (select c2 from test order by c1" +
                "             )  \n" +
                "ORDER BY orderstatus, clerk";

        SQLStatement stmt = SQLUtils.parseSingleStatement(sql, DbType.mysql);

        assertEquals("SELECT *\n" +
                "FROM (\n" +
                "\tSELECT c2\n" +
                "\tFROM test\n" +
                "\tORDER BY c1\n" +
                ")\n" +
                "ORDER BY orderstatus, clerk", stmt.toString());
    }

    public void test_2() throws Exception {
        String sql = "SELECT * \n" +
                "FROM (select c2 from test order by c1 limit 10" +
                "             )  \n" +
                "ORDER BY orderstatus, clerk";

        SQLStatement stmt = SQLUtils.parseSingleStatement(sql, DbType.mysql);

        assertEquals("SELECT *\n" +
                "FROM (\n" +
                "\tSELECT c2\n" +
                "\tFROM test\n" +
                "\tORDER BY c1\n" +
                "\tLIMIT 10\n" +
                ")\n" +
                "ORDER BY orderstatus, clerk", stmt.toString());
    }

    public void test_3() throws Exception {
        String sql = "SELECT * \n" +
                "FROM (select c2 from test order by c1 limit 10" +
                "             )  \n" +
                "ORDER BY orderstatus, clerk";

        SQLStatement stmt = SQLUtils.parseSingleStatement(sql, DbType.mysql);

        assertEquals("SELECT *\n" +
                "FROM (\n" +
                "\tSELECT c2\n" +
                "\tFROM test\n" +
                "\tORDER BY c1\n" +
                "\tLIMIT 10\n" +
                ")\n" +
                "ORDER BY orderstatus, clerk", stmt.toString());
    }

    public void test_4() throws Exception {
        String sql = "SELECT * from ((select 2 order by 2) order by 1) a";

        SQLStatement stmt = SQLUtils.parseSingleStatement(sql, DbType.mysql);

        assertEquals("SELECT *\n" +
                "FROM (\n" +
                "\tSELECT 2\n" +
                "\tORDER BY 2\n" +
                ") a", stmt.toString());
    }

    public void test_5() throws Exception {
        String sql = "SELECT * \n" +
                "FROM (select c2 from test order by c1 limit 10" +
                "             )  \n" +
                "limit 20";

        SQLStatement stmt = SQLUtils.parseSingleStatement(sql, DbType.mysql);

        assertEquals("SELECT *\n" +
                "FROM (\n" +
                "\tSELECT c2\n" +
                "\tFROM test\n" +
                "\tORDER BY c1\n" +
                "\tLIMIT 10\n" +
                ")\n" +
                "LIMIT 20", stmt.toString());
    }

    public void test_6() throws Exception {
        String sql = "SELECT * \n" +
                "FROM (select 2" +
                "             )  \n" +
                "limit 20";

        SQLStatement stmt = SQLUtils.parseSingleStatement(sql, DbType.mysql);

        assertEquals("SELECT *\n" +
                "FROM (\n" +
                "\tSELECT 2\n" +
                ")\n" +
                "LIMIT 20", stmt.toString());
    }

    public void test_7() throws Exception {
        String sql = "select * from ((select * from test) limit 1)  ";

        SQLStatement stmt = SQLUtils.parseSingleStatement(sql, DbType.mysql);

        assertEquals("SELECT *\n" +
                "FROM (\n" +
                "\tSELECT *\n" +
                "\tFROM test\n" +
                "\tLIMIT 1\n" +
                ")", stmt.toString());
    }

    public void test_8() throws Exception {
        String sql = "select * from ((select * from test) order by a)  ";

        SQLStatement stmt = SQLUtils.parseSingleStatement(sql, DbType.mysql);

        assertEquals("SELECT *\n" +
                "FROM (\n" +
                "\tSELECT *\n" +
                "\tFROM test\n" +
                "\tORDER BY a\n" +
                ")", stmt.toString());
    }
}