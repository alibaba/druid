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

import java.util.List;

import org.junit.Assert;
import junit.framework.TestCase;

import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.mysql.parser.MySqlStatementParser;
import com.alibaba.druid.sql.dialect.mysql.visitor.MySqlOutputVisitor;
import com.alibaba.druid.sql.parser.SQLStatementParser;

public class InformationFunctionsTest extends TestCase {

    public void test_0() throws Exception {
        String sql = "SELECT BENCHMARK(1000000,ENCODE('hello','goodbye'))";

        SQLStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> stmtList = parser.parseStatementList();

        String text = output(stmtList);

        Assert.assertEquals("SELECT BENCHMARK(1000000, ENCODE('hello', 'goodbye'));", text);
    }

    public void test_1() throws Exception {
        String sql = "SELECT CHARSET('abc');";

        SQLStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> stmtList = parser.parseStatementList();

        String text = output(stmtList);

        Assert.assertEquals("SELECT CHARSET('abc');", text);
    }

    public void test_2() throws Exception {
        String sql = "SELECT CHARSET(CONVERT('abc' USING utf8));";

        SQLStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> stmtList = parser.parseStatementList();

        String text = output(stmtList);

        Assert.assertEquals("SELECT CHARSET(CONVERT('abc' USING utf8));", text);
    }

    public void test_3() throws Exception {
        String sql = "SELECT CHARSET(USER());";

        SQLStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> stmtList = parser.parseStatementList();

        String text = output(stmtList);

        Assert.assertEquals("SELECT CHARSET(USER());", text);
    }

    public void test_4() throws Exception {
        String sql = "SELECT COERCIBILITY('abc' COLLATE latin1_swedish_ci);";

        SQLStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> stmtList = parser.parseStatementList();

        String text = output(stmtList);

        Assert.assertEquals("SELECT COERCIBILITY('abc' COLLATE latin1_swedish_ci);", text);
    }

    public void test_5() throws Exception {
        String sql = "SELECT COLLATION('abc');";

        SQLStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> stmtList = parser.parseStatementList();

        String text = output(stmtList);

        Assert.assertEquals("SELECT COLLATION('abc');", text);
    }

    public void test_6() throws Exception {
        String sql = "SELECT * FROM mysql.user;";

        SQLStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> stmtList = parser.parseStatementList();

        String text = output(stmtList);

        Assert.assertEquals("SELECT *\nFROM mysql.user;", text);
    }

    public void test_7() throws Exception {
        String sql = "SELECT CURRENT_USER();";

        SQLStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> stmtList = parser.parseStatementList();

        String text = output(stmtList);

        Assert.assertEquals("SELECT CURRENT_USER();", text);
    }

    public void test_8() throws Exception {
        String sql = "SELECT DATABASE();";

        SQLStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> stmtList = parser.parseStatementList();

        String text = output(stmtList);

        Assert.assertEquals("SELECT DATABASE();", text);
    }

    public void test_9() throws Exception {
        String sql = "SELECT SQL_CALC_FOUND_ROWS * FROM tbl_name;";

        SQLStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> stmtList = parser.parseStatementList();

        String text = output(stmtList);

        Assert.assertEquals("SELECT SQL_CALC_FOUND_ROWS *\nFROM tbl_name;", text);
    }

    public void test_10() throws Exception {
        String sql = "SELECT FOUND_ROWS();";

        SQLStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> stmtList = parser.parseStatementList();

        String text = output(stmtList);

        Assert.assertEquals("SELECT FOUND_ROWS();", text);
    }

    public void test_11() throws Exception {
        String sql = "SELECT LAST_INSERT_ID();";

        SQLStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> stmtList = parser.parseStatementList();

        String text = output(stmtList);

        Assert.assertEquals("SELECT LAST_INSERT_ID();", text);
    }

    public void test_12() throws Exception {
        String sql = "SELECT ROW_COUNT();";

        SQLStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> stmtList = parser.parseStatementList();

        String text = output(stmtList);

        Assert.assertEquals("SELECT ROW_COUNT();", text);
    }

    public void test_13() throws Exception {
        String sql = "SELECT USER();";

        SQLStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> stmtList = parser.parseStatementList();

        String text = output(stmtList);

        Assert.assertEquals("SELECT USER();", text);
    }

    public void test_14() throws Exception {
        String sql = "SELECT VERSION();";

        SQLStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> stmtList = parser.parseStatementList();

        String text = output(stmtList);

        Assert.assertEquals("SELECT VERSION();", text);
    }

    private String output(List<SQLStatement> stmtList) {
        StringBuilder out = new StringBuilder();

        for (SQLStatement stmt : stmtList) {
            stmt.accept(new MySqlOutputVisitor(out));
            out.append(";");
        }

        return out.toString();
    }
}
