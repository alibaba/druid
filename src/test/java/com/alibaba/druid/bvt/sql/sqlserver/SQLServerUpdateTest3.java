/*
 * Copyright 1999-2101 Alibaba Group Holding Ltd.
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
package com.alibaba.druid.bvt.sql.sqlserver;

import junit.framework.TestCase;

import org.junit.Assert;

import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.sqlserver.parser.SQLServerStatementParser;
import com.alibaba.druid.sql.test.TestUtils;

public class SQLServerUpdateTest3 extends TestCase {

    public void test_update() throws Exception {
        String sql = "UPDATE Sales.SalesPerson " + //
                     "SET SalesYTD = SalesYTD + " + //
                     "    (SELECT SUM(so.SubTotal) " + //
                     "     FROM Sales.SalesOrderHeader AS so" + //
                     "     WHERE so.OrderDate = (SELECT MAX(OrderDate)" + //
                     "                           FROM Sales.SalesOrderHeader AS so2" + //
                     "                           WHERE so2.SalesPersonID = " + //
                     "                                 so.SalesPersonID)" + //
                     "     AND Sales.SalesPerson.SalesPersonID = so.SalesPersonID" + //
                     "     GROUP BY so.SalesPersonID);";

        String expect = "UPDATE Sales.SalesPerson" +
        		"\nSET SalesYTD = SalesYTD + (" +
        		"\n\tSELECT SUM(so.SubTotal)" +
        		"\n\tFROM Sales.SalesOrderHeader so" +
        		"\n\tWHERE so.OrderDate = (" +
        		"\n\t\t\tSELECT MAX(OrderDate)" +
        		"\n\t\t\tFROM Sales.SalesOrderHeader so2" +
        		"\n\t\t\tWHERE so2.SalesPersonID = so.SalesPersonID" +
        		"\n\t\t\t)" +
        		"\n\t\tAND Sales.SalesPerson.SalesPersonID = so.SalesPersonID" +
        		"\n\tGROUP BY so.SalesPersonID" +
        		"\n\t)";

        SQLServerStatementParser parser = new SQLServerStatementParser(sql);
        SQLStatement stmt = parser.parseStatementList().get(0);

        String text = TestUtils.outputSqlServer(stmt);

        Assert.assertEquals(expect, text);

        System.out.println(text);
    }
}
