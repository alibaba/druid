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

import com.alibaba.druid.sql.MysqlTest;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.mysql.parser.MySqlStatementParser;

import java.util.List;


public class MySqlSelectTest_96_istrue extends MysqlTest {

    public void test_0() throws Exception {
        String sql = "SELECT * FROM student \n" +
                "where  id > 123 is true";

        MySqlStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();

        assertEquals(1, statementList.size());

        String text = output(statementList);
        assertEquals("SELECT *\n" +
                "FROM student\n" +
                "WHERE id > 123 IS true", text);
    }

    public void test_1() throws Exception {
        String sql = "SELECT * FROM student \n" +
                "where  id = 123 is true";

        MySqlStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();

        assertEquals(1, statementList.size());

        String text = output(statementList);
        assertEquals("SELECT *\n" +
                "FROM student\n" +
                "WHERE id = 123 IS true", text);
    }
}