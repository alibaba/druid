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
package com.alibaba.druid.bvt.sql.db2;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.OracleTest;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.db2.parser.DB2StatementParser;
import com.alibaba.druid.sql.dialect.db2.visitor.DB2SchemaStatVisitor;
import com.alibaba.druid.util.JdbcUtils;
import com.alibaba.druid.util.Utils;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.List;

public class DB2ResourceTest extends OracleTest {

    public void test_0() throws Exception {
        exec_test("bvt/parser/db2-0.txt");
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

        DB2StatementParser parser = new DB2StatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();

        // Assert.assertEquals(1, statementList.size());

        System.out.println(sql);

        print(statementList);

        DB2SchemaStatVisitor visitor = new DB2SchemaStatVisitor();

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
