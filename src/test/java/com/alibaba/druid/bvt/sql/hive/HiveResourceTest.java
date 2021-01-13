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
package com.alibaba.druid.bvt.sql.hive;

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.hive.parser.HiveStatementParser;
import com.alibaba.druid.sql.dialect.hive.visitor.HiveSchemaStatVisitor;
import com.alibaba.druid.sql.visitor.SchemaStatVisitor;
import com.alibaba.druid.util.JdbcUtils;
import com.alibaba.druid.util.Utils;
import junit.framework.TestCase;
import org.junit.Assert;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.List;

public class HiveResourceTest extends TestCase {

    public void test_0() throws Exception {
        exec_test("bvt/parser/hive-0.txt");
    }

    public void test_1() throws Exception {
        exec_test("bvt/parser/hive-1.txt");
    }

    public void test_2() throws Exception {
        exec_test("bvt/parser/hive-2.txt");
    }

    public void test_3() throws Exception {
        exec_test("bvt/parser/hive-3.txt");
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
        String expect = null;
        
        if (items.length > 1) {
            expect = items[1].trim();
            if (expect != null) {
                expect = expect.replaceAll("\\r\\n", "\n");
            }
        }

        HiveStatementParser parser = new HiveStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();
        SQLStatement stmt = statementList.get(0);

        Assert.assertEquals(1, statementList.size());

        SchemaStatVisitor visitor = new HiveSchemaStatVisitor();
        stmt.accept(visitor);

        if (expect != null && !expect.isEmpty()) {
            assertEquals(expect, stmt.toString());
        }

        System.out.println(sql);
//        System.out.println(stmt.toString());
//        System.out.println("Tables : " + visitor.getTables());
//        System.out.println("fields : " + visitor.getColumns());
//
//        System.out.println();
//        System.out.println("---------------------------");
        System.out.println(SQLUtils.toHiveString(stmt));
    }



}
