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
package com.alibaba.druid.bvt.sql.oracle.select;

import com.alibaba.druid.sql.OracleTest;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.oracle.parser.OracleStatementParser;
import com.alibaba.druid.sql.dialect.oracle.visitor.OracleSchemaStatVisitor;
import org.junit.Assert;

import java.util.List;

public class OracleSelectTest81_join_brace extends OracleTest {

    public void test_0() throws Exception {
        String sql = //
                "select * \n" +
                        "FROM ((tcp_cpr.con_config c1 INNER JOIN tcp_cpr.con_osg3a_headers osg ON\n" +
                        "        c1.contract_config_id = osg.parent_id) LEFT OUTER JOIN\n" +
                        "        erp_zte.zte_kx_osg3_items kx ON osg.osg_item_id = kx.osg_item_id),\n" +
                        "       tcp_cpr.con_sites sites,\n" +
                        "       tcp_cpr.system_bom b4\n" +
                        " WHERE sites.site_id = c1.site_id\n" +
                        "   AND b4.system_bom_id = c1.system_bom_id\n" +
                        "   AND c1.layer = 4\n" +
                        "   AND sites.site_id IN (71242000)\n" +
                        "   AND osg.quantity > 0\n" +
                        "   AND c1.enabled_flag = 'Y'\n" +
                        "   AND c1.contract_header_id = :headerid\n" +
                        "   AND sites.contract_header_id = :headerid\n" +
                        "   AND c1.partition_date = :partiondate;"; //

        System.out.println(sql);

        OracleStatementParser parser = new OracleStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();
        SQLStatement stmt = statementList.get(0);
        print(statementList);



        Assert.assertEquals(1, statementList.size());

        OracleSchemaStatVisitor visitor = new OracleSchemaStatVisitor();
        stmt.accept(visitor);

        {
            String text = SQLUtils.toOracleString(stmt);

            assertEquals("SELECT *\n" +
                    "FROM tcp_cpr.con_config c1\n" +
                    "INNER JOIN tcp_cpr.con_osg3a_headers osg ON c1.contract_config_id = osg.parent_id \n" +
                    "LEFT JOIN erp_zte.zte_kx_osg3_items kx ON osg.osg_item_id = kx.osg_item_id , tcp_cpr.con_sites sites, tcp_cpr.system_bom b4\n" +
                    "WHERE sites.site_id = c1.site_id\n" +
                    "\tAND b4.system_bom_id = c1.system_bom_id\n" +
                    "\tAND c1.layer = 4\n" +
                    "\tAND sites.site_id IN (71242000)\n" +
                    "\tAND osg.quantity > 0\n" +
                    "\tAND c1.enabled_flag = 'Y'\n" +
                    "\tAND c1.contract_header_id = :headerid\n" +
                    "\tAND sites.contract_header_id = :headerid\n" +
                    "\tAND c1.partition_date = :partiondate;", text);
        }

        System.out.println("Tables : " + visitor.getTables());
        System.out.println("fields : " + visitor.getColumns());
        System.out.println("coditions : " + visitor.getConditions());
        System.out.println("relationships : " + visitor.getRelationships());
        System.out.println("orderBy : " + visitor.getOrderByColumns());

        assertEquals(5, visitor.getTables().size());
        assertEquals(19, visitor.getColumns().size());
        assertEquals(15, visitor.getConditions().size());
        assertEquals(4, visitor.getRelationships().size());
        assertEquals(0, visitor.getOrderByColumns().size());


         Assert.assertTrue(visitor.containsColumn("tcp_cpr.con_config", "contract_config_id"));
         Assert.assertTrue(visitor.containsColumn("tcp_cpr.con_osg3a_headers", "parent_id"));
//
    }
}
