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
 * WITHOUT WARRANTIES OR XONDITIONS OF ANY KIND, either express or implied.
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

public class OracleCreatePackageTest5 extends OracleTest {

    public void test_types() throws Exception {
        String sql = //
                "CREATE OR REPLACE package body         cms_con_attributes_pkg is\n" +
                        "  function get_hc_org_id(in_hc_con_id number) return number is\n" +
                        "           hc_org_id number default null;\n" +
                        "           sqlStr varchar2(500);\n" +
                        "  begin\n" +
                        "    sqlStr :='select  h.org_id as hc_org_id\n" +
                        "               from ecc_cpr.ecc_cpr_hc_con_heahers h\n" +
                        "               where h.hc_con_id =' || to_char(in_hc_con_id);\n" +
                        "    execute immediate sqlStr into hc_org_id;\n" +
                        "    return hc_org_id;\n" +
                        "  end get_hc_org_id;\n" +
                        "begin\n" +
                        "  return;\n" +
                        "end  cms_con_attributes_pkg;";

        System.out.println(sql);

        OracleStatementParser parser = new OracleStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();
        SQLStatement stmt = statementList.get(0);
        print(statementList);

        assertEquals(1, statementList.size());

        assertEquals("CREATE OR REPLACE PACKAGE BODY cms_con_attributes_pkg\n" +
                        "BEGIN\n" +
                        "\tFUNCTION get_hc_org_id (\n" +
                        "\t\tin_hc_con_id number\n" +
                        "\t)\n" +
                        "\tRETURN number\n" +
                        "\tIS\n" +
                        "\thc_org_id number := NULL;\n" +
                        "\t\tsqlStr varchar2(500);\n" +
                        "\tBEGIN\n" +
                        "\t\tsqlStr := 'select  h.org_id as hc_org_id\n" +
                        "               from ecc_cpr.ecc_cpr_hc_con_heahers h\n" +
                        "               where h.hc_con_id =' || to_char(in_hc_con_id);\n" +
                        "\t\tEXECUTE IMMEDIATE sqlStr INTO hc_org_id;\n" +
                        "\t\tRETURN hc_org_id;\n" +
                        "\tEND;\n" +
                        "\tBEGIN\n" +
                        "\t\tRETURN;\n" +
                        "\tEND;\n" +
                        "END cms_con_attributes_pkg;",//
                            SQLUtils.toSQLString(stmt, JdbcConstants.ORACLE));

        OracleSchemaStatVisitor visitor = new OracleSchemaStatVisitor();
        stmt.accept(visitor);

        System.out.println("Tables : " + visitor.getTables());
        System.out.println("fields : " + visitor.getColumns());
        System.out.println("coditions : " + visitor.getConditions());
        System.out.println("relationships : " + visitor.getRelationships());
        System.out.println("orderBy : " + visitor.getOrderByColumns());

        SQLUtils.toPGString(stmt);
        stmt.clone();

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
