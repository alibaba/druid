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


public class MySqlSelectTest_97_alias extends MysqlTest {

    public void test_0() throws Exception {
        String sql = "SELECT * FROM student \n" +
                "where  id = \"123\"";

        MySqlStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();

        assertEquals(1, statementList.size());

        String text = output(statementList);
        assertEquals("SELECT *\n" +
                "FROM student\n" +
                "WHERE id = '123'", text);
    }

    public void test_1() throws Exception {
        String sql = "\n" +
                "select * from test_hash_tb a join test_hash_tb b on a.id=b.id join test_hash_tb c  on b.id=c.id where a.id=\"xx\"";

        MySqlStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();

        assertEquals(1, statementList.size());

        String text = output(statementList);
        assertEquals("SELECT *\n" +
                "FROM test_hash_tb a\n" +
                "\tJOIN test_hash_tb b ON a.id = b.id\n" +
                "\tJOIN test_hash_tb c ON b.id = c.id\n" +
                "WHERE a.id = 'xx'", text);
    }

}