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
package com.alibaba.druid.bvt.sql.mysql.select;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.MysqlTest;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.mysql.parser.MySqlStatementParser;
import com.alibaba.druid.sql.parser.SQLParserFeature;

import java.util.List;


public class MySqlSelectTest_214 extends MysqlTest {
    public void test_0() throws Exception {
        String sql = "SELECT numbers, animals, n, a\n" +
                "FROM (\n" +
                "  VALUES\n" +
                "    (ARRAY[2, 5], ARRAY['dog', 'cat', 'bird']),\n" +
                "    (ARRAY[7, 8, 9], ARRAY['cow', 'pig'])\n" +
                ") AS x (numbers, animals)\n" +
                "CROSS JOIN UNNEST(numbers, animals) AS t (n, a);";

        System.out.println(sql);

        MySqlStatementParser parser = new MySqlStatementParser(sql, SQLParserFeature.SelectItemGenerateAlias);
        List<SQLStatement> statementList = parser.parseStatementList();

        assertEquals(1, statementList.size());

        SQLStatement stmt = statementList.get(0);

        assertEquals("SELECT numbers, animals, n, a\n" +
                "FROM (\n" +
                "\tVALUES (ARRAY[2, 5], ARRAY['dog', 'cat', 'bird']),\n" +
                "\t(ARRAY[7, 8, 9], ARRAY['cow', 'pig'])\n" +
                ") AS x (numbers, animals)\n" +
                "\tCROSS JOIN UNNEST(numbers, animals) AS t (n, a);", stmt.toString());

        assertEquals("select numbers, animals, n, a\n" +
                "from (\n" +
                "\tvalues (ARRAY[2, 5], ARRAY['dog', 'cat', 'bird']),\n" +
                "\t(ARRAY[7, 8, 9], ARRAY['cow', 'pig'])\n" +
                ") AS x (numbers, animals)\n" +
                "\tcross join unnest(numbers, animals) as t (n, a);", stmt.clone().toLowerCaseString());
    }

    public void test_1() throws Exception {
        String sql = "SELECT numbers, animals, n, a\n" +
                "FROM (\n" +
                "  VALUES\n" +
                "    (ARRAY[2, 5], ARRAY['dog', 'cat', 'bird']),\n" +
                "    (ARRAY[7, 8, 9], ARRAY['cow', 'pig'])\n" +
                ") AS x (numbers, animals)\n" +
                "CROSS JOIN UNNEST(numbers, animals) AS t (n, a);";

        List<SQLStatement> statementList = SQLUtils.parseStatements(sql, DbType.postgresql);

        assertEquals(1, statementList.size());

        SQLStatement stmt = statementList.get(0);

        assertEquals("SELECT numbers, animals, n, a\n" +
                "FROM (VALUES (ARRAY[2, 5], ARRAY['dog', 'cat', 'bird']), (ARRAY[7, 8, 9], ARRAY['cow', 'pig'])) AS x (numbers, animals)\n" +
                "\tCROSS JOIN UNNEST(numbers, animals) AS t (n, a);", stmt.toString());

        assertEquals("select numbers, animals, n, a\n" +
                "from (values (ARRAY[2, 5], ARRAY['dog', 'cat', 'bird']), (ARRAY[7, 8, 9], ARRAY['cow', 'pig'])) as x (numbers, animals)\n" +
                "\tcross join unnest(numbers, animals) as t (n, a);", stmt.clone().toLowerCaseString());
    }
}