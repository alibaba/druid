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

public class SQLServerSelectTest10 extends TestCase {

    public void test_isEmpty() throws Exception {
        String sql = "SELECT c.FirstName, c.LastName, e.Title, a.AddressLine1, a.City" + //
                     " , sp.Name AS [State/Province], a.PostalCode" + //
                     " INTO dbo.EmployeeAddresses" + //
                     " FROM Person.Contact AS c" + //
                     " JOIN HumanResources.Employee AS e ON e.ContactID = c.ContactID" + //
                     " JOIN HumanResources.EmployeeAddress AS ea ON ea.EmployeeID = e.EmployeeID" + //
                     " JOIN Person.Address AS a on a.AddressID = ea.AddressID" + //
                     " JOIN Person.StateProvince as sp ON sp.StateProvinceID = a.StateProvinceID;";

        String expect = "SELECT c.FirstName, c.LastName, e.Title, a.AddressLine1, a.City" + //
                        "\n\t, sp.Name AS [State/Province], a.PostalCode" + //
                        "\nINTO dbo.EmployeeAddresses" + //
                        "\nFROM Person.Contact c" + //
                        "\n\tJOIN HumanResources.Employee e ON e.ContactID = c.ContactID" + //
                        "\n\tJOIN HumanResources.EmployeeAddress ea ON ea.EmployeeID = e.EmployeeID" + //
                        "\n\tJOIN Person.Address a ON a.AddressID = ea.AddressID" + //
                        "\n\tJOIN Person.StateProvince sp ON sp.StateProvinceID = a.StateProvinceID";

        SQLServerStatementParser parser = new SQLServerStatementParser(sql);
        SQLStatement stmt = parser.parseStatementList().get(0);

        String text = TestUtils.outputSqlServer(stmt);

        Assert.assertEquals(expect, text);

        System.out.println(text);
    }
}
