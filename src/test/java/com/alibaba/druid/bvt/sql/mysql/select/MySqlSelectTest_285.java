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


public class MySqlSelectTest_285 extends MysqlTest {

    public void test_0() throws Exception {
        String sql = "select 'a', \"a rrr122\" , avg(`id`) as 'x Y Z' from test4dmp.test where string_test = \"abdfeed\" and date_test > \"1991-01-10 00:12:11\" group by id having `x Y Z` > 10 order by 3 limit 5;";

        SQLStatement stmt = SQLUtils
                .parseSingleStatement(sql, DbType.mysql, SQLParserFeature.SupportUnicodeCodePoint);

        assertEquals("SELECT 'a', 'a rrr122', avg(`id`) AS \"x Y Z\"\n" +
                "FROM test4dmp.test\n" +
                "WHERE string_test = 'abdfeed'\n" +
                "\tAND date_test > '1991-01-10 00:12:11'\n" +
                "GROUP BY id\n" +
                "HAVING `x Y Z` > 10\n" +
                "ORDER BY 3\n" +
                "LIMIT 5;", stmt.toString());
    }



}