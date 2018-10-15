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
package com.alibaba.druid.bvt.sql.sqlserver.select;

import junit.framework.TestCase;

import org.junit.Assert;

import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.sqlserver.parser.SQLServerStatementParser;
import com.alibaba.druid.sql.test.TestUtils;

public class SQLServerSelectTest12 extends TestCase {

    public void test_simple() throws Exception {
        String sql = "SELECT Row, Name " + //
                     "FROM(" + //
                     "SELECT ROW_NUMBER() OVER (ORDER BY ProductID) AS Row, Name " + //
                     "FROM Product " + //
                     ") AS ProductsWithRowNumbers " + //
                     "WHERE Row >= 6 AND Row <= 10";

        String expect = "SELECT Row, Name\n" +
                "FROM (\n" +
                "\tSELECT ROW_NUMBER() OVER (ORDER BY ProductID) AS Row, Name\n" +
                "\tFROM Product\n" +
                ") ProductsWithRowNumbers\n" +
                "WHERE Row >= 6\n" +
                "\tAND Row <= 10";

        SQLServerStatementParser parser = new SQLServerStatementParser(sql);
        SQLStatement stmt = parser.parseStatementList().get(0);

        String text = TestUtils.outputSqlServer(stmt);

        Assert.assertEquals(expect, text);

//        System.out.println(text);
    }
}
