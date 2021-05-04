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
package com.alibaba.druid.bvt.sql.postgresql;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.List;

import org.junit.Assert;

import com.alibaba.druid.sql.PGTest;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.postgresql.parser.PGSQLStatementParser;
import com.alibaba.druid.sql.dialect.postgresql.visitor.PGOutputVisitor;
import com.alibaba.druid.sql.dialect.postgresql.visitor.PGSchemaStatVisitor;
import com.alibaba.druid.util.Utils;
import com.alibaba.druid.util.JdbcUtils;

public class PostgresqlResourceTest extends PGTest {

    public void test_0() throws Exception {
        // 13
        exec_test("bvt/parser/postgresql-0.txt");
        // for (int i = 0; i <= 53; ++i) {
        // exec_test("bvt/parser/oracle-" + i + ".txt");
        // }
    }

    public void test_1() throws Exception {
        exec_test("bvt/parser/postgresql-1.txt");
    }

    public void test_2() throws Exception {
        exec_test("bvt/parser/postgresql-2.txt");
    }

    public void test_3() throws Exception {
        exec_test("bvt/parser/postgresql-3.txt");
    }

    public void test_4() throws Exception {
        exec_test("bvt/parser/postgresql-4.txt");
    }

    public void test_5() throws Exception {
        exec_test("bvt/parser/postgresql-5.txt");
    }

    public void test_6() throws Exception {
        exec_test("bvt/parser/postgresql-6.txt");
    }

    public void test_7() throws Exception {
        exec_test("bvt/parser/postgresql-7.txt");
    }

    public void test_8() throws Exception {
        exec_test("bvt/parser/postgresql-8.txt");
    }

    public void test_9() throws Exception {
        exec_test("bvt/parser/postgresql-9.txt");
    }

    public void test_10() throws Exception {
        exec_test("bvt/parser/postgresql-10.txt");
    }

    public void test_11() throws Exception {
        exec_test("bvt/parser/postgresql-11.txt");
    }

    public void test_12() throws Exception {
        exec_test("bvt/parser/postgresql-12.txt");
    }

    public void test_13() throws Exception {
        exec_test("bvt/parser/postgresql-13.txt");
    }

    public void exec_test(String resource) throws Exception {
        System.out.println(resource);
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

        PGSQLStatementParser parser = new PGSQLStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();
        SQLStatement stmt = statementList.get(0);
        print(statementList);

        Assert.assertEquals(1, statementList.size());

        PGSchemaStatVisitor visitor = new PGSchemaStatVisitor();
        stmt.accept(visitor);

        System.out.println(sql);
        System.out.println("Tables : " + visitor.getTables());
        System.out.println("fields : " + visitor.getColumns());
        System.out.println("coditions : " + visitor.getConditions());

        System.out.println();
        System.out.println();

        if (expect != null && !expect.isEmpty()) {
            assertEquals(expect, stmt.toString());
        }
    }

    void mergValidate(String sql, String expect) {
    	PGSQLStatementParser parser = new PGSQLStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();
        SQLStatement statemen = statementList.get(0);

        Assert.assertEquals(1, statementList.size());

        StringBuilder out = new StringBuilder();
        PGOutputVisitor visitor = new PGOutputVisitor(out);
        statemen.accept(visitor);

        System.out.println(out.toString());

        Assert.assertEquals(expect, out.toString());
    }


}
