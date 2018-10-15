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
package com.alibaba.druid.bvt.sql.oracle.create;

import com.alibaba.druid.sql.OracleTest;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.oracle.parser.OracleStatementParser;
import com.alibaba.druid.sql.dialect.oracle.visitor.OracleSchemaStatVisitor;
import com.alibaba.druid.util.JdbcConstants;

import java.util.List;

public class OracleCreatePackageTest3 extends OracleTest {

    public void test_types() throws Exception {
        String sql = //
                "\n" +
                        "CREATE OR REPLACE PACKAGE         Mail_fck_bpi AS\n" +
                        "-- --------------------------------------------------------------------------\n" +
                        "-- Name         : http://www.oracle-base.com/dba/miscellaneous/soap_api\n" +
                        "-- Author       : DR Timothy S Hall\n" +
                        "-- Description  : SOAP related functions for consuming web services.\n" +
                        "-- Ammedments   :\n" +
                        "--   When         Who       What\n" +
                        "--   ===========  ========  =================================================\n" +
                        "--   04-OCT-2003  Tim Hall  Initial Creation\n" +
                        "--   23-FEB-2006  Tim Hall  Parameterized the \"soap\" envelope tags.\n" +
                        "-- --------------------------------------------------------------------------\n" +
                        "TYPE t_request IS RECORD (\n" +
                        "  method        VARCHAR2(256),\n" +
                        "  namespace     VARCHAR2(256),\n" +
                        "  body          VARCHAR2(32767),\n" +
                        "  envelope_tag  VARCHAR2(30)\n" +
                        ");\n" +
                        "TYPE t_response IS RECORD\n" +
                        "(\n" +
                        "  doc           XMLTYPE,\n" +
                        "  envelope_tag  VARCHAR2(30)\n" +
                        ");\n" +
                        "PROCEDURE set_proxy_authentication(p_username  IN  VARCHAR2,\n" +
                        "\t\t\t\t   p_password  IN  VARCHAR2);\n" +
                        "FUNCTION new_request(p_method        IN  VARCHAR2,\n" +
                        "\t\t     p_namespace     IN  VARCHAR2,\n" +
                        "\t\t     p_envelope_tag  IN  VARCHAR2 DEFAULT 'SOAP-ENV')\n" +
                        "  RETURN t_request;\n" +
                        "PROCEDURE add_parameter(p_request  IN OUT NOCOPY  t_request,\n" +
                        "\t\t\tp_name     IN             VARCHAR2,\n" +
                        "\t\t\tp_type     IN             VARCHAR2,\n" +
                        "\t\t\tp_value    IN             VARCHAR2);\n" +
                        "FUNCTION invoke(p_request  IN OUT NOCOPY  t_request,\n" +
                        "\t\tp_url      IN             VARCHAR2,\n" +
                        "\t\tp_action   IN             VARCHAR2)\n" +
                        "  RETURN t_response;\n" +
                        "FUNCTION get_return_value(p_response   IN OUT NOCOPY  t_response,\n" +
                        "\t\t\t  p_name       IN             VARCHAR2,\n" +
                        "\t\t\t  p_namespace  IN             VARCHAR2)\n" +
                        "  RETURN VARCHAR2;\n" +
                        "END Mail_fck_bpi;";

        OracleStatementParser parser = new OracleStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();
        SQLStatement stmt = statementList.get(0);
        print(statementList);

        assertEquals(1, statementList.size());

        assertEquals("CREATE OR REPLACE PACKAGE Mail_fck_bpi\n" +
                        "\tTYPE t_request IS RECORD (\n" +
                        "\t\tmethod VARCHAR2(256), \n" +
                        "\t\tnamespace VARCHAR2(256), \n" +
                        "\t\tbody VARCHAR2(32767), \n" +
                        "\t\tenvelope_tag VARCHAR2(30)\n" +
                        "\t);\n" +
                        "\tTYPE t_response IS RECORD (\n" +
                        "\t\tdoc XMLTYPE, \n" +
                        "\t\tenvelope_tag VARCHAR2(30)\n" +
                        "\t);\n" +
                        "\tPROCEDURE set_proxy_authentication (\n" +
                        "\t\tp_username IN VARCHAR2, \n" +
                        "\t\tp_password IN VARCHAR2\n" +
                        "\t)\n" +
                        "\t;\n" +
                        "\tFUNCTION new_request (\n" +
                        "\t\tp_method IN VARCHAR2, \n" +
                        "\t\tp_namespace IN VARCHAR2, \n" +
                        "\t\tp_envelope_tag IN VARCHAR2 := 'SOAP-ENV'\n" +
                        "\t)\n" +
                        "\tRETURN t_request\n" +
                        "\t\n" +
                        "\tPROCEDURE add_parameter (\n" +
                        "\t\tp_request IN OUT NOCOPY t_request, \n" +
                        "\t\tp_name IN VARCHAR2, \n" +
                        "\t\tp_type IN VARCHAR2, \n" +
                        "\t\tp_value IN VARCHAR2\n" +
                        "\t)\n" +
                        "\t;\n" +
                        "\tFUNCTION invoke (\n" +
                        "\t\tp_request IN OUT NOCOPY t_request, \n" +
                        "\t\tp_url IN VARCHAR2, \n" +
                        "\t\tp_action IN VARCHAR2\n" +
                        "\t)\n" +
                        "\tRETURN t_response\n" +
                        "\t\n" +
                        "\tFUNCTION get_return_value (\n" +
                        "\t\tp_response IN OUT NOCOPY t_response, \n" +
                        "\t\tp_name IN VARCHAR2, \n" +
                        "\t\tp_namespace IN VARCHAR2\n" +
                        "\t)\n" +
                        "\tRETURN VARCHAR2\n" +
                        "\t\n" +
                        "END Mail_fck_bpi;",//
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
