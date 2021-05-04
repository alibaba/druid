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
package com.alibaba.druid.bvt.sql.oracle.visitor;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.List;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.dialect.mysql.visitor.MySqlOutputVisitor;
import org.junit.Assert;

import com.alibaba.druid.sql.OracleTest;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.mysql.parser.MySqlStatementParser;
import com.alibaba.druid.sql.dialect.oracle.parser.OracleStatementParser;
import com.alibaba.druid.sql.dialect.oracle.visitor.OracleSchemaStatVisitor;
import com.alibaba.druid.util.Utils;
import com.alibaba.druid.util.JdbcUtils;

public class OracleResourceTest extends OracleTest {

    public void test_0_9() throws Exception {
        for (int i = 0; i <= 9; ++i) { // 57
             exec_test("bvt/parser/oracle-" + i + ".txt");
            System.out.println();
        }
    }

    public void test_10_19() throws Exception {
        for (int i = 10; i <= 19; ++i) { // 57
            exec_test("bvt/parser/oracle-" + i + ".txt");
            System.out.println();
        }
    }

    public void test_20_29() throws Exception {
        for (int i = 20; i <= 29; ++i) { // 57
            exec_test("bvt/parser/oracle-" + i + ".txt");
            System.out.println();
        }
    }

    public void test_30_39() throws Exception {
        for (int i = 30; i <= 39; ++i) { // 57
            exec_test("bvt/parser/oracle-" + i + ".txt");
            System.out.println();
        }
    }

    public void test_40_49() throws Exception {
        for (int i = 40; i <= 49; ++i) { // 57
            exec_test("bvt/parser/oracle-" + i + ".txt");
            System.out.println();
        }
    }

    public void test_50_57() throws Exception {
        for (int i = 50; i <= 57; ++i) { // 57
            exec_test("bvt/parser/oracle-" + i + ".txt");
            System.out.println();
        }
    }

    public void test_58() throws Exception {
        exec_test("bvt/parser/oracle-58.txt");
    }

    public void test_59() throws Exception {
        exec_test("bvt/parser/oracle-59.txt");
    }

    public void test_60() throws Exception {
        exec_test("bvt/parser/oracle-60.txt");
    }

    public void test_61() throws Exception {
        // exec_test("bvt/parser/oracle-61.txt");
    }

    public void test_62() throws Exception {
         exec_test("bvt/parser/oracle-62.txt");
    }

    public void test_63() throws Exception {
         exec_test("bvt/parser/oracle-63.txt");
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
        String expect = null;
        if (items.length > 1) {
            expect = items[1];
            expect = expect.trim();
            expect = expect.replaceAll("\\r\\n", "\n");
        }

        OracleStatementParser parser = new OracleStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();

        // Assert.assertEquals(1, statementList.size());

        System.out.println(sql);

        print(statementList);

        OracleSchemaStatVisitor visitor = new OracleSchemaStatVisitor();

        for (int i = 0, size = statementList.size(); i < size; ++i) {
            SQLStatement statement = statementList.get(i);
            statement.accept(visitor);
        }

        System.out.println("Tables : " + visitor.getTables());
        System.out.println("fields : " + visitor.getColumns());

        System.out.println();
        System.out.println();

        if (statementList.size() == 1) {
            SQLStatement stmt = statementList.get(0);
            if (expect != null && !expect.isEmpty()) {
                String actual = stmt.toString();
                assertEquals(expect, actual.trim());
            }
        } else {
            if (expect != null && !expect.isEmpty()) {
                assertEquals(expect, SQLUtils.toSQLString(statementList, DbType.oracle));
            }
        }
    }

}
