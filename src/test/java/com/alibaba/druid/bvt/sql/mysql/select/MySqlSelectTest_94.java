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
import com.alibaba.druid.sql.ast.statement.SQLCreateTableStatement;
import com.alibaba.druid.sql.ast.statement.SQLSelectStatement;
import com.alibaba.druid.sql.dialect.mysql.parser.MySqlStatementParser;
import com.alibaba.druid.sql.parser.SQLParserFeature;

import java.util.List;

public class MySqlSelectTest_94 extends MysqlTest {

    public void test_0() throws Exception {
        String sql = "select * from test where name = 'cail\\1';";

        MySqlStatementParser parser = new MySqlStatementParser(sql, SQLParserFeature.IgnoreNameQuotes);
        List<SQLStatement> statementList = parser.parseStatementList();

        assertEquals(1, statementList.size());

        SQLSelectStatement stmt = (SQLSelectStatement) statementList.get(0);
        assertEquals("SELECT *\n" + "FROM test\n" + "WHERE name = 'cail1';", stmt.toString());
    }


    public void test_2() throws Exception {
        String sql = "select * from test where name = 'cail\\\\1';";

        MySqlStatementParser parser = new MySqlStatementParser(sql, SQLParserFeature.IgnoreNameQuotes);
        List<SQLStatement> statementList = parser.parseStatementList();

        assertEquals(1, statementList.size());

        SQLSelectStatement stmt = (SQLSelectStatement) statementList.get(0);
        assertEquals("SELECT *\n" + "FROM test\n" + "WHERE name = 'cail\\\\1';", stmt.toString());
    }

    public void test_1() throws Exception {
        String sql = "select * from test where name like 'cai\\%1';";

        MySqlStatementParser parser = new MySqlStatementParser(sql, SQLParserFeature.IgnoreNameQuotes);
        List<SQLStatement> statementList = parser.parseStatementList();

        assertEquals(1, statementList.size());

        SQLSelectStatement stmt = (SQLSelectStatement) statementList.get(0);
        assertEquals("SELECT *\n" + "FROM test\n" + "WHERE name LIKE 'cai\\\\%1';", stmt.toString());
    }

    public void test_3() throws Exception {
        String sql = "select * from test where name = 'cai\\%1';";

        MySqlStatementParser parser = new MySqlStatementParser(sql, SQLParserFeature.IgnoreNameQuotes);
        List<SQLStatement> statementList = parser.parseStatementList();

        assertEquals(1, statementList.size());

        SQLSelectStatement stmt = (SQLSelectStatement) statementList.get(0);
        assertEquals("SELECT *\n" + "FROM test\n" + "WHERE name = 'cai\\\\%1';", stmt.toString());
    }

    public void test_4() throws Exception {
        String sql = "select * from test WHERE name = 'cailijun' or name like 'cai\\%1';";

        MySqlStatementParser parser = new MySqlStatementParser(sql, SQLParserFeature.IgnoreNameQuotes);
        List<SQLStatement> statementList = parser.parseStatementList();

        assertEquals(1, statementList.size());

        SQLSelectStatement stmt = (SQLSelectStatement) statementList.get(0);
        assertEquals("SELECT *\n" + "FROM test\n" + "WHERE name = 'cailijun'\n" + "\tOR name LIKE 'cai\\\\%1';", stmt.toString());
    }

    public void test_5() throws Exception {
        String sql = "select * from test WHERE name = 'cailijun' or name like 'cai\\1';";

        MySqlStatementParser parser = new MySqlStatementParser(sql, SQLParserFeature.IgnoreNameQuotes);
        List<SQLStatement> statementList = parser.parseStatementList();

        assertEquals(1, statementList.size());

        SQLSelectStatement stmt = (SQLSelectStatement) statementList.get(0);
        assertEquals("SELECT *\n" + "FROM test\n" + "WHERE name = 'cailijun'\n" + "\tOR name LIKE 'cai1';", stmt.toString());
    }

    public void test_6() throws Exception {
        String sql = "select * from test WHERE name = 'cai\t';";

        MySqlStatementParser parser = new MySqlStatementParser(sql, SQLParserFeature.IgnoreNameQuotes);
        List<SQLStatement> statementList = parser.parseStatementList();

        assertEquals(1, statementList.size());

        SQLSelectStatement stmt = (SQLSelectStatement) statementList.get(0);
        assertEquals("SELECT *\n" + "FROM test\n" + "WHERE name = 'cai\t';", stmt.toString());
    }

    public void test_7() throws Exception {
        String sql = "/*+ a=1,b=2*/select count(distinct a) from test WHERE name = 'cai\t';";

        MySqlStatementParser parser = new MySqlStatementParser(sql, SQLParserFeature.IgnoreNameQuotes);
        List<SQLStatement> statementList = parser.parseStatementList();

        assertEquals(1, statementList.size());

        SQLSelectStatement stmt = (SQLSelectStatement) statementList.get(0);
        assertEquals("/*+ a=1,b=2*/\n" + "SELECT count(DISTINCT a)\n" + "FROM test\n" + "WHERE name = 'cai\t';", stmt.toString());
    }
    public void test_8() throws Exception {
        String sql = "/*+engine=MPP*/ with tmp1 as ( select uid, ugroups_str from dw.test_multivalue1 where uid = 101 ) select tmp1.uid, tmp1.ugroups_str from tmp1";

        MySqlStatementParser parser = new MySqlStatementParser(sql, SQLParserFeature.IgnoreNameQuotes);
        List<SQLStatement> statementList = parser.parseStatementList();

        assertEquals(1, statementList.size());

        SQLSelectStatement stmt = (SQLSelectStatement) statementList.get(0);
        assertEquals("/*+engine=MPP*/\n"
                     + "WITH tmp1 AS (\n"
                     + "\t\tSELECT uid, ugroups_str\n"
                     + "\t\tFROM dw.test_multivalue1\n"
                     + "\t\tWHERE uid = 101\n"
                     + "\t)\n"
                     + "SELECT tmp1.uid, tmp1.ugroups_str\n"
                     + "FROM tmp1", stmt.toString());
    }

    public void test_9() throws Exception {
        String sql = "create table testkey3( `key` varchar(4), `id` int, primary key (`key`) );";

        MySqlStatementParser parser = new MySqlStatementParser(sql, SQLParserFeature.IgnoreNameQuotes);
        List<SQLStatement> statementList = parser.parseStatementList();

        assertEquals(1, statementList.size());

        SQLCreateTableStatement stmt = (SQLCreateTableStatement) statementList.get(0);
        assertEquals("CREATE TABLE testkey3 (\n"
                     + "\tkey varchar(4),\n"
                     + "\tid int,\n"
                     + "\tPRIMARY KEY (key)\n"
                     + ");", stmt.toString());
    }

}
