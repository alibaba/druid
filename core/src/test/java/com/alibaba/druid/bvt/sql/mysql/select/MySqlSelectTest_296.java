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
import com.alibaba.druid.sql.parser.SQLParserFeature;

public class MySqlSelectTest_296
        extends MysqlTest {
    public void test_0() throws Exception {
        String sql = "SELECT x, array_agg(y ORDER BY y), array_agg(y ORDER BY y) FILTER (WHERE y > 1), count(*) \n" +
                "FROM (VALUES    (1, 3),    (1, 1),    (2, 3),    (2, 4)) t(x, y) \n" +
                "GROUP BY GROUPING SETS ((), (x))";

        SQLStatement stmt = SQLUtils
                .parseSingleStatement(sql, DbType.mysql, SQLParserFeature.SupportUnicodeCodePoint);

        assertEquals("SELECT x, array_agg(y ORDER BY y), array_agg(y ORDER BY y) FILTER (WHERE y > 1)\n" +
                "\t, count(*)\n" +
                "FROM (\n" +
                "\tVALUES (1, 3),\n" +
                "\t(1, 1),\n" +
                "\t(2, 3),\n" +
                "\t(2, 4)\n" +
                ") AS t (x, y)\n" +
                "GROUP BY GROUPING SETS ((), (x))", stmt.toString());
    }


}