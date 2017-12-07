/*
 * Copyright 1999-2017 Alibaba Group Holding Ltd.
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
package com.alibaba.druid.bvt.sql.oracle.create;

import com.alibaba.druid.sql.OracleTest;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.oracle.parser.OracleStatementParser;
import com.alibaba.druid.sql.dialect.oracle.visitor.OracleSchemaStatVisitor;
import com.alibaba.druid.util.JdbcConstants;

import java.util.List;

public class OracleCreatePackageTest2 extends OracleTest {

    public void test_types() throws Exception {
        String sql = //
                "CREATE OR REPLACE package         PKG_ECC_CAL_TASK_ACCOMPLISH\n" +
                        "AS\n" +
                        "    TYPE CURSOR_CAL_TASK_ACCOMPLISH IS REF CURSOR;\n" +
                        "    PROCEDURE ECC_CAL_TASK_ACCOMPLISH_PROC(\n" +
                        "    p_rc OUT CURSOR_CAL_TASK_ACCOMPLISH,\n" +
                        "    dealerID IN NUMBER,\n" +
                        "    dealerType IN VARCHAR2,\n" +
                        "    profitType IN NUMBER,\n" +
                        "    profitDate IN VARCHAR2);\n" +
                        "end PKG_ECC_CAL_TASK_ACCOMPLISH;";

        OracleStatementParser parser = new OracleStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();
        SQLStatement stmt = statementList.get(0);
        print(statementList);

        assertEquals(1, statementList.size());

        assertEquals("CREATE OR REPLACE PACKAGE PKG_ECC_CAL_TASK_ACCOMPLISH\n" +
                        "\tCURSOR_CAL_TASK_ACCOMPLISH REF CURSOR;\n" +
                        "\tPROCEDURE ECC_CAL_TASK_ACCOMPLISH_PROC (\n" +
                        "\t\tp_rc OUT CURSOR_CAL_TASK_ACCOMPLISH, \n" +
                        "\t\tdealerID IN NUMBER, \n" +
                        "\t\tdealerType IN VARCHAR2, \n" +
                        "\t\tprofitType IN NUMBER, \n" +
                        "\t\tprofitDate IN VARCHAR2\n" +
                        "\t)\n" +
                        "\t;\n" +
                        "END PKG_ECC_CAL_TASK_ACCOMPLISH;",//
                            SQLUtils.toSQLString(stmt, JdbcConstants.ORACLE));

        OracleSchemaStatVisitor visitor = new OracleSchemaStatVisitor();
        stmt.accept(visitor);

        System.out.println("Tables : " + visitor.getTables());
        System.out.println("fields : " + visitor.getColumns());
        System.out.println("coditions : " + visitor.getConditions());
        System.out.println("relationships : " + visitor.getRelationships());
        System.out.println("orderBy : " + visitor.getOrderByColumns());

//        Assert.assertEquals(2, visitor.getTables().size());
//
//        Assert.assertEquals(5, visitor.getColumns().size());
//
//        Assert.assertTrue(visitor.containsColumn("employees", "employee_id"));
//        Assert.assertTrue(visitor.containsColumn("employees", "*"));
//        Assert.assertTrue(visitor.containsColumn("departments", "department_id"));
//        Assert.assertTrue(visitor.containsColumn("employees", "salary"));
//        Assert.assertTrue(visitor.containsColumn("employees", "commission_pct"));
    }
}
