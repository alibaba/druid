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
package com.alibaba.druid.bvt.sql.mysql;

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.mysql.parser.MySqlStatementParser;
import com.alibaba.druid.sql.parser.SQLStatementParser;
import com.alibaba.druid.util.JdbcConstants;
import junit.framework.TestCase;
import org.junit.Assert;

import java.util.List;

public class StringComparisonFunctionsTest extends TestCase {

    public void test_0() throws Exception {
        String sql = "SELECT '?' LIKE 'ae' COLLATE latin1_german2_ci;";

        SQLStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> stmtList = parser.parseStatementList();

        String text = output(stmtList);

        Assert.assertEquals("SELECT '?' LIKE 'ae' COLLATE latin1_german2_ci;", text);
    }

    public void test_1() throws Exception {
        String sql = "SELECT '?' = 'ae' COLLATE latin1_german2_ci;";

        SQLStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> stmtList = parser.parseStatementList();

        String text = output(stmtList);

        Assert.assertEquals("SELECT '?' = 'ae' COLLATE latin1_german2_ci;", text);
    }

    public void test_2() throws Exception {
        String sql = "SELECT 'a' = 'a ', 'a' LIKE 'a ';";

        SQLStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> stmtList = parser.parseStatementList();

        String text = output(stmtList);

        Assert.assertEquals("SELECT 'a' = 'a ', 'a' LIKE 'a ';", text);
    }

    public void test_3() throws Exception {
        String sql = "SELECT 'David!' LIKE 'David_';";

        SQLStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> stmtList = parser.parseStatementList();

        String text = output(stmtList);

        Assert.assertEquals("SELECT 'David!' LIKE 'David_';", text);
    }

    public void test_4() throws Exception {
        String sql = "SELECT 'David!' LIKE '%D%v%';";

        SQLStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> stmtList = parser.parseStatementList();

        String text = output(stmtList);

        Assert.assertEquals("SELECT 'David!' LIKE '%D%v%';", text);
    }

    public void test_5() throws Exception {
        String sql = "SELECT 'David!' LIKE 'David\\_';";

        SQLStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> stmtList = parser.parseStatementList();

        String text = output(stmtList);

        Assert.assertEquals("SELECT 'David!' LIKE 'David\\_';", text);
    }

    public void test_6() throws Exception {
        String sql = "SELECT 'David_' LIKE 'David|_' ESCAPE '|'";

        SQLStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> stmtList = parser.parseStatementList();

        String text = output(stmtList);

        Assert.assertEquals("SELECT 'David_' LIKE 'David|_' ESCAPE '|'", text);
    }

    public void test_7() throws Exception {
        String sql = "SELECT 'abc' LIKE BINARY 'ABC'";

        SQLStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> stmtList = parser.parseStatementList();

        String text = output(stmtList);

        Assert.assertEquals("SELECT 'abc' LIKE BINARY 'ABC'", text);
    }

    public void test_8() throws Exception {
        String sql = "SELECT 10 LIKE '1%'";

        SQLStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> stmtList = parser.parseStatementList();

        String text = output(stmtList);

        Assert.assertEquals("SELECT 10 LIKE '1%'", text);
    }

    public void test_9() throws Exception {
        String sql = "SELECT filename, filename LIKE '%\\\\' FROM t1";

        SQLStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> stmtList = parser.parseStatementList();

        String text = output(stmtList);

        Assert.assertEquals("SELECT filename, filename LIKE '%\\\\'\nFROM t1", text);
    }

    public void test_10() throws Exception {
        String sql = "SELECT STRCMP('text', 'text2')";

        SQLStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> stmtList = parser.parseStatementList();

        String text = output(stmtList);

        Assert.assertEquals("SELECT STRCMP('text', 'text2')", text);
    }

    public void test_11() throws Exception {
        String sql = "SET @s1 = _latin1 'x' COLLATE latin1_general_ci;";

        SQLStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> stmtList = parser.parseStatementList();

        String text = output(stmtList);

        Assert.assertEquals("SET @s1 = _latin1 'x' COLLATE latin1_general_ci;", text);
    }

    public void test_12() throws Exception {
        String sql = "SET @s2 = _latin1 'X' COLLATE latin1_general_ci;";

        SQLStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> stmtList = parser.parseStatementList();

        String text = output(stmtList);

        Assert.assertEquals("SET @s2 = _latin1 'X' COLLATE latin1_general_ci;", text);
    }

    public void test_13() throws Exception {
        String sql = "SELECT STRCMP(@s1, @s2), STRCMP(@s3, @s4);";

        SQLStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> stmtList = parser.parseStatementList();

        String text = output(stmtList);

        Assert.assertEquals("SELECT STRCMP(@s1, @s2)\n" +
                "\t, STRCMP(@s3, @s4);", text);
    }

    public void test_14() throws Exception {
        String sql = "SELECT STRCMP(@s1, @s3 COLLATE latin1_general_ci);";

        SQLStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> stmtList = parser.parseStatementList();

        String text = output(stmtList);

        Assert.assertEquals("SELECT STRCMP(@s1, @s3 COLLATE latin1_general_ci);", text);
    }

    private String output(List<SQLStatement> stmtList) {
        return SQLUtils.toSQLString(stmtList, JdbcConstants.MYSQL);
    }
}
