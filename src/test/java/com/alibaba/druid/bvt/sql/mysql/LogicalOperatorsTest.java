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

public class LogicalOperatorsTest extends TestCase {

    public void test_0() throws Exception {
        String sql = "SELECT 10 IS TRUE;";

        SQLStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> stmtList = parser.parseStatementList();

        String text = output(stmtList);

        Assert.assertEquals("SELECT 10 IS true;", text);
    }

    public void test_1() throws Exception {
        String sql = "SELECT -10 IS TRUE;";

        SQLStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> stmtList = parser.parseStatementList();

        String text = output(stmtList);

        Assert.assertEquals("SELECT -10 IS true;", text);
    }

    public void test_2() throws Exception {
        String sql = "SELECT 'string' IS NOT NULL;";

        SQLStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> stmtList = parser.parseStatementList();

        String text = output(stmtList);

        Assert.assertEquals("SELECT 'string' IS NOT NULL;", text);
    }

    public void test_3() throws Exception {
        String sql = "SELECT NOT 10;";

        SQLStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> stmtList = parser.parseStatementList();

        String text = output(stmtList);

        Assert.assertEquals("SELECT NOT 10;", text);
    }

    public void test_4() throws Exception {
        String sql = "SELECT NOT 0;";

        SQLStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> stmtList = parser.parseStatementList();

        String text = output(stmtList);

        Assert.assertEquals("SELECT NOT 0;", text);
    }

    public void test_5() throws Exception {
        String sql = "SELECT NOT NULL;";

        SQLStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> stmtList = parser.parseStatementList();

        String text = output(stmtList);

        Assert.assertEquals("SELECT NOT NULL;", text);
    }

    public void test_6() throws Exception {
        String sql = "SELECT ! (1+1);";

        SQLStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> stmtList = parser.parseStatementList();

        String text = output(stmtList);

        Assert.assertEquals("SELECT !(1 + 1);", text);
    }

    public void test_7() throws Exception {
        String sql = "SELECT ! 1+1;";

        SQLStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> stmtList = parser.parseStatementList();

        String text = output(stmtList);

        Assert.assertEquals("SELECT !1 + 1;", text);
    }

    public void test_8() throws Exception {
        String sql = "SELECT 1 && 1;";

        SQLStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> stmtList = parser.parseStatementList();

        String text = output(stmtList);

        Assert.assertEquals("SELECT 1\n\tAND 1;", text);
    }

    public void test_9() throws Exception {
        String sql = "SELECT 1 AND NULL;";

        SQLStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> stmtList = parser.parseStatementList();

        String text = output(stmtList);

        Assert.assertEquals("SELECT 1\n\tAND NULL;", text);
    }

    public void test_10() throws Exception {
        String sql = "SELECT 0 OR NULL;";

        SQLStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> stmtList = parser.parseStatementList();

        String text = output(stmtList);

        Assert.assertEquals("SELECT 0\n\tOR NULL;", text);
    }

    public void test_11() throws Exception {
        String sql = "SELECT 0 || NULL;";

        SQLStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> stmtList = parser.parseStatementList();

        String text = output(stmtList);

        Assert.assertEquals("SELECT 0\n\tOR NULL;", text);
    }

    public void test_12() throws Exception {
        String sql = "SELECT 0 XOR NULL;";

        SQLStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> stmtList = parser.parseStatementList();

        String text = output(stmtList);

        Assert.assertEquals("SELECT 0 XOR NULL;", text);
    }

    public void test_13() throws Exception {
        String sql = "SELECT 1 XOR 1 XOR 1;";

        SQLStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> stmtList = parser.parseStatementList();

        String text = output(stmtList);

        Assert.assertEquals("SELECT 1 XOR 1 XOR 1;", text);
    }



    public void test14(){
        String sql = "SELECT ~1;";

        SQLStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> stmtList = parser.parseStatementList();

        String text = output(stmtList);

        Assert.assertEquals("SELECT ~1;", text);


        sql = "SELECT ~(1+1);";

        parser = new MySqlStatementParser(sql);
        stmtList = parser.parseStatementList();

        text = output(stmtList);

        Assert.assertEquals("SELECT ~(1 + 1);", text);
    }

    public void test15(){
        String sql = "SELECT * FROM SUNTEST WHERE ~ID = 1;";

        SQLStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> stmtList = parser.parseStatementList();

        String text = SQLUtils.toSQLString(stmtList, JdbcConstants.MYSQL);
        Assert.assertEquals("SELECT *\nFROM SUNTEST\nWHERE ~ID = 1;", text);


        sql = "SELECT * FROM SUNTEST WHERE ~(ID = 1);";

        parser = new MySqlStatementParser(sql);
        stmtList = parser.parseStatementList();

        text = SQLUtils.toSQLString(stmtList, JdbcConstants.MYSQL);
        Assert.assertEquals("SELECT *\nFROM SUNTEST\nWHERE ~(ID = 1);", text);
    }
    private String output(List<SQLStatement> stmtList) {
        return SQLUtils.toSQLString(stmtList, JdbcConstants.MYSQL);
    }
}
