/*
 * Copyright 1999-2018 Alibaba Group Holding Ltd.
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

import com.alibaba.druid.sql.SQLUtils;
import org.junit.Assert;
import junit.framework.TestCase;

import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.mysql.parser.MySqlStatementParser;
import com.alibaba.druid.sql.dialect.mysql.visitor.MySqlOutputVisitor;
import com.alibaba.druid.sql.parser.SQLStatementParser;

public class BitFunctionsTest extends TestCase {

    public void test_0() throws Exception {
        String sql = "SELECT 29 | 15";

        SQLStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> stmtList = parser.parseStatementList();

        String text = output(stmtList);

        Assert.assertEquals("SELECT 29 | 15", text);
    }

    public void test_1() throws Exception {
        String sql = "SELECT 29 & 15";

        SQLStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> stmtList = parser.parseStatementList();

        String text = output(stmtList);

        Assert.assertEquals("SELECT 29 & 15", text);
    }

    public void test_2() throws Exception {
        String sql = "SELECT 1 ^ 1";

        SQLStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> stmtList = parser.parseStatementList();

        String text = output(stmtList);

        Assert.assertEquals("SELECT 1 ^ 1", text);
    }

    public void test_3() throws Exception {
        String sql = "SELECT 1 ^ 0";

        SQLStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> stmtList = parser.parseStatementList();

        String text = output(stmtList);

        Assert.assertEquals("SELECT 1 ^ 0", text);
    }

    public void test_4() throws Exception {
        String sql = "SELECT 1 << 2";

        SQLStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> stmtList = parser.parseStatementList();

        String text = output(stmtList);

        Assert.assertEquals("SELECT 1 << 2", text);
    }

    public void test_5() throws Exception {
        String sql = "SELECT 4 >> 2";

        SQLStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> stmtList = parser.parseStatementList();

        String text = output(stmtList);

        assertEquals("SELECT 4 >> 2", text);
    }

    public void test_6() throws Exception {
        String sql = "SELECT 5 & ~1";

        SQLStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> stmtList = parser.parseStatementList();

        String text = output(stmtList);

        assertEquals("SELECT 5 & ~1", text);
    }

    public void test_7() throws Exception {
        String sql = "SELECT BIT_COUNT(29), BIT_COUNT(b'101010');";

        SQLStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> stmtList = parser.parseStatementList();

        String text = output(stmtList);

        Assert.assertEquals("SELECT BIT_COUNT(29), BIT_COUNT(b'101010');", text);
    }

    public void test_8() throws Exception {
        String sql = "SET @v2 = CAST(0b1000001 AS UNSIGNED), @v3 = 0b1000001+0;";

        SQLStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> stmtList = parser.parseStatementList();

        String text = output(stmtList);

        Assert.assertEquals("SET @v2 = CAST(b'1000001' AS UNSIGNED), @v3 = b'1000001' + 0;", text);
    }

    public void test_9() throws Exception {
        String sql = "SELECT b+0, BIN(b+0), OCT(b+0), HEX(b+0) FROM t;";

        SQLStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> stmtList = parser.parseStatementList();

        String text = output(stmtList);

        Assert.assertEquals("SELECT b + 0, BIN(b + 0)\n" +
                "\t, OCT(b + 0)\n" +
                "\t, HEX(b + 0)\n" +
                "FROM t;", text);
    }

    private String output(List<SQLStatement> stmtList) {
        return SQLUtils.toSQLString(stmtList, null);
    }
}
