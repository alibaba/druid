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

import java.util.List;

public class OracleCreateTypeTest2 extends OracleTest {

    public void test_0() throws Exception {
        String sql = //
                "CREATE TYPE customer_typ_demo AS OBJECT\n" +
                        "    ( customer_id        NUMBER(6)\n" +
                        "    , cust_first_name    VARCHAR2(20)\n" +
                        "    , cust_last_name     VARCHAR2(20)\n" +
                        "    , cust_address       CUST_ADDRESS_TYP\n" +
                        "    , phone_numbers      PHONE_LIST_TYP\n" +
                        "    , nls_language       VARCHAR2(3)\n" +
                        "    , nls_territory      VARCHAR2(30)\n" +
                        "    , credit_limit       NUMBER(9,2)\n" +
                        "    , cust_email         VARCHAR2(30)\n" +
                        "    , cust_orders        ORDER_LIST_TYP\n" +
                        "    ) ;"; //

        OracleStatementParser parser = new OracleStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();
        SQLStatement stmt = statementList.get(0);
        print(statementList);

        assertEquals(1, statementList.size());

        OracleSchemaStatVisitor visitor = new OracleSchemaStatVisitor();
        stmt.accept(visitor);

//        System.out.println("Tables : " + visitor.getTables());
//        System.out.println("fields : " + visitor.getColumns());
//        System.out.println("coditions : " + visitor.getConditions());
//        System.out.println("relationships : " + visitor.getRelationships());
//        System.out.println("orderBy : " + visitor.getOrderByColumns());

        assertEquals(0, visitor.getTables().size());

        assertEquals(0, visitor.getColumns().size());

        {
            String text = SQLUtils.toOracleString(stmt);

            assertEquals("CREATE TYPE customer_typ_demo AS OBJECT (\n" +
                    "\tcustomer_id NUMBER(6), \n" +
                    "\tcust_first_name VARCHAR2(20), \n" +
                    "\tcust_last_name VARCHAR2(20), \n" +
                    "\tcust_address CUST_ADDRESS_TYP, \n" +
                    "\tphone_numbers PHONE_LIST_TYP, \n" +
                    "\tnls_language VARCHAR2(3), \n" +
                    "\tnls_territory VARCHAR2(30), \n" +
                    "\tcredit_limit NUMBER(9, 2), \n" +
                    "\tcust_email VARCHAR2(30), \n" +
                    "\tcust_orders ORDER_LIST_TYP\n" +
                    ");", text);
        }

        {
            String text = SQLUtils.toOracleString(stmt, SQLUtils.DEFAULT_LCASE_FORMAT_OPTION);

            assertEquals("create type customer_typ_demo AS OBJECT (\n" +
                    "\tcustomer_id NUMBER(6), \n" +
                    "\tcust_first_name VARCHAR2(20), \n" +
                    "\tcust_last_name VARCHAR2(20), \n" +
                    "\tcust_address CUST_ADDRESS_TYP, \n" +
                    "\tphone_numbers PHONE_LIST_TYP, \n" +
                    "\tnls_language VARCHAR2(3), \n" +
                    "\tnls_territory VARCHAR2(30), \n" +
                    "\tcredit_limit NUMBER(9, 2), \n" +
                    "\tcust_email VARCHAR2(30), \n" +
                    "\tcust_orders ORDER_LIST_TYP\n" +
                    ");", text);
        }
        // assertTrue(visitor.getColumns().contains(new TableStat.Column("acduser.vw_acd_info", "xzqh")));

        // assertTrue(visitor.getOrderByColumns().contains(new TableStat.Column("employees", "last_name")));
    }
}
