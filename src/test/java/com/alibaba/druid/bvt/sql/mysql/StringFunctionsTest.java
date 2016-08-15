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

public class StringFunctionsTest extends TestCase {

    public void test_0() throws Exception {
        String sql = "SELECT ASCII('2');";

        SQLStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> stmtList = parser.parseStatementList();

        String text = output(stmtList);

        Assert.assertEquals("SELECT ASCII('2');", text);
    }

    public void test_1() throws Exception {
        String sql = "SELECT ASCII(2);";

        SQLStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> stmtList = parser.parseStatementList();

        String text = output(stmtList);

        Assert.assertEquals("SELECT ASCII(2);", text);
    }

    public void test_2() throws Exception {
        String sql = "SELECT BIN(12);";

        SQLStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> stmtList = parser.parseStatementList();

        String text = output(stmtList);

        Assert.assertEquals("SELECT BIN(12);", text);
    }

    public void test_3() throws Exception {
        String sql = "SELECT BIT_LENGTH('text');";

        SQLStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> stmtList = parser.parseStatementList();

        String text = output(stmtList);

        Assert.assertEquals("SELECT BIT_LENGTH('text');", text);
    }

    public void test_4() throws Exception {
        String sql = "SELECT CHAR(77,121,83,81,'76');";

        SQLStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> stmtList = parser.parseStatementList();

        String text = output(stmtList);

        Assert.assertEquals("SELECT CHAR(77, 121, 83, 81, '76');", text);
    }

    public void test_5() throws Exception {
        String sql = "SELECT HEX(CHAR(1,0)), HEX(CHAR(256));";

        SQLStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> stmtList = parser.parseStatementList();

        String text = output(stmtList);

        Assert.assertEquals("SELECT HEX(CHAR(1, 0)), HEX(CHAR(256));", text);
    }

    public void test_6() throws Exception {
        String sql = "SELECT HEX(CHAR(1,0,0)), HEX(CHAR(256*256));";

        SQLStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> stmtList = parser.parseStatementList();

        String text = output(stmtList);

        Assert.assertEquals("SELECT HEX(CHAR(1, 0, 0)), HEX(CHAR(256 * 256));", text);
    }

    public void test_7() throws Exception {
        String sql = "SELECT CHARSET(CHAR(0x65)), CHARSET(CHAR(0x65 USING utf8))";

        SQLStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> stmtList = parser.parseStatementList();

        String text = output(stmtList);

        Assert.assertEquals("SELECT CHARSET(CHAR(0x65)), CHARSET(CHAR(0x65 USING utf8));", text);
    }

    public void test_8() throws Exception {
        String sql = "SELECT CONCAT(CAST(int_col AS CHAR), char_col)";

        SQLStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> stmtList = parser.parseStatementList();

        String text = output(stmtList);

        Assert.assertEquals("SELECT CONCAT(CAST(int_col AS CHAR), char_col);", text);
    }

    public void test_9() throws Exception {
        String sql = "SELECT 'My' 'S' 'QL'";

        SQLStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> stmtList = parser.parseStatementList();

        String text = output(stmtList);

        Assert.assertEquals("SELECT CONCAT('My', 'S', 'QL');", text);
    }

    public void test_10() throws Exception {
        String sql = "SELECT CONCAT_WS(',','First name','Second name','Last Name');";

        SQLStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> stmtList = parser.parseStatementList();

        String text = output(stmtList);

        Assert.assertEquals("SELECT CONCAT_WS(',', 'First name', 'Second name', 'Last Name');", text);
    }

    public void test_11() throws Exception {
        String sql = "SELECT CONCAT_WS(',','First name',NULL,'Last Name');";

        SQLStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> stmtList = parser.parseStatementList();

        String text = output(stmtList);

        Assert.assertEquals("SELECT CONCAT_WS(',', 'First name', NULL, 'Last Name');", text);
    }

    public void test_12() throws Exception {
        String sql = "SELECT ELT(1, 'ej', 'Heja', 'hej', 'foo');";

        SQLStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> stmtList = parser.parseStatementList();

        String text = output(stmtList);

        Assert.assertEquals("SELECT ELT(1, 'ej', 'Heja', 'hej', 'foo');", text);
    }

    public void test_13() throws Exception {
        String sql = "SELECT ELT(4, 'ej', 'Heja', 'hej', 'foo');";

        SQLStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> stmtList = parser.parseStatementList();

        String text = output(stmtList);

        Assert.assertEquals("SELECT ELT(4, 'ej', 'Heja', 'hej', 'foo');", text);
    }

    public void test_14() throws Exception {
        String sql = "SELECT EXPORT_SET(5,'Y','N',',',4);";

        SQLStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> stmtList = parser.parseStatementList();

        String text = output(stmtList);

        Assert.assertEquals("SELECT EXPORT_SET(5, 'Y', 'N', ',', 4);", text);
    }

    public void test_15() throws Exception {
        String sql = "SELECT EXPORT_SET(6,'1','0',',',10);";

        SQLStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> stmtList = parser.parseStatementList();

        String text = output(stmtList);

        Assert.assertEquals("SELECT EXPORT_SET(6, '1', '0', ',', 10);", text);
    }

    public void test_16() throws Exception {
        String sql = "SELECT FIELD('ej', 'Hej', 'ej', 'Heja', 'hej', 'foo');";

        SQLStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> stmtList = parser.parseStatementList();

        String text = output(stmtList);

        Assert.assertEquals("SELECT FIELD('ej', 'Hej', 'ej', 'Heja', 'hej', 'foo');", text);
    }

    public void test_17() throws Exception {
        String sql = "SELECT FIND_IN_SET('b','a,b,c,d');";

        SQLStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> stmtList = parser.parseStatementList();

        String text = output(stmtList);

        Assert.assertEquals("SELECT FIND_IN_SET('b', 'a,b,c,d');", text);
    }

    public void test_18() throws Exception {
        String sql = "SELECT FORMAT(12332.123456, 4);";

        SQLStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> stmtList = parser.parseStatementList();

        String text = output(stmtList);

        Assert.assertEquals("SELECT FORMAT(12332.123456, 4);", text);
    }

    public void test_19() throws Exception {
        String sql = "SELECT 0x616263, HEX('abc'), UNHEX(HEX('abc'));";

        SQLStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> stmtList = parser.parseStatementList();

        String text = output(stmtList);

        Assert.assertEquals("SELECT 0x616263, HEX('abc'), UNHEX(HEX('abc'));", text);
    }

    public void test_20() throws Exception {
        String sql = "SELECT HEX(255), CONV(HEX(255),16,10);";

        SQLStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> stmtList = parser.parseStatementList();

        String text = output(stmtList);

        Assert.assertEquals("SELECT HEX(255), CONV(HEX(255), 16, 10);", text);
    }

    public void test_21() throws Exception {
        String sql = "SELECT INSERT('Quadratic', 3, 4, 'What');";

        SQLStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> stmtList = parser.parseStatementList();

        String text = output(stmtList);

        Assert.assertEquals("SELECT INSERT('Quadratic', 3, 4, 'What');", text);
    }

    public void test_22() throws Exception {
        String sql = "SELECT INSERT('Quadratic', -1, 4, 'What');";

        SQLStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> stmtList = parser.parseStatementList();

        String text = output(stmtList);

        Assert.assertEquals("SELECT INSERT('Quadratic', -1, 4, 'What');", text);
    }

    public void test_23() throws Exception {
        String sql = "SELECT INSTR('foobarbar', 'bar');";

        SQLStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> stmtList = parser.parseStatementList();

        String text = output(stmtList);

        Assert.assertEquals("SELECT INSTR('foobarbar', 'bar');", text);
    }

    public void test_24() throws Exception {
        String sql = "SELECT LEFT('foobarbar', 5);";

        SQLStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> stmtList = parser.parseStatementList();

        String text = output(stmtList);

        Assert.assertEquals("SELECT LEFT('foobarbar', 5);", text);
    }

    public void test_25() throws Exception {
        String sql = "SELECT LENGTH('text');";

        SQLStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> stmtList = parser.parseStatementList();

        String text = output(stmtList);

        Assert.assertEquals("SELECT LENGTH('text');", text);
    }

    public void test_26() throws Exception {
        String sql = "UPDATE t SET blob_col=LOAD_FILE('/tmp/picture') WHERE id=1;";

        SQLStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> stmtList = parser.parseStatementList();

        String text = output(stmtList);

        Assert.assertEquals("UPDATE t\nSET blob_col = LOAD_FILE('/tmp/picture')\nWHERE id = 1;", text);
    }

    public void test_27() throws Exception {
        String sql = "SELECT LOCATE('bar', 'foobarbar');";

        SQLStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> stmtList = parser.parseStatementList();

        String text = output(stmtList);

        Assert.assertEquals("SELECT LOCATE('bar', 'foobarbar');", text);
    }

    public void test_28() throws Exception {
        String sql = "SELECT LOWER('QUADRATICALLY');";

        SQLStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> stmtList = parser.parseStatementList();

        String text = output(stmtList);

        Assert.assertEquals("SELECT LOWER('QUADRATICALLY');", text);
    }

    public void test_29() throws Exception {
        String sql = "SELECT LPAD('hi',4,'??');";

        SQLStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> stmtList = parser.parseStatementList();

        String text = output(stmtList);

        Assert.assertEquals("SELECT LPAD('hi', 4, '??');", text);
    }

    public void test_30() throws Exception {
        String sql = "SELECT LTRIM('  barbar')";

        SQLStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> stmtList = parser.parseStatementList();

        String text = output(stmtList);

        Assert.assertEquals("SELECT LTRIM('  barbar');", text);
    }

    public void test_31() throws Exception {
        String sql = "SELECT MAKE_SET(1,'a','b','c')";

        SQLStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> stmtList = parser.parseStatementList();

        String text = output(stmtList);

        Assert.assertEquals("SELECT MAKE_SET(1, 'a', 'b', 'c');", text);
    }

    public void test_32() throws Exception {
        String sql = "SELECT MAKE_SET(1 | 4,'hello','nice','world')";

        SQLStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> stmtList = parser.parseStatementList();

        String text = output(stmtList);

        Assert.assertEquals("SELECT MAKE_SET(1 | 4, 'hello', 'nice', 'world');", text);
    }

    public void test_33() throws Exception {
        String sql = "SELECT MAKE_SET(1 | 4,'hello','nice',NULL,'world')";

        SQLStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> stmtList = parser.parseStatementList();

        String text = output(stmtList);

        Assert.assertEquals("SELECT MAKE_SET(1 | 4, 'hello', 'nice', NULL, 'world');", text);
    }

    public void test_34() throws Exception {
        String sql = "SELECT ORD('2')";

        SQLStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> stmtList = parser.parseStatementList();

        String text = output(stmtList);

        Assert.assertEquals("SELECT ORD('2');", text);
    }

    public void test_35() throws Exception {
        String sql = "SELECT QUOTE('Don\\'t!')";

        SQLStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> stmtList = parser.parseStatementList();

        String text = output(stmtList);

        Assert.assertEquals("SELECT QUOTE('Don''t!');", text);
    }

    public void test_36() throws Exception {
        String sql = "SELECT REPEAT('MySQL', 3)";

        SQLStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> stmtList = parser.parseStatementList();

        String text = output(stmtList);

        Assert.assertEquals("SELECT REPEAT('MySQL', 3);", text);
    }

    public void test_37() throws Exception {
        String sql = "SELECT REVERSE('abc')";

        SQLStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> stmtList = parser.parseStatementList();

        String text = output(stmtList);

        Assert.assertEquals("SELECT REVERSE('abc');", text);
    }

    public void test_38() throws Exception {
        String sql = "SELECT RIGHT('foobarbar', 4)";

        SQLStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> stmtList = parser.parseStatementList();

        String text = output(stmtList);

        Assert.assertEquals("SELECT RIGHT('foobarbar', 4);", text);
    }

    public void test_39() throws Exception {
        String sql = "SELECT RPAD('hi',5,'?')";

        SQLStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> stmtList = parser.parseStatementList();

        String text = output(stmtList);

        Assert.assertEquals("SELECT RPAD('hi', 5, '?');", text);
    }

    public void test_40() throws Exception {
        String sql = "SELECT RTRIM('barbar   ')";

        SQLStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> stmtList = parser.parseStatementList();

        String text = output(stmtList);

        Assert.assertEquals("SELECT RTRIM('barbar   ');", text);
    }

    public void test_41() throws Exception {
        String sql = "SELECT SOUNDEX('Hello')";

        SQLStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> stmtList = parser.parseStatementList();

        String text = output(stmtList);

        Assert.assertEquals("SELECT SOUNDEX('Hello');", text);
    }

    public void test_42() throws Exception {
        String sql = "SELECT SPACE(6)";

        SQLStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> stmtList = parser.parseStatementList();

        String text = output(stmtList);

        Assert.assertEquals("SELECT SPACE(6);", text);
    }

    public void test_43() throws Exception {
        String sql = "SELECT SUBSTRING('Quadratically',5)";

        SQLStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> stmtList = parser.parseStatementList();

        String text = output(stmtList);

        Assert.assertEquals("SELECT SUBSTRING('Quadratically', 5);", text);
    }

    public void test_44() throws Exception {
        String sql = "SELECT SUBSTRING('Sakila' FROM -4 FOR 2)";

        SQLStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> stmtList = parser.parseStatementList();

        String text = output(stmtList);

        Assert.assertEquals("SELECT SUBSTRING('Sakila', -4, 2);", text);
    }

    public void test_45() throws Exception {
        String sql = "SELECT SUBSTRING('foobarbar' FROM 4)";

        SQLStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> stmtList = parser.parseStatementList();

        String text = output(stmtList);

        Assert.assertEquals("SELECT SUBSTRING('foobarbar', 4);", text);
    }

    public void test_46() throws Exception {
        String sql = "SELECT SUBSTRING('Quadratically',5,6)";

        SQLStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> stmtList = parser.parseStatementList();

        String text = output(stmtList);

        Assert.assertEquals("SELECT SUBSTRING('Quadratically', 5, 6);", text);
    }

    public void test_47() throws Exception {
        String sql = "SELECT SUBSTRING_INDEX('www.mysql.com', '.', -2)";

        SQLStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> stmtList = parser.parseStatementList();

        String text = output(stmtList);

        Assert.assertEquals("SELECT SUBSTRING_INDEX('www.mysql.com', '.', -2);", text);
    }

    public void test_48() throws Exception {
        String sql = "SELECT TRIM('  bar   ')";

        SQLStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> stmtList = parser.parseStatementList();

        String text = output(stmtList);

        Assert.assertEquals("SELECT TRIM('  bar   ');", text);
    }

    public void test_49() throws Exception {
        String sql = "SELECT TRIM(LEADING 'x' FROM 'xxxbarxxx')";

        SQLStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> stmtList = parser.parseStatementList();

        String text = output(stmtList);

        Assert.assertEquals("SELECT TRIM(LEADING 'x' FROM 'xxxbarxxx');", text);
    }

    public void test_50() throws Exception {
        String sql = "SELECT TRIM(BOTH 'x' FROM 'xxxbarxxx');";

        SQLStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> stmtList = parser.parseStatementList();

        String text = output(stmtList);

        Assert.assertEquals("SELECT TRIM(BOTH 'x' FROM 'xxxbarxxx');", text);
    }

    public void test_51() throws Exception {
        String sql = "SELECT TRIM(TRAILING 'xyz' FROM 'barxxyz');";

        SQLStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> stmtList = parser.parseStatementList();

        String text = output(stmtList);

        Assert.assertEquals("SELECT TRIM(TRAILING 'xyz' FROM 'barxxyz');", text);
    }

    public void test_52() throws Exception {
        String sql = "SELECT UNHEX('4D7953514C');";

        SQLStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> stmtList = parser.parseStatementList();

        String text = output(stmtList);

        Assert.assertEquals("SELECT UNHEX('4D7953514C');", text);
    }

    public void test_53() throws Exception {
        String sql = "SELECT 0x4D7953514C;";

        SQLStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> stmtList = parser.parseStatementList();

        String text = output(stmtList);

        Assert.assertEquals("SELECT 0x4D7953514C;", text);
    }

    public void test_54() throws Exception {
        String sql = "SELECT UPPER('Hej');";

        SQLStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> stmtList = parser.parseStatementList();

        String text = output(stmtList);

        Assert.assertEquals("SELECT UPPER('Hej');", text);
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
