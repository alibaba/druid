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
package com.alibaba.druid.bvt.sql.clickhouse;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.OracleTest;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.mysql.parser.MySqlStatementParser;
import com.alibaba.druid.sql.dialect.mysql.visitor.MySqlOutputVisitor;
import com.alibaba.druid.sql.dialect.oracle.parser.OracleStatementParser;
import com.alibaba.druid.sql.dialect.oracle.visitor.OracleSchemaStatVisitor;
import com.alibaba.druid.sql.parser.SQLParserUtils;
import com.alibaba.druid.sql.visitor.SchemaStatVisitor;
import com.alibaba.druid.util.JdbcUtils;
import com.alibaba.druid.util.Utils;
import org.junit.Assert;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.List;

public class ClickHouseResourceTest extends OracleTest {

    public void test_0() throws Exception {
        exec_test("bvt/parser/clickhouse-0.txt");
    }

    public void test_1() throws Exception {
        exec_test("bvt/parser/clickhouse-1.txt");
    }

    public void test_2() throws Exception {
        exec_test("bvt/parser/clickhouse-2.txt");
    }

    public void test_3() throws Exception {
        exec_test("bvt/parser/clickhouse-3.txt");
    }
//
//    public void test_4() throws Exception {
//        exec_test("bvt/parser/clickhouse-4.txt");
//    }

    public void test_5() throws Exception {
        exec_test("bvt/parser/clickhouse-5.txt");
    }

    public void test_6() throws Exception {
        exec_test("bvt/parser/clickhouse-6.txt");
    }

    public void test_7() throws Exception {
        exec_test("bvt/parser/clickhouse-7.txt");
    }

    public void test_8() throws Exception {
        exec_test("bvt/parser/clickhouse-8.txt");
    }

    public void test_9() throws Exception {
        exec_test("bvt/parser/clickhouse-9.txt");
    }

    public void test_10() throws Exception {
        exec_test("bvt/parser/clickhouse-10.txt");
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
        String expect = items.length > 1 ? items[1].trim() : null;

        List<SQLStatement> statementList = SQLUtils.parseStatements(sql, DbType.clickhouse);

        assertEquals(1, statementList.size());

        SQLStatement stmt = statementList.get(0);
        System.out.println(stmt.toString());

        if (expect != null) {
            assertEquals(expect, stmt.toString());
        }

        SchemaStatVisitor visitor = SQLUtils.createSchemaStatVisitor(DbType.clickhouse);

        for (int i = 0, size = statementList.size(); i < size; ++i) {
            SQLStatement statement = statementList.get(i);
            statement.accept(visitor);
        }

        System.out.println("Tables : " + visitor.getTables());
        System.out.println("fields : " + visitor.getColumns());

        System.out.println();
        System.out.println();
    }

}
