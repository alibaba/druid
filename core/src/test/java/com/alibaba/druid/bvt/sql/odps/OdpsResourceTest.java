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
package com.alibaba.druid.bvt.sql.odps;

import java.util.List;

import com.alibaba.druid.DbType;
import com.alibaba.druid.TestUtil;
import com.alibaba.druid.bvt.sql.SQLResourceTest;
import com.alibaba.druid.sql.parser.SQLParserFeature;
import com.alibaba.druid.sql.visitor.VisitorFeature;
import junit.framework.TestCase;

import org.junit.Assert;

import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.odps.parser.OdpsStatementParser;
import com.alibaba.druid.sql.dialect.odps.visitor.OdpsSchemaStatVisitor;
import com.alibaba.druid.sql.visitor.SchemaStatVisitor;
import org.junit.Test;

public class OdpsResourceTest extends SQLResourceTest {
    public OdpsResourceTest() {
        super(DbType.odps);
    }

    @Test
    public void test_0() throws Exception {
        exec_test("bvt/parser/odps-0.txt");
    }

    @Test
    public void test_9() throws Exception {
        exec_test("bvt/parser/odps-9.txt");
    }

    @Test
    public void test_10() throws Exception {
        exec_test("bvt/parser/odps-10.txt");
    }

    @Test
    public void test_11() throws Exception {
        exec_test("bvt/parser/odps-11.txt");
    }

    @Test
    public void test_12() throws Exception {
        exec_test("bvt/parser/odps-12.txt");
    }
//
//    public void test_13() throws Exception {
//        exec_test("bvt/parser/odps-13.txt");
//    }

    @Test
    public void test_14() throws Exception {
        exec_test("bvt/parser/odps-14.txt");
    }

    @Test
    public void test_15() throws Exception {
        exec_test("bvt/parser/odps-15.txt");
    }

    @Test
    public void test_16() throws Exception {
        exec_test("bvt/parser/odps-16.txt");
    }

    @Test
    public void test_17() throws Exception {
        exec_test("bvt/parser/odps-17.txt");
    }

    @Test
    public void test_18() throws Exception {
        exec_test("bvt/parser/odps-18.txt");
    }

    @Test
    public void test_19() throws Exception {
        exec_test("bvt/parser/odps-19.txt");
    }

    @Test
    public void test_20() throws Exception {
        exec_test("bvt/parser/odps-20.txt");
    }

    @Test
    public void test_21() throws Exception {
        exec_test("bvt/parser/odps-21.txt");
    }

    @Test
    public void test_22() throws Exception {
        exec_test("bvt/parser/odps-22.txt");
    }

    @Test
    public void odps_parse() throws Exception {
        fileTest(0, 999, i -> "bvt/parser/odps/" + i + ".txt");
    }

    public void exec_test(String resource) throws Exception {
        String input = TestUtil.getResource(resource);
        String[] items = input.split("---------------------------");
        String sql = items[0].trim().replaceAll("\\r\\n", "\n");
        String expect = null;

        if (items.length > 1) {
            expect = items[1].trim().replaceAll("\\r\\n", "\n");
        }

        OdpsStatementParser parser = new OdpsStatementParser(sql, SQLParserFeature.KeepComments);
        List<SQLStatement> statementList = parser.parseStatementList();
        SQLStatement stmt = statementList.get(0);

        Assert.assertEquals(1, statementList.size());

        SchemaStatVisitor visitor = new OdpsSchemaStatVisitor();
        stmt.accept(visitor);

        if (expect != null) {
            String result = stmt.toString(VisitorFeature.OutputPrettyFormat);
            Assert.assertEquals(expect, result);
        }
    }
}
