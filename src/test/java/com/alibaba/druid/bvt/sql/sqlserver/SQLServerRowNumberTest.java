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
package com.alibaba.druid.bvt.sql.sqlserver;

import org.junit.Assert;
import junit.framework.TestCase;

import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.sqlserver.parser.SQLServerStatementParser;
import com.alibaba.druid.sql.test.TestUtils;

public class SQLServerRowNumberTest extends TestCase {

    public void test_isEmpty() throws Exception {
        String sql = "SELECT c.FirstName, c.LastName" + //
                     "    ,ROW_NUMBER() OVER(ORDER BY SalesYTD DESC) AS 'Row Number'" + //
                     "    ,s.SalesYTD, a.PostalCode " + //
                     "FROM Sales.SalesPerson s " + //
                     "    INNER JOIN Person.Contact c " + //
                     "        ON s.SalesPersonID = c.ContactID" + //
                     "    INNER JOIN Person.Address a " + //
                     "        ON a.AddressID = c.ContactID " + //
                     "WHERE TerritoryID IS NOT NULL " + //
                     "    AND SalesYTD <> 0;";

        String expect = "SELECT c.FirstName, c.LastName, ROW_NUMBER() OVER (ORDER BY SalesYTD DESC) AS 'Row Number', s.SalesYTD, a.PostalCode" //
                        + "\nFROM Sales.SalesPerson s"
                        + "\n\tINNER JOIN Person.Contact c ON s.SalesPersonID = c.ContactID"
                        + "\n\tINNER JOIN Person.Address a ON a.AddressID = c.ContactID" //
                        + "\nWHERE TerritoryID IS NOT NULL" //
                        + "\n\tAND SalesYTD <> 0;";

        SQLServerStatementParser parser = new SQLServerStatementParser(sql);
        SQLStatement stmt = parser.parseStatementList().get(0);

        String text = TestUtils.outputSqlServer(stmt);

        Assert.assertEquals(expect, text);

//        System.out.println(text);
    }
}
