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
package com.alibaba.druid.bvt.sql.mysql.visitor;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.List;

import org.junit.Assert;
import junit.framework.TestCase;

import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.mysql.parser.MySqlStatementParser;
import com.alibaba.druid.sql.dialect.mysql.visitor.MySqlOutputVisitor;
import com.alibaba.druid.sql.dialect.mysql.visitor.MySqlSchemaStatVisitor;
import com.alibaba.druid.util.Utils;
import com.alibaba.druid.util.JdbcUtils;

public class MySqlResourceTest extends TestCase {

    public void test_0() throws Exception {
//        exec_test("bvt/parser/mysql-0.txt");
//        exec_test("bvt/parser/mysql-1.txt");
//        exec_test("bvt/parser/mysql-2.txt");
//        exec_test("bvt/parser/mysql-3.txt");
//        exec_test("bvt/parser/mysql-4.txt");
//        exec_test("bvt/parser/mysql-5.txt");
//        exec_test("bvt/parser/mysql-6.txt");
//        exec_test("bvt/parser/mysql-7.txt");
//        exec_test("bvt/parser/mysql-8.txt");
//        exec_test("bvt/parser/mysql-9.txt");
//        exec_test("bvt/parser/mysql-10.txt");
//        exec_test("bvt/parser/mysql-11.txt");
//        exec_test("bvt/parser/mysql-12.txt");
//        exec_test("bvt/parser/mysql-13.txt");
        exec_test("bvt/parser/mysql-0.txt");
    }


    public void test_14() throws Exception {
        exec_test("bvt/parser/mysql-16.txt");
    }

    public void test_16() throws Exception {
        exec_test("bvt/parser/mysql-16.txt");
    }

    public void test_17() throws Exception {
        exec_test("bvt/parser/mysql-17.txt");
    }

    public void test_18() throws Exception {
        exec_test("bvt/parser/mysql-18.txt");
    }

    public void test_19() throws Exception {
        exec_test("bvt/parser/mysql-19.txt");
    }

    public void exec_test(String resource) throws Exception {
//        System.out.println(resource);
        InputStream is = null;

        is = Thread.currentThread().getContextClassLoader().getResourceAsStream(resource);
        Reader reader = new InputStreamReader(is, "UTF-8");
        String input = Utils.read(reader);
        JdbcUtils.close(reader);
        String[] items = input.split("---------------------------");
        String sql = items[0].trim();
        String expect = items[1].trim();
        if (expect != null) {
            expect = expect.replaceAll("\\r\\n", "\n");
        }

        MySqlStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();
        SQLStatement stmt = statementList.get(0);

        assertEquals(1, statementList.size());

        MySqlSchemaStatVisitor visitor = new MySqlSchemaStatVisitor();
        stmt.accept(visitor);

//        System.out.println(sql);
//        System.out.println("Tables : " + visitor.getTables());
//        System.out.println("fields : " + visitor.getColumns());
//
//        System.out.println();
//        System.out.println();

        if (expect != null && !expect.isEmpty()) {
            assertEquals(expect, stmt.toString());
        }
    }

    void mergValidate(String sql, String expect) {

        MySqlStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();
        SQLStatement statemen = statementList.get(0);

        Assert.assertEquals(1, statementList.size());

        StringBuilder out = new StringBuilder();
        MySqlOutputVisitor visitor = new MySqlOutputVisitor(out, true);
        statemen.accept(visitor);

        System.out.println(out.toString());

        Assert.assertEquals(expect, out.toString());
    }

    private String output(List<SQLStatement> stmtList) {
        StringBuilder out = new StringBuilder();
        MySqlOutputVisitor visitor = new MySqlOutputVisitor(out);

        for (SQLStatement stmt : stmtList) {
            stmt.accept(visitor);
        }

        return out.toString();
    }
}
