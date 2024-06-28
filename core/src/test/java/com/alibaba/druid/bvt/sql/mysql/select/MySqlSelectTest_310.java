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

public class MySqlSelectTest_310
        extends MysqlTest {
    public void test_0() throws Exception {
        String sql = "SELECT orders.extras->\"$.case_no\"";

        SQLStatement stmt = SQLUtils
                .parseSingleStatement(sql, DbType.mysql);

        assertEquals("SELECT orders.extras -> '$.case_no'", stmt.toString());
    }

    public void test_1() throws Exception {
        String sql = "SELECT orders.extras -> \"$.case_no\" > 1";

        SQLStatement stmt = SQLUtils
                .parseSingleStatement(sql, DbType.mysql);

        assertEquals("SELECT orders.extras -> '$.case_no' > 1", stmt.toString());
    }

    public void test_2() throws Exception {
        String sql = "SELECT orders.extras -> '$.case_no' > 1";

        SQLStatement stmt = SQLUtils
                .parseSingleStatement(sql, DbType.mysql);

        assertEquals("SELECT orders.extras -> '$.case_no' > 1", stmt.toString());
    }

    public void test_3() throws Exception {
        String sql = "--abc\nSELECT orders.extras -> '$.case_no' > 1";

        SQLStatement stmt = SQLUtils
                .parseSingleStatement(sql, DbType.mysql, SQLParserFeature.MySQLSupportStandardComment);

        assertEquals("-- abc\n" +
                "SELECT orders.extras -> '$.case_no' > 1", stmt.toString());
    }

    public void test_4() throws Exception {
        String sql = "SELECT 1#abc\n";

        SQLStatement stmt = SQLUtils
                .parseSingleStatement(sql, DbType.mysql, SQLParserFeature.MySQLSupportStandardComment);

        assertEquals("SELECT 1 # abc", stmt.toString());

        // bypass last \n.
        sql = "SELECT 1#abc\n";

        stmt = SQLUtils
                .parseSingleStatement(sql, DbType.mysql, SQLParserFeature.MySQLSupportStandardComment);

        assertEquals("SELECT 1 # abc", stmt.toString());

        // bypass last \r\n.
        sql = "SELECT 1#abc\r\n";

        stmt = SQLUtils
                .parseSingleStatement(sql, DbType.mysql, SQLParserFeature.MySQLSupportStandardComment);

        assertEquals("SELECT 1 # abc", stmt.toString());
    }

    public void test_5() throws Exception {
        String sql = "SELECT 1--abc";

        SQLStatement stmt = SQLUtils
                .parseSingleStatement(sql, DbType.mysql, SQLParserFeature.MySQLSupportStandardComment);

        assertEquals("SELECT 1 -- abc", stmt.toString());

        // bypass last \n.
        sql = "SELECT 1--abc\n";

        stmt = SQLUtils
                .parseSingleStatement(sql, DbType.mysql, SQLParserFeature.MySQLSupportStandardComment);

        assertEquals("SELECT 1 -- abc", stmt.toString());

        // bypass last \r\n.
        sql = "SELECT 1--abc\r\n";

        stmt = SQLUtils
                .parseSingleStatement(sql, DbType.mysql, SQLParserFeature.MySQLSupportStandardComment);

        assertEquals("SELECT 1 -- abc", stmt.toString());
    }
}