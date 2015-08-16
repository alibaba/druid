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

public class MathematicalFunctionsTest extends TestCase {

    public void test_0() throws Exception {
        String sql = "SELECT ACOS(1);";

        SQLStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> stmtList = parser.parseStatementList();

        String text = output(stmtList);

        Assert.assertEquals("SELECT ACOS(1);", text);
    }

    public void test_1() throws Exception {
        String sql = "SELECT ABS(2);";

        SQLStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> stmtList = parser.parseStatementList();

        String text = output(stmtList);

        Assert.assertEquals("SELECT ABS(2);", text);
    }

    public void test_2() throws Exception {
        String sql = "SELECT ABS(-32);";

        SQLStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> stmtList = parser.parseStatementList();

        String text = output(stmtList);

        Assert.assertEquals("SELECT ABS(-32);", text);
    }

    public void test_3() throws Exception {
        String sql = "SELECT ACOS(1.0001);";

        SQLStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> stmtList = parser.parseStatementList();

        String text = output(stmtList);

        Assert.assertEquals("SELECT ACOS(1.0001);", text);
    }

    public void test_4() throws Exception {
        String sql = "SELECT ACOS(0);";

        SQLStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> stmtList = parser.parseStatementList();

        String text = output(stmtList);

        Assert.assertEquals("SELECT ACOS(0);", text);
    }

    public void test_5() throws Exception {
        String sql = "SELECT ASIN(0.2);";

        SQLStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> stmtList = parser.parseStatementList();

        String text = output(stmtList);

        Assert.assertEquals("SELECT ASIN(0.2);", text);
    }

    public void test_6() throws Exception {
        String sql = "SELECT ASIN('foo');";

        SQLStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> stmtList = parser.parseStatementList();

        String text = output(stmtList);

        Assert.assertEquals("SELECT ASIN('foo');", text);
    }

    public void test_7() throws Exception {
        String sql = "SELECT ATAN(2);";

        SQLStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> stmtList = parser.parseStatementList();

        String text = output(stmtList);

        Assert.assertEquals("SELECT ATAN(2);", text);
    }

    public void test_8() throws Exception {
        String sql = "SELECT ATAN(-2);";

        SQLStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> stmtList = parser.parseStatementList();

        String text = output(stmtList);

        Assert.assertEquals("SELECT ATAN(-2);", text);
    }

    public void test_9() throws Exception {
        String sql = "SELECT ATAN(-2,2);";

        SQLStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> stmtList = parser.parseStatementList();

        String text = output(stmtList);

        Assert.assertEquals("SELECT ATAN(-2, 2);", text);
    }

    public void test_10() throws Exception {
        String sql = "SELECT ATAN2(PI(),0);";

        SQLStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> stmtList = parser.parseStatementList();

        String text = output(stmtList);

        Assert.assertEquals("SELECT ATAN2(PI(), 0);", text);
    }

    public void test_11() throws Exception {
        String sql = "SELECT CEILING(1.23);";

        SQLStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> stmtList = parser.parseStatementList();

        String text = output(stmtList);

        Assert.assertEquals("SELECT CEILING(1.23);", text);
    }

    public void test_12() throws Exception {
        String sql = "SELECT CEILING(-1.23);";

        SQLStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> stmtList = parser.parseStatementList();

        String text = output(stmtList);

        Assert.assertEquals("SELECT CEILING(-1.23);", text);
    }

    public void test_13() throws Exception {
        String sql = "SELECT CONV('a',16,2);";

        SQLStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> stmtList = parser.parseStatementList();

        String text = output(stmtList);

        Assert.assertEquals("SELECT CONV('a', 16, 2);", text);
    }

    public void test_14() throws Exception {
        String sql = "SELECT CONV(10+'10'+'10'+0xa,10,10);";

        SQLStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> stmtList = parser.parseStatementList();

        String text = output(stmtList);

        Assert.assertEquals("SELECT CONV(10 + '10' + '10' + 0xa, 10, 10);", text);
    }

    public void test_15() throws Exception {
        String sql = "SELECT COS(PI())";

        SQLStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> stmtList = parser.parseStatementList();

        String text = output(stmtList);

        Assert.assertEquals("SELECT COS(PI());", text);
    }

    public void test_16() throws Exception {
        String sql = "SELECT CRC32('MySQL')";

        SQLStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> stmtList = parser.parseStatementList();

        String text = output(stmtList);

        Assert.assertEquals("SELECT CRC32('MySQL');", text);
    }

    public void test_17() throws Exception {
        String sql = "SELECT DEGREES(PI())";

        SQLStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> stmtList = parser.parseStatementList();

        String text = output(stmtList);

        Assert.assertEquals("SELECT DEGREES(PI());", text);
    }

    public void test_18() throws Exception {
        String sql = "SELECT DEGREES(PI() / 2)";

        SQLStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> stmtList = parser.parseStatementList();

        String text = output(stmtList);

        Assert.assertEquals("SELECT DEGREES(PI() / 2);", text);
    }

    public void test_19() throws Exception {
        String sql = "SELECT MOD(29,9)";

        SQLStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> stmtList = parser.parseStatementList();

        String text = output(stmtList);

        Assert.assertEquals("SELECT MOD(29, 9);", text);
    }

    public void test_20() throws Exception {
        String sql = "SELECT 29 MOD 9";

        SQLStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> stmtList = parser.parseStatementList();

        String text = output(stmtList);

        Assert.assertEquals("SELECT 29 % 9;", text);
    }

    public void test_21() throws Exception {
        String sql = "SELECT PI()+0.000000000000000000";

        SQLStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> stmtList = parser.parseStatementList();

        String text = output(stmtList);

        Assert.assertEquals("SELECT PI() + 0E-18;", text);
    }

    public void test_22() throws Exception {
        String sql = "SELECT POW(2,-2)";

        SQLStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> stmtList = parser.parseStatementList();

        String text = output(stmtList);

        Assert.assertEquals("SELECT POW(2, -2);", text);
    }

    public void test_23() throws Exception {
        String sql = "SELECT RADIANS(90)";

        SQLStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> stmtList = parser.parseStatementList();

        String text = output(stmtList);

        Assert.assertEquals("SELECT RADIANS(90);", text);
    }

    public void test_24() throws Exception {
        String sql = "SELECT i, RAND() FROM t";

        SQLStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> stmtList = parser.parseStatementList();

        String text = output(stmtList);

        Assert.assertEquals("SELECT i, RAND()\nFROM t;", text);
    }

    public void test_25() throws Exception {
        String sql = "SELECT * FROM tbl_name ORDER BY RAND()";

        SQLStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> stmtList = parser.parseStatementList();

        String text = output(stmtList);

        Assert.assertEquals("SELECT *\nFROM tbl_name\nORDER BY RAND();", text);
    }

    public void test_26() throws Exception {
        String sql = "SELECT ROUND(2.5), ROUND(25E-1)";

        SQLStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> stmtList = parser.parseStatementList();

        String text = output(stmtList);

        Assert.assertEquals("SELECT ROUND(2.5), ROUND(2.5);", text);
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
