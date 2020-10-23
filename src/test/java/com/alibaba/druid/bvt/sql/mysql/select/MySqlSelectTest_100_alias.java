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


public class MySqlSelectTest_100_alias extends MysqlTest {

    public void test_2() throws Exception {
        String sql = "\n" +
                "select id as \"id\", name as \"\\\"abc\\\"\" from test_hash_tb";

        System.out.println(sql);

        MySqlStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();

        assertEquals(1, statementList.size());

        String text = output(statementList);
        System.out.println(text);
        assertEquals("SELECT id AS \"id\", name AS \"\\\"abc\\\"\"\n" +
                "FROM test_hash_tb", text);
    }

    public void test_3() throws Exception {
        String sql = "select count(Distinct id) from t";

        MySqlStatementParser parser = new MySqlStatementParser(sql, SQLParserFeature.SelectItemGenerateAlias);
        List<SQLStatement> statementList = parser.parseStatementList();

        assertEquals(1, statementList.size());

        String text = output(statementList);
        assertEquals("SELECT count(DISTINCT id) AS `count(Distinct id)`\n" +
                "FROM t", text);
    }

    public void test_4() throws Exception {
        String sql = "select date '2010-01-01'";

        MySqlStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();

        assertEquals(1, statementList.size());

        String text = output(statementList);
        assertEquals("SELECT DATE '2010-01-01'", text);
    }

    public void test_5() throws Exception {
        String sql = "select 1 from t";

        MySqlStatementParser parser = new MySqlStatementParser(sql, SQLParserFeature.SelectItemGenerateAlias);
        List<SQLStatement> statementList = parser.parseStatementList();

        assertEquals(1, statementList.size());

        String text = output(statementList);
        assertEquals("SELECT 1\n" +
                "FROM t", text);
    }

    public void test_6() throws Exception {
        String sql = "select -f1 from t";

        MySqlStatementParser parser = new MySqlStatementParser(sql, SQLParserFeature.SelectItemGenerateAlias);
        List<SQLStatement> statementList = parser.parseStatementList();

        assertEquals(1, statementList.size());

        String text = output(statementList);
        assertEquals("SELECT -f1 AS -f1\n" +
                "FROM t", text);
    }

    public void test_7() throws Exception {
        String sql = "SELECT 'schema_name'\n" +
                "FROM information_schema.schemata";

        MySqlStatementParser parser = new MySqlStatementParser(sql, SQLParserFeature.SelectItemGenerateAlias);
        List<SQLStatement> statementList = parser.parseStatementList();

        assertEquals(1, statementList.size());

        String text = output(statementList);
        assertEquals("SELECT 'schema_name'\n" +
                "FROM information_schema.schemata", text);
    }

    public void test_8() throws Exception {
        String sql = "SELECT -\"calcs\".\"num0\" AS \"TEMP(Test)(4188722171)(0)\"\n" +
                "FROM mysql.tableau_mysql.\"calcs\"\n" +
                "GROUP BY 1";

        MySqlStatementParser parser = new MySqlStatementParser(sql, SQLParserFeature.SelectItemGenerateAlias);
        List<SQLStatement> statementList = parser.parseStatementList();

        assertEquals(1, statementList.size());

        String text = output(statementList);
        assertEquals("SELECT -\"calcs\".\"num0\" AS \"TEMP(Test)(4188722171)(0)\"\n" +
                "FROM mysql.tableau_mysql.\"calcs\"\n" +
                "GROUP BY 1", text);
    }

    public void test_true() throws Exception {
        String sql = "select true from t";

        MySqlStatementParser parser = new MySqlStatementParser(sql, SQLParserFeature.SelectItemGenerateAlias);
        List<SQLStatement> statementList = parser.parseStatementList();

        assertEquals(1, statementList.size());

        String text = output(statementList);
        assertEquals("SELECT true\n" +
                "FROM t", text);
    }
}