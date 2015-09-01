/*
 * Copyright 1999-2101 Alibaba Group Holding Ltd.
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
package com.alibaba.druid.bvt.sql.mysql;

import com.alibaba.druid.sql.MysqlTest;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.mysql.parser.MySqlStatementParser;

import java.util.List;
/**
*测试char()  CHAR(N,... [USING charset_name])
*/
public class MySqlTest_Char extends MysqlTest {

    public void test_0() throws Exception {
        String sql = "SELECT char(888 using utf8)";
        MySqlStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();
        print(statementList);
        assertEquals(output(statementList),"SELECT char(888 USING utf8)");
    }
    public void test_1() throws Exception {
        String sql = "SELECT char('abc8a9b10c' using utf8)";
        MySqlStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();
        print(statementList);
        assertEquals(output(statementList),"SELECT char('abc8a9b10c' USING utf8)");
    }

    public void test_2() throws Exception {
        String sql = "SELECT char(12,321,'lq9s9f','abc8a9b10c' using utf8)";
        MySqlStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();
        print(statementList);
        assertEquals(output(statementList),"SELECT char(12, 321, 'lq9s9f', 'abc8a9b10c' USING utf8)");
    }

    public void test_3() throws Exception {
        String sql = "SELECT char(12,321,'lq9s9f','abc8a9b10c')";
        MySqlStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();
        print(statementList);
        assertEquals(output(statementList),"SELECT char(12, 321, 'lq9s9f', 'abc8a9b10c')");
    }

    public void test_4() throws Exception {
        String sql = "SELECT CHAR(77,121,83,81,'76' using utf8)";
        MySqlStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();
        print(statementList);
        assertEquals(output(statementList),"SELECT CHAR(77, 121, 83, 81, '76' USING utf8)");
    }
}
