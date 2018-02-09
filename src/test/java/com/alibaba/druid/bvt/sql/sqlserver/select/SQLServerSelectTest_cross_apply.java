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

import java.util.List;

import junit.framework.TestCase;

import org.junit.Assert;

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.statement.SQLSelect;
import com.alibaba.druid.sql.ast.statement.SQLSelectStatement;
import com.alibaba.druid.sql.dialect.sqlserver.ast.SQLServerSelectQueryBlock;
import com.alibaba.druid.sql.dialect.sqlserver.parser.SQLServerStatementParser;
import com.alibaba.druid.sql.dialect.sqlserver.visitor.SQLServerSchemaStatVisitor;
import com.alibaba.druid.util.JdbcUtils;

public class SQLServerSelectTest_cross_apply extends TestCase {

    public void test_0() throws Exception {
        String sql = "SELECT DeptID, DeptName, DeptMgrID, EmpID, EmpLastName, EmpSalary FROM Departments d CROSS APPLY dbo.GetReports(d.DeptMgrID)";

        SQLServerStatementParser parser = new SQLServerStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();
        SQLStatement stmt = statementList.get(0);

        SQLSelectStatement selectStmt = (SQLSelectStatement) stmt;

        SQLSelect select = selectStmt.getSelect();
        Assert.assertNotNull(select.getQuery());
        SQLServerSelectQueryBlock queryBlock = (SQLServerSelectQueryBlock) select.getQuery();
        Assert.assertNull(queryBlock.getGroupBy());

        String fomatSQL = SQLUtils.toSQLString(statementList, JdbcUtils.SQL_SERVER);

//        System.out.println(fomatSQL);

        Assert.assertEquals(1, statementList.size());

        SQLServerSchemaStatVisitor visitor = new SQLServerSchemaStatVisitor();
        stmt.accept(visitor);

//        System.out.println("Tables : " + visitor.getTables());
//        System.out.println("fields : " + visitor.getColumns());
//        System.out.println("coditions : " + visitor.getConditions());
//        System.out.println("orderBy : " + visitor.getOrderByColumns());

        Assert.assertEquals(1, visitor.getTables().size());
        Assert.assertEquals(7, visitor.getColumns().size());
        Assert.assertEquals(0, visitor.getConditions().size());
        Assert.assertEquals(0, visitor.getOrderByColumns().size());

        String expected = "SELECT DeptID, DeptName, DeptMgrID, EmpID, EmpLastName"//
                          + "\n\t, EmpSalary"//
                          + "\nFROM Departments d" //
                          + "\n\tCROSS APPLY dbo.GetReports(d.DeptMgrID)";

        Assert.assertEquals(expected, fomatSQL);
    }
}
