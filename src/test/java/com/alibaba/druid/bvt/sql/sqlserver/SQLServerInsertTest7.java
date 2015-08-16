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

import java.util.List;

import junit.framework.TestCase;

import org.junit.Assert;

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.sqlserver.ast.stmt.SQLServerInsertStatement;
import com.alibaba.druid.sql.dialect.sqlserver.parser.SQLServerStatementParser;
import com.alibaba.druid.sql.dialect.sqlserver.visitor.SQLServerSchemaStatVisitor;

public class SQLServerInsertTest7 extends TestCase {

    public void test_0() throws Exception {
        String sql = "INSERT Production.ScrapReason OUTPUT INSERTED.ScrapReasonID, INSERTED.Name, INSERTED.ModifiedDate INTO @MyTableVar VALUES (N'Operator error', GETDATE());";

        SQLServerStatementParser parser = new SQLServerStatementParser(sql);
        parser.setParseCompleteValues(false);
        parser.setParseValuesSize(3);
        List<SQLStatement> statementList = parser.parseStatementList();
        SQLStatement stmt = statementList.get(0);

        SQLServerInsertStatement insertStmt = (SQLServerInsertStatement) stmt;

        Assert.assertEquals(1, insertStmt.getValuesList().size());
        Assert.assertEquals(2, insertStmt.getValues().getValues().size());
        Assert.assertEquals(0, insertStmt.getColumns().size());
        Assert.assertEquals(1, statementList.size());

        SQLServerSchemaStatVisitor visitor = new SQLServerSchemaStatVisitor();
        stmt.accept(visitor);

        String formatSql = "INSERT INTO Production.ScrapReason"//
                           + "\nOUTPUT INSERTED.ScrapReasonID, INSERTED.Name, INSERTED.ModifiedDate"//
                           + "\n\tINTO @MyTableVar"//
                           + "\nVALUES"//
                           + "\n(N'Operator error', GETDATE())";
        Assert.assertEquals(formatSql, SQLUtils.toSQLServerString(insertStmt));
    }

    public void test_1() throws Exception {
        String sql = "INSERT TOP(5)INTO dbo.EmployeeSales  OUTPUT inserted.EmployeeID, inserted.FirstName, inserted.LastName, inserted.YearlySales SELECT sp.BusinessEntityID, c.LastName, c.FirstName, sp.SalesYTD FROM Sales.SalesPerson AS sp INNER JOIN Person.Person AS c ON sp.BusinessEntityID = c.BusinessEntityID WHERE sp.SalesYTD > 250000.00 ORDER BY sp.SalesYTD DESC;";

        SQLServerStatementParser parser = new SQLServerStatementParser(sql);
        parser.setParseCompleteValues(false);
        parser.setParseValuesSize(3);
        List<SQLStatement> statementList = parser.parseStatementList();
        SQLStatement stmt = statementList.get(0);

        SQLServerInsertStatement insertStmt = (SQLServerInsertStatement) stmt;

        Assert.assertEquals(0, insertStmt.getValuesList().size());
        Assert.assertEquals(0, insertStmt.getColumns().size());
        Assert.assertEquals(1, statementList.size());

        SQLServerSchemaStatVisitor visitor = new SQLServerSchemaStatVisitor();
        stmt.accept(visitor);

        String formatSql = "INSERT TOP (5) INTO dbo.EmployeeSales"//
                           + "\nOUTPUT inserted.EmployeeID, inserted.FirstName, inserted.LastName, inserted.YearlySales"//
                           + "\nSELECT sp.BusinessEntityID, c.LastName, c.FirstName, sp.SalesYTD"//
                           + "\nFROM Sales.SalesPerson sp"//
                           + "\nINNER JOIN Person.Person c ON sp.BusinessEntityID = c.BusinessEntityID"//
                           + "\nWHERE sp.SalesYTD > 250000.00"//
                           + "\nORDER BY sp.SalesYTD DESC";
        Assert.assertEquals(formatSql, SQLUtils.toSQLServerString(insertStmt));
    }

}
