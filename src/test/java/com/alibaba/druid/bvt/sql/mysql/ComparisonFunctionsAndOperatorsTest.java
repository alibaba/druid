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
import com.alibaba.druid.util.JdbcConstants;
import org.junit.Assert;
import junit.framework.TestCase;

import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.mysql.parser.MySqlStatementParser;
import com.alibaba.druid.sql.dialect.mysql.visitor.MySqlOutputVisitor;
import com.alibaba.druid.sql.parser.SQLStatementParser;

public class ComparisonFunctionsAndOperatorsTest extends TestCase {

    public void test_0() throws Exception {
        String sql = "SELECT 1 = 0;";

        SQLStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> stmtList = parser.parseStatementList();

        String text = output(stmtList);

        Assert.assertEquals("SELECT 1 = 0;", text);
    }

    public void test_1() throws Exception {
        String sql = "SELECT '0' = 0;";

        SQLStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> stmtList = parser.parseStatementList();

        String text = output(stmtList);

        Assert.assertEquals("SELECT '0' = 0;", text);
    }

    public void test_2() throws Exception {
        String sql = "SELECT 1 <=> 1, NULL <=> NULL, 1 <=> NULL;";

        SQLStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> stmtList = parser.parseStatementList();

        String text = output(stmtList);

        Assert.assertEquals("SELECT 1 <=> 1, NULL <=> NULL\n" +
                "\t, 1 <=> NULL;", text);
    }

    public void test_3() throws Exception {
        String sql = "SELECT 1 = 1, NULL = NULL, 1 = NULL;";

        SQLStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> stmtList = parser.parseStatementList();

        String text = output(stmtList);

        Assert.assertEquals("SELECT 1 = 1, NULL = NULL\n" +
                "\t, 1 = NULL;", text);
    }

    public void test_4() throws Exception {
        String sql = "SELECT '.01' <> '0.01';";

        SQLStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> stmtList = parser.parseStatementList();

        String text = output(stmtList);

        Assert.assertEquals("SELECT '.01' <> '0.01';", text);
    }

    public void test_5() throws Exception {
        String sql = "SELECT 0.1 <= 2";

        SQLStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> stmtList = parser.parseStatementList();

        String text = output(stmtList);

        Assert.assertEquals("SELECT 0.1 <= 2", text);
    }

    public void test_6() throws Exception {
        String sql = "SELECT 2 < 2";

        SQLStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> stmtList = parser.parseStatementList();

        String text = output(stmtList);

        Assert.assertEquals("SELECT 2 < 2", text);
    }

    public void test_7() throws Exception {
        String sql = "SELECT 2 >= 2";

        SQLStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> stmtList = parser.parseStatementList();

        String text = output(stmtList);

        Assert.assertEquals("SELECT 2 >= 2", text);
    }

    public void test_8() throws Exception {
        String sql = "SELECT 2 > 2";

        SQLStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> stmtList = parser.parseStatementList();

        String text = output(stmtList);

        Assert.assertEquals("SELECT 2 > 2", text);
    }

    public void test_9() throws Exception {
        String sql = "SELECT 1 IS TRUE, 0 IS FALSE, NULL IS UNKNOWN";

        SQLStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> stmtList = parser.parseStatementList();

        String text = output(stmtList);

        assertEquals("SELECT 1 IS true, 0 IS false\n" +
                "\t, NULL IS UNKNOWN", text);
    }

    public void test_10() throws Exception {
        String sql = "SELECT 1 IS NOT UNKNOWN, 0 IS NOT UNKNOWN, NULL IS NOT UNKNOWN";

        SQLStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> stmtList = parser.parseStatementList();

        String text = output(stmtList);

        Assert.assertEquals("SELECT 1 IS NOT UNKNOWN, 0 IS NOT UNKNOWN\n" +
                "\t, NULL IS NOT UNKNOWN", text);
    }

    public void test_11() throws Exception {
        String sql = "SELECT 1 IS NULL, 0 IS NULL, NULL IS NULL";

        SQLStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> stmtList = parser.parseStatementList();

        String text = output(stmtList);

        Assert.assertEquals("SELECT 1 IS NULL, 0 IS NULL\n" +
                "\t, NULL IS NULL", text);
    }

    public void test_12() throws Exception {
        String sql = "SELECT * FROM tbl_name WHERE auto_col IS NULL";

        SQLStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> stmtList = parser.parseStatementList();

        String text = output(stmtList);

        Assert.assertEquals("SELECT *\nFROM tbl_name\nWHERE auto_col IS NULL", text);
    }

    public void test_13() throws Exception {
        String sql = "SELECT * FROM tbl_name WHERE date_column IS NULL";

        SQLStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> stmtList = parser.parseStatementList();

        String text = output(stmtList);

        Assert.assertEquals("SELECT *\nFROM tbl_name\nWHERE date_column IS NULL", text);
    }

    public void test_14() throws Exception {
        String sql = "SELECT 2 BETWEEN 1 AND 3, 2 BETWEEN 3 and 1";

        SQLStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> stmtList = parser.parseStatementList();

        String text = output(stmtList);

        Assert.assertEquals("SELECT 2 BETWEEN 1 AND 3, 2 BETWEEN 3 AND 1", text);
    }

    public void test_15() throws Exception {
        String sql = "SELECT 1 BETWEEN 2 AND 3;";

        SQLStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> stmtList = parser.parseStatementList();

        String text = output(stmtList);

        assertEquals("SELECT 1 BETWEEN 2 AND 3;", text);
    }

    public void test_16() throws Exception {
        String sql = "SELECT COALESCE(NULL,1);";

        SQLStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> stmtList = parser.parseStatementList();

        String text = output(stmtList);

        Assert.assertEquals("SELECT COALESCE(NULL, 1);", text);
    }

    public void test_17() throws Exception {
        String sql = "SELECT GREATEST(2,0);";

        SQLStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> stmtList = parser.parseStatementList();

        String text = output(stmtList);

        Assert.assertEquals("SELECT GREATEST(2, 0);", text);
    }

    public void test_18() throws Exception {
        String sql = "SELECT GREATEST(34.0,3.0,5.0,767.0);";

        SQLStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> stmtList = parser.parseStatementList();

        String text = output(stmtList);

        Assert.assertEquals("SELECT GREATEST(34.0, 3.0, 5.0, 767.0);", text);
    }

    public void test_19() throws Exception {
        String sql = "SELECT GREATEST('B', 'A', 'C');";

        SQLStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> stmtList = parser.parseStatementList();

        String text = output(stmtList);

        Assert.assertEquals("SELECT GREATEST('B', 'A', 'C');", text);
    }

    public void test_20() throws Exception {
        String sql = "SELECT 2 IN (0,3,5,7);";

        SQLStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> stmtList = parser.parseStatementList();

        String text = output(stmtList);

        Assert.assertEquals("SELECT 2 IN (0, 3, 5, 7);", text);
    }

    public void test_21() throws Exception {
        String sql = "SELECT 'wefwf' IN ('wee','wefwf','weg');";

        SQLStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> stmtList = parser.parseStatementList();

        String text = output(stmtList);

        Assert.assertEquals("SELECT 'wefwf' IN ('wee', 'wefwf', 'weg');", text);
    }

    public void test_22() throws Exception {
        String sql = "SELECT val1 FROM tbl1 WHERE val1 IN ('1','2','a');";

        SQLStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> stmtList = parser.parseStatementList();

        String text = output(stmtList);

        Assert.assertEquals("SELECT val1\nFROM tbl1\nWHERE val1 IN ('1', '2', 'a');", text);
    }

    public void test_23() throws Exception {
        String sql = "SELECT ISNULL(1+1);";

        SQLStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> stmtList = parser.parseStatementList();

        String text = output(stmtList);

        Assert.assertEquals("SELECT ISNULL(1 + 1);", text);
    }

    public void test_24() throws Exception {
        String sql = "SELECT INTERVAL(23, 1, 15, 17, 30, 44, 200);";

        SQLStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> stmtList = parser.parseStatementList();

        String text = output(stmtList);

        Assert.assertEquals("SELECT INTERVAL(23, 1, 15, 17, 30, 44, 200);", text);
    }

    public void test_25() throws Exception {
        String sql = "SELECT LEAST(34.0,3.0,5.0,767.0);";

        SQLStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> stmtList = parser.parseStatementList();

        String text = output(stmtList);

        Assert.assertEquals("SELECT LEAST(34.0, 3.0, 5.0, 767.0);", text);
    }

    public void test_26() throws Exception {
        String sql = "SELECT CAST(LEAST(3600, 9223372036854775808.0) as SIGNED);";

        SQLStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> stmtList = parser.parseStatementList();

        String text = output(stmtList);

        Assert.assertEquals("SELECT CAST(LEAST(3600, 9223372036854775808.0) AS SIGNED);", text);
    }

    private String output(List<SQLStatement> stmtList) {
        return SQLUtils.toSQLString(stmtList, JdbcConstants.ORACLE);
    }
}
