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


public class MySqlSelectTest_269 extends MysqlTest {

    public void test_0() throws Exception {
        String sql = "SELECT `IDENTIFIED`, `PRIMARY`, `TABLEGROUP`, `MATCH`, `ROWS`, `OUT`, `ANY`, `CASE`, `DECIMAL`, `REFERENCES`, `RLIKE` FROM SQL_TEST_NEW_20181016_1.THEN__1 WHERE 1=1 LIMIT 65";

        SQLStatement stmt = SQLUtils
                .parseSingleStatement(sql, DbType.mysql
                );

        assertEquals("SELECT `IDENTIFIED`, `PRIMARY`, `TABLEGROUP`, `MATCH`, `ROWS`\n" +
                "\t, `OUT`, `ANY`, `CASE`, `DECIMAL`, `REFERENCES`\n" +
                "\t, `RLIKE`\n" +
                "FROM SQL_TEST_NEW_20181016_1.THEN__1\n" +
                "WHERE 1 = 1\n" +
                "LIMIT 65", stmt.toString());
    }


}