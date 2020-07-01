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
package com.alibaba.druid.bvt.sql.oracle.select;

import com.alibaba.druid.sql.OracleTest;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.oracle.parser.OracleStatementParser;
import com.alibaba.druid.sql.dialect.oracle.visitor.OracleSchemaStatVisitor;
import org.junit.Assert;

import java.util.List;

public class OracleSelectTest77 extends OracleTest {

    public void test_0() throws Exception {
        String sql = //
                "SELECT\n" +
                        "    a.contract_header_id,\n" +
                        "    a.contract_number,\n" +
                        "    a.contract_name,\n" +
                        "    b.field_value contract_amount,\n" +
                        "    c.payment_rate,\n" +
                        "    d.id customer_id,\n" +
                        "    d.name customer_name,\n" +
                        "    a.salesrep_id\n" +
                        "FROM (\n" +
                        "    tcc_cpr.tcc_cpr_contract_headers a\n" +
                        "    LEFT JOIN tcc_cpr.virtual_value2004 b ON\n" +
                        "        a.contract_header_id = b.contract_header_id\n" +
                        "    AND\n" +
                        "        b.template_id =\n" +
                        "            CASE\n" +
                        "                WHEN a.contract_category = 'SALES'         THEN 1\n" +
                        "                WHEN a.contract_category = 'INTERNATIONAL' THEN 49\n" +
                        "            END\n" +
                        "    AND\n" +
                        "        b.enabled_flag = 'Y'\n" +
                        "    LEFT JOIN tcc_cpr.tcc_cpr_payment c ON\n" +
                        "        a.contract_header_id = c.contract_header_id\n" +
                        "    AND\n" +
                        "        c.payment_condition_code = 'ZTE_PAYMENT_YUFU'\n" +
                        "    AND\n" +
                        "        c.enabled_flag = 'Y'\n" +
                        ")\n" +
                        "    LEFT JOIN tcc_cust.tcc_cust_customer d ON (\n" +
                        "            a.customer_id = d.id\n" +
                        "        AND (\n" +
                        "                d.enable_flag = 'Y'\n" +
                        "            OR\n" +
                        "                d.enable_flag = 'T'\n" +
                        "        )\n" +
                        "    )\n" +
                        "WHERE\n" +
                        "    a.enabled_flag = 'Y'"; //

        OracleStatementParser parser = new OracleStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();
        SQLStatement stmt = statementList.get(0);
        print(statementList);

        Assert.assertEquals(1, statementList.size());

        OracleSchemaStatVisitor visitor = new OracleSchemaStatVisitor();
        stmt.accept(visitor);

        System.out.println("Tables : " + visitor.getTables());
        System.out.println("fields : " + visitor.getColumns());
        System.out.println("coditions : " + visitor.getConditions());
        System.out.println("relationships : " + visitor.getRelationships());
        System.out.println("orderBy : " + visitor.getOrderByColumns());

        assertEquals(4, visitor.getTables().size());
        assertEquals(18, visitor.getColumns().size());
        assertEquals(12, visitor.getConditions().size());
        assertEquals(3, visitor.getRelationships().size());
        assertEquals(0, visitor.getOrderByColumns().size());

        {
            String text = SQLUtils.toOracleString(stmt);

            assertEquals("SELECT a.contract_header_id, a.contract_number, a.contract_name, b.field_value AS contract_amount, c.payment_rate\n" +
                    "\t, d.id AS customer_id, d.name AS customer_name, a.salesrep_id\n" +
                    "FROM tcc_cpr.tcc_cpr_contract_headers a\n" +
                    "LEFT JOIN tcc_cpr.virtual_value2004 b ON a.contract_header_id = b.contract_header_id\n" +
                    "AND b.template_id = CASE \n" +
                    "\tWHEN a.contract_category = 'SALES' THEN 1\n" +
                    "\tWHEN a.contract_category = 'INTERNATIONAL' THEN 49\n" +
                    "END\n" +
                    "AND b.enabled_flag = 'Y' \n" +
                    "LEFT JOIN tcc_cpr.tcc_cpr_payment c ON a.contract_header_id = c.contract_header_id\n" +
                    "AND c.payment_condition_code = 'ZTE_PAYMENT_YUFU'\n" +
                    "AND c.enabled_flag = 'Y' \n" +
                    "\tLEFT JOIN tcc_cust.tcc_cust_customer d ON a.customer_id = d.id\n" +
                    "AND (d.enable_flag = 'Y'\n" +
                    "\tOR d.enable_flag = 'T') \n" +
                    "WHERE a.enabled_flag = 'Y'", text);
        }
        // Assert.assertTrue(visitor.getColumns().contains(new TableStat.Column("acduser.vw_acd_info", "xzqh")));

        // Assert.assertTrue(visitor.getOrderByColumns().contains(new TableStat.Column("employees", "last_name")));
    }
}
