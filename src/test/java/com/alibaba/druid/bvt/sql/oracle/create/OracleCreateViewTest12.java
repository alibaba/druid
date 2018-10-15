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
import com.alibaba.druid.stat.TableStat;
import com.alibaba.druid.util.JdbcConstants;

import java.util.List;

public class OracleCreateViewTest12 extends OracleTest {

    public void test_types() throws Exception {
        String sql = //
        "CREATE OR REPLACE FORCE VIEW \"TCP_CPR\".\"DIFF_CON_CONFIG_ALL_V\" (\"OSG_TYPE_ID\", \"CONTRACT_HEADER_ID\", \"CONTRACT_NUMBER\", \"ORGANIZATION_ID\", \"CONTRACT_PRODUCT_ID\", \"PROD_ID\", \"PROD_DES\", \"MI\", \"CONTRACT_DEVICE_ID\", \"DEV_ID\", \"DEV_DES\", \"SITE_ID\", \"SITE_QUANTITY\", \"SITE_DES\", \"CONTRACT_MODULE_ID\", \"MOD_ID\", \"MOD_DES\", \"MODULE_QUANTITY\", \"CONTRACT_ITEM_ID\", \"ITEM_ID\", \"ITEM_DES\", \"ITEM_TYPE\", \"ITEM_QUANTITY\", \"HARD_PARAM\", \"SOFT_PARAM\", \"MAKE_PARAM\", \"RISK_PARAM\", \"SOFT_COST_PARAM\", \"PROD_MANAGER\", \"COST_PRICE04\", \"CONFIRM_ITEM_PARAM\", \"CONFIRM_FLAG04\", \"COST_PRICE\", \"COST_PRICE_PARAMETER\", \"OLD_COST\", \"LIST_PRICE\", \"ITEM_CODE\", \"CONFIRM_COST_PRICE04\", \"CUSTOMIZE_SITES_ID\", \"SPARE_FLAG\") AS \n" +
                "  SELECT I.osg_type_id, M.CONTRACT_HEADER_ID,M.CONTRACT_NUMBER,\n" +
                "       M.ORGANIZATION_ID,\n" +
                "       M.CONTRACT_PRODUCT_ID,\n" +
                "       M.prod_id,\n" +
                "       M.prod_des,M.MI,\n" +
                "       M.CONTRACT_DEVICE_ID,\n" +
                "       M.dev_id,\n" +
                "       m.dev_des,\n" +
                "       M.SITE_ID ,\n" +
                "       M.SITE_QUANTITY,\n" +
                "       M.site_des,\n" +
                "       M.CONTRACT_MODULE_ID,\n" +
                "       M.mod_id,\n" +
                "       M.mod_des,\n" +
                "       M.MODULE_QUANTITY,\n" +
                "       I.CONTRACT_ITEM_ID,\n" +
                "       I.ITEM_ID,\n" +
                "       I.ITEM_DES,\n" +
                "       I.ITEM_TYPE,\n" +
                "       I.item_quantity,\n" +
                "       M.hard_param,\n" +
                "       M.soft_param, M.make_param,M.risk_param, M.soft_cost_param,\n" +
                "       M.prod_manager, I.COST_PRICE04, I.CONFIRM_ITEM_PARAM, I.CONFIRM_FLAG04,\n" +
                "       I.cost_price,I.COST_PRICE_PARAMETER,I.OLD_COST,I.LIST_PRICE,I.ITEM_CODE,\n" +
                "       I.CONFIRM_COST_PRICE04\n" +
                "       --,I.PROD_ATTRIBUTE_ID,I.ITEM_CHIP\n" +
                "       ,M.customize_sites_id\n" +
                "       ,M.spare_flag\n" +
                "  FROM TCP_CPR.DIFF_CON_CONFIG_MODULE_V M\n" +
                "  LEFT JOIN\n" +
                "  (\n" +
                "      (\n" +
                "        select 0 osg_type_id,\n" +
                "               v.contract_module_id,\n" +
                "               v.item_id,\n" +
                "               v.contract_item_id,\n" +
                "               v.item_des,\n" +
                "               v.item_type,\n" +
                "               v.ITEM_QUANTITY,\n" +
                "               v.cost_price,\n" +
                "               v.COST_PRICE_PARAMETER,\n" +
                "               v.CONFIRM_FLAG,\n" +
                "               v.COST_PRICE04,\n" +
                "               v.CONFIRM_ITEM_PARAM,\n" +
                "               v.CONFIRM_FLAG04,\n" +
                "               v.OLD_COST,\n" +
                "               v.LIST_PRICE,\n" +
                "               v.ITEM_CODE,\n" +
                "               v.CONFIRM_COST_PRICE04\n" +
                "        from TCP_CPR.DIFF_CON_CONFIG_ITEM_V v\n" +
                "      )\n" +
                "      union all\n" +
                "      (\n" +
                "        SELECT header.product_id osg_type_id,\n" +
                "               HEADER.PARENT_ID CONTRACT_MODULE_ID,\n" +
                "               HEADER.SERIAL_ID item_id,\n" +
                "               HEADER.OSG_HEADER_ID CONTRACT_ITEM_ID,\n" +
                "               ser.product_serial item_name,\n" +
                "               'OSG' item_type,\n" +
                "               HEADER.QUANTITY item_quantity,\n" +
                "               (LINE.REF_PRICE+ nvl(REPLY.MARKET_REFERENCE_PRICE,0)) COST_PRICE,\n" +
                "               1 COST_PRICE_PARAMETER,\n" +
                "               'Y' CONFIRM_FLAG,\n" +
                "               0 COST_PRICE04,\n" +
                "               1 CONFIRM_ITEM_PARAM,\n" +
                "               'Y' CONFIRM_FLAG04,\n" +
                "               1 OLD_COST,\n" +
                "               --LINE.PRICE+nvl(REPLY.LIST_PRICE,0) LIST_PRICE,\n" +
                "               HEADER.LIST_PRICE LIST_PRICE,\n" +
                "               '+Mn\u0016-�' ITEM_CODE,\n" +
                "              (LINE.COST+ nvl(REPLY.RMBPRICE_WITHTAX,0)) CONFIRM_COST_PRICE04\n" +
                "             --0 PROD_ATTRIBUTE_ID,0 ITEM_CHIP\n" +
                "          FROM TCP_CPR.DIFF_CON_OSG3_HEADERS HEADER,ERP_ZTE.ZTE_KX_OSG3_SERIALS ser, ERP_ZTE.zte_kx_osg3_reply_headers\n" +
                "            REPLY, (\n" +
                "            select LINE.OSG_HEADER_ID,SUM((LINE.QUANTITY-LINE.THEORETIC_QTY)*\n" +
                "                PART.rmbprice_withtax) COST, SUM((LINE.QUANTITY-\n" +
                "                LINE.THEORETIC_QTY)* PART.LIST_PRICE) PRICE, SUM((\n" +
                "                LINE.QUANTITY-LINE.THEORETIC_QTY)* PART.MARKET_REFERENCE_PRICE\n" +
                "                ) REF_PRICE\n" +
                "              from TCP_CPR.DIFF_CON_OSG3_LINES LINE,\n" +
                "                ERP_ZTE.ZTE_KX_OSG3_PART_DETAILS PART\n" +
                "              WHERE LINE.PART_DETAIL_ID = PART.PART_DETAIL_ID\n" +
                "                AND LINE.ENABLED_FLAG = 'Y'\n" +
                "              GROUP BY LINE.OSG_HEADER_ID) LINE\n" +
                "          where HEADER.ENABLED_FLAG = 'Y' AND ser.serial_id=HEADER.Serial_Id\n" +
                "            and header.REPLY_ID = reply.reply_head_id(+)\n" +
                "            and header.OSG_HEADER_ID = line.OSG_HEADER_ID\n" +
                "      )\n" +
                "      union all\n" +
                "      (\n" +
                "        SELECT item.osg_type_id osg_type_id,\n" +
                "               ITEM.PARENT_ID CONTRACT_MODULE_ID,\n" +
                "               item.osg_item_id item_id,\n" +
                "               ITEM.OSG_ITEM_ID CONTRACT_ITEM_ID,\n" +
                "               SYS_ITEM.DESCRIPTION item_name,\n" +
                "               'SINGLEOSG' item_type,\n" +
                "               ITEM.QUANTITY item_quantity,\n" +
                "               SYS_ITEM.MARKET_REFERENCE_PRICE COST_PRICE,\n" +
                "               1 COST_PRICE_PARAMETER,\n" +
                "               SYS_ITEM.ENABLED_FLAG CONFIRM_FLAG,\n" +
                "               0 COST_PRICE04,\n" +
                "               1 CONFIRM_ITEM_PARAM,\n" +
                "               'Y' CONFIRM_FLAG04,\n" +
                "               1 OLD_COST,\n" +
                "               --SYS_ITEM.LIST_PRICE LIST_PRICE,\n" +
                "               ITEM.LIST_PRICE LIST_PRICE,\n" +
                "               SYS_ITEM.INVENTORY_ID||'\n" +
                "+Mn\u0016-�' ITEM_CODE,\n" +
                "               SYS_ITEM.PRICE CONFIRM_COST_PRICE04--, 0 PROD_ATTRIBUTE_ID--,0 ITEM_CHIP\n" +
                "          FROM TCP_CPR.DIFF_CON_OSG3A_HEADERS ITEM,ERP_ZTE.ZTE_KX_OSG3_ITEMS\n" +
                "            SYS_ITEM\n" +
                "          where ITEM.OSG_ITEM_ID = SYS_ITEM.OSG_ITEM_ID\n" +
                "            AND ITEM.ENABLED_FLAG = 'Y'\n" +
                "       )\n" +
                "  ) I\n" +
                "  ON M.CONTRACT_MODULE_ID = I.CONTRACT_MODULE_ID\n" +
                "  WHERE item_quantity>=0";

        System.out.println(sql);

        OracleStatementParser parser = new OracleStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();
        SQLStatement stmt = statementList.get(0);
        print(statementList);

        assertEquals(1, statementList.size());

        assertEquals("CREATE OR REPLACE VIEW \"TCP_CPR\".\"DIFF_CON_CONFIG_ALL_V\" (\n" +
                        "\t\"OSG_TYPE_ID\", \n" +
                        "\t\"CONTRACT_HEADER_ID\", \n" +
                        "\t\"CONTRACT_NUMBER\", \n" +
                        "\t\"ORGANIZATION_ID\", \n" +
                        "\t\"CONTRACT_PRODUCT_ID\", \n" +
                        "\t\"PROD_ID\", \n" +
                        "\t\"PROD_DES\", \n" +
                        "\t\"MI\", \n" +
                        "\t\"CONTRACT_DEVICE_ID\", \n" +
                        "\t\"DEV_ID\", \n" +
                        "\t\"DEV_DES\", \n" +
                        "\t\"SITE_ID\", \n" +
                        "\t\"SITE_QUANTITY\", \n" +
                        "\t\"SITE_DES\", \n" +
                        "\t\"CONTRACT_MODULE_ID\", \n" +
                        "\t\"MOD_ID\", \n" +
                        "\t\"MOD_DES\", \n" +
                        "\t\"MODULE_QUANTITY\", \n" +
                        "\t\"CONTRACT_ITEM_ID\", \n" +
                        "\t\"ITEM_ID\", \n" +
                        "\t\"ITEM_DES\", \n" +
                        "\t\"ITEM_TYPE\", \n" +
                        "\t\"ITEM_QUANTITY\", \n" +
                        "\t\"HARD_PARAM\", \n" +
                        "\t\"SOFT_PARAM\", \n" +
                        "\t\"MAKE_PARAM\", \n" +
                        "\t\"RISK_PARAM\", \n" +
                        "\t\"SOFT_COST_PARAM\", \n" +
                        "\t\"PROD_MANAGER\", \n" +
                        "\t\"COST_PRICE04\", \n" +
                        "\t\"CONFIRM_ITEM_PARAM\", \n" +
                        "\t\"CONFIRM_FLAG04\", \n" +
                        "\t\"COST_PRICE\", \n" +
                        "\t\"COST_PRICE_PARAMETER\", \n" +
                        "\t\"OLD_COST\", \n" +
                        "\t\"LIST_PRICE\", \n" +
                        "\t\"ITEM_CODE\", \n" +
                        "\t\"CONFIRM_COST_PRICE04\", \n" +
                        "\t\"CUSTOMIZE_SITES_ID\", \n" +
                        "\t\"SPARE_FLAG\"\n" +
                        ")\n" +
                        "AS\n" +
                        "SELECT I.osg_type_id, M.CONTRACT_HEADER_ID, M.CONTRACT_NUMBER, M.ORGANIZATION_ID, M.CONTRACT_PRODUCT_ID\n" +
                        "\t, M.prod_id, M.prod_des, M.MI, M.CONTRACT_DEVICE_ID, M.dev_id\n" +
                        "\t, m.dev_des, M.SITE_ID, M.SITE_QUANTITY, M.site_des, M.CONTRACT_MODULE_ID\n" +
                        "\t, M.mod_id, M.mod_des, M.MODULE_QUANTITY, I.CONTRACT_ITEM_ID, I.ITEM_ID\n" +
                        "\t, I.ITEM_DES, I.ITEM_TYPE, I.item_quantity, M.hard_param, M.soft_param\n" +
                        "\t, M.make_param, M.risk_param, M.soft_cost_param, M.prod_manager, I.COST_PRICE04\n" +
                        "\t, I.CONFIRM_ITEM_PARAM, I.CONFIRM_FLAG04, I.cost_price, I.COST_PRICE_PARAMETER, I.OLD_COST\n" +
                        "\t, I.LIST_PRICE, I.ITEM_CODE, I.CONFIRM_COST_PRICE04, M.customize_sites_id, M.spare_flag\n" +
                        "FROM TCP_CPR.DIFF_CON_CONFIG_MODULE_V M\n" +
                        "\tLEFT JOIN (\n" +
                        "\t\tSELECT 0 AS osg_type_id, v.contract_module_id, v.item_id, v.contract_item_id, v.item_des\n" +
                        "\t\t\t, v.item_type, v.ITEM_QUANTITY, v.cost_price, v.COST_PRICE_PARAMETER, v.CONFIRM_FLAG\n" +
                        "\t\t\t, v.COST_PRICE04, v.CONFIRM_ITEM_PARAM, v.CONFIRM_FLAG04, v.OLD_COST, v.LIST_PRICE\n" +
                        "\t\t\t, v.ITEM_CODE, v.CONFIRM_COST_PRICE04\n" +
                        "\t\tFROM TCP_CPR.DIFF_CON_CONFIG_ITEM_V v\n" +
                        "\t\tUNION ALL\n" +
                        "\t\tSELECT header.product_id AS osg_type_id, HEADER.PARENT_ID AS CONTRACT_MODULE_ID, HEADER.SERIAL_ID AS item_id, HEADER.OSG_HEADER_ID AS CONTRACT_ITEM_ID, ser.product_serial AS item_name\n" +
                        "\t\t\t, 'OSG' AS item_type, HEADER.QUANTITY AS item_quantity\n" +
                        "\t\t\t, LINE.REF_PRICE + nvl(REPLY.MARKET_REFERENCE_PRICE, 0) AS COST_PRICE\n" +
                        "\t\t\t, 1 AS COST_PRICE_PARAMETER, 'Y' AS CONFIRM_FLAG, 0 AS COST_PRICE04, 1 AS CONFIRM_ITEM_PARAM, 'Y' AS CONFIRM_FLAG04\n" +
                        "\t\t\t, 1 AS OLD_COST, HEADER.LIST_PRICE AS LIST_PRICE, '+Mn\u0016-�' AS ITEM_CODE\n" +
                        "\t\t\t, LINE.COST + nvl(REPLY.RMBPRICE_WITHTAX, 0) AS CONFIRM_COST_PRICE04\n" +
                        "\t\tFROM TCP_CPR.DIFF_CON_OSG3_HEADERS HEADER, ERP_ZTE.ZTE_KX_OSG3_SERIALS ser, ERP_ZTE.zte_kx_osg3_reply_headers REPLY, (\n" +
                        "\t\t\tSELECT LINE.OSG_HEADER_ID, SUM((LINE.QUANTITY - LINE.THEORETIC_QTY) * PART.rmbprice_withtax) AS COST\n" +
                        "\t\t\t\t, SUM((LINE.QUANTITY - LINE.THEORETIC_QTY) * PART.LIST_PRICE) AS PRICE\n" +
                        "\t\t\t\t, SUM((LINE.QUANTITY - LINE.THEORETIC_QTY) * PART.MARKET_REFERENCE_PRICE) AS REF_PRICE\n" +
                        "\t\t\tFROM TCP_CPR.DIFF_CON_OSG3_LINES LINE, ERP_ZTE.ZTE_KX_OSG3_PART_DETAILS PART\n" +
                        "\t\t\tWHERE LINE.PART_DETAIL_ID = PART.PART_DETAIL_ID\n" +
                        "\t\t\t\tAND LINE.ENABLED_FLAG = 'Y'\n" +
                        "\t\t\tGROUP BY LINE.OSG_HEADER_ID\n" +
                        "\t\t) LINE\n" +
                        "\t\tWHERE HEADER.ENABLED_FLAG = 'Y'\n" +
                        "\t\t\tAND ser.serial_id = HEADER.Serial_Id\n" +
                        "\t\t\tAND header.REPLY_ID = reply.reply_head_id(+)\n" +
                        "\t\t\tAND header.OSG_HEADER_ID = line.OSG_HEADER_ID\n" +
                        "\t\tUNION ALL\n" +
                        "\t\tSELECT item.osg_type_id AS osg_type_id, ITEM.PARENT_ID AS CONTRACT_MODULE_ID, item.osg_item_id AS item_id, ITEM.OSG_ITEM_ID AS CONTRACT_ITEM_ID, SYS_ITEM.DESCRIPTION AS item_name\n" +
                        "\t\t\t, 'SINGLEOSG' AS item_type, ITEM.QUANTITY AS item_quantity, SYS_ITEM.MARKET_REFERENCE_PRICE AS COST_PRICE, 1 AS COST_PRICE_PARAMETER, SYS_ITEM.ENABLED_FLAG AS CONFIRM_FLAG\n" +
                        "\t\t\t, 0 AS COST_PRICE04, 1 AS CONFIRM_ITEM_PARAM, 'Y' AS CONFIRM_FLAG04, 1 AS OLD_COST, ITEM.LIST_PRICE AS LIST_PRICE\n" +
                        "\t\t\t, SYS_ITEM.INVENTORY_ID || '\n" +
                        "+Mn\u0016-�' AS ITEM_CODE, SYS_ITEM.PRICE AS CONFIRM_COST_PRICE04\n" +
                        "\t\tFROM TCP_CPR.DIFF_CON_OSG3A_HEADERS ITEM, ERP_ZTE.ZTE_KX_OSG3_ITEMS SYS_ITEM\n" +
                        "\t\tWHERE ITEM.OSG_ITEM_ID = SYS_ITEM.OSG_ITEM_ID\n" +
                        "\t\t\tAND ITEM.ENABLED_FLAG = 'Y'\n" +
                        "\t) I ON M.CONTRACT_MODULE_ID = I.CONTRACT_MODULE_ID \n" +
                        "WHERE item_quantity >= 0",//
                            SQLUtils.toSQLString(stmt, JdbcConstants.ORACLE));

        OracleSchemaStatVisitor visitor = new OracleSchemaStatVisitor();
        stmt.accept(visitor);

        System.out.println("Tables : " + visitor.getTables());
        System.out.println("fields : " + visitor.getColumns());
        System.out.println("coditions : " + visitor.getConditions());
        System.out.println("relationships : " + visitor.getRelationships());
        System.out.println("orderBy : " + visitor.getOrderByColumns());

        assertEquals(9, visitor.getTables().size());

        assertEquals(75, visitor.getColumns().size());

        assertTrue(visitor.getColumns().contains(new TableStat.Column("TCP_CPR.DIFF_CON_CONFIG_ITEM_V", "contract_module_id")));
    }
}
