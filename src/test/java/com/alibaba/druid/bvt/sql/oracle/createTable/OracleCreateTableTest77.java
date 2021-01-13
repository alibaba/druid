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
package com.alibaba.druid.bvt.sql.oracle.createTable;

import com.alibaba.druid.sql.OracleTest;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.util.JdbcConstants;

import java.util.List;

public class OracleCreateTableTest77 extends OracleTest {

    public void test_types() throws Exception {
        String sql = //
        " CREATE OR REPLACE FORCE VIEW \"TPC_CPR\".\"TPC_CPR_ITEM_PRICE_V\" (\"CONTRACT_HEADER_ID\", \"CONTRACT_NUMBER\", \"CONTRACT_PRODUCT_ID\", \"PROD_ID\", \"PROD_DES\", \"MI\", \"CONTRACT_DEVICE_ID\", \"DEV_ID\", \"DEV_DES\", \"SITE_ID\", \"SITE_QUANTITY\", \"SITE_DES\", \"CONTRACT_MODULE_ID\", \"MOD_ID\", \"MOD_DES\", \"MODULE_QUANTITY\", \"CONTRACT_ITEM_ID\", \"ITEM_ID\", \"ITEM_DES\", \"ITEM_TYPE\", \"ITEM_QUANTITY\", \"HARD_PARAM\", \"SOFT_PARAM\", \"MAKE_PARAM\", \"RISK_PARAM\", \"SOFT_COST_PARAM\", \"PROD_MANAGER\", \"COST_PRICE04\", \"CONFIRM_ITEM_PARAM\", \"CONFIRM_FLAG04\", \"COST_PRICE\", \"COST_PRICE_PARAMETER\", \"OLD_COST\", \"LIST_PRICE\", \"ITEM_CODE\", \"CONFIRM_COST_PRICE04\", \"PROD_ATTRIBUTE_ID\", \"ITEM_CHIP\") AS \n" +
                "  select Mod.CONTRACT_HEADER_ID,'',Mod.CONTRACT_PRODUCT_ID,\n" +
                "Mod.PROD_ID , Mod.PROD_DES,Mod.MI, Mod.CONTRACT_DEVICE_ID, Mod.DEV_ID,Mod.DEV_DES,\n" +
                "Mod.SITE_ID , Mod.SITE_QUANTITY, Mod.SITE_DES, Mod.CONTRACT_MODULE_ID,\n" +
                "Mod.MOD_ID, Mod.MOD_DES, Mod.module_quantity, I.CONTRACT_ITEM_ID, I.INVENTORY_ITEM_ID\n" +
                "ITEM_ID, I.DESCRIPTION ITEM_DES,I.ATTRIBUTE1 ITEM_TYPE , I.item_quantity,\n" +
                "Mod.hard_param,Mod.soft_param, Mod.make_param, Mod.risk_param, Mod.soft_cost_param,\n" +
                "Mod.prod_manager, I.COST_PRICE04, I.CONFIRM_ITEM_PARAM, I.CONFIRM_FLAG04,\n" +
                "I.cost_price, I.COST_PRICE_PARAMETER,I.OLD_COST,I.LIST_PRICE,I.ITEM_CODE,\n" +
                "I.CONFIRM_COST_PRICE04,I.PROD_ATTRIBUTE_ID,I.ITEM_CHIP from ( SELECT p.CONTRACT_HEADER_ID,'',P.CONTRACT_PRODUCT_ID,\n" +
                "P.INVENTORY_ITEM_ID PROD_ID,P.DESCRIPTION PROD_DES,P.MI, D.CONTRACT_DEVICE_ID,\n" +
                "D.INVENTORY_ITEM_ID DEV_ID,D.DESCRIPTION DEV_DES, S.SITE_ID, S.SITE_QUANTITY,\n" +
                "( S.SITE_ADDRESS||S.SECOND_LEVEL|| S.THIRD_LEVEL|| S.FOURTH_LEVEL) SITE_DES,\n" +
                "M.CONTRACT_MODULE_ID, M.INVENTORY_ITEM_ID MOD_ID , M.DESCRIPTION MOD_DES,\n" +
                "M.module_quantity, p.hard_param, p.soft_param, p.make_param,p.risk_param,\n" +
                "p.soft_cost_param, p.prod_manager FROM ( SELECT PROD.*,SYS_ITEM1.ATTRIBUTE2\n" +
                "MI,SYS_ITEM1.DESCRIPTION, SYS_ITEM1.ATTRIBUTE3 PROD_MANAGER , SYS_ITEM1.HARD_PARAM,\n" +
                "SYS_ITEM1.SOFT_PARAM,SYS_ITEM1.MAKE_PARAM,SYS_ITEM1.RISK_PARAM, SYS_ITEM1.SOFT_COST_PARAM\n" +
                "FROM TPC_CPR.TPC_CPR_PRODUCTS PROD, TPC_CPR.TPC_CPR_SYSTEM_ITEMS SYS_ITEM1\n" +
                "WHERE PROD.INVENTORY_ITEM_ID = SYS_ITEM1.INVENTORY_ITEM_ID AND SYS_ITEM1.ITEM_TYPE\n" +
                "= 'CP' AND PROD.ENABLED_FLAG = 'Y') P, ( SELECT DEV.*,SYS_ITEM2.DESCRIPTION\n" +
                "FROM TPC_CPR.TPC_CPR_DEVICES DEV,TPC_CPR.TPC_CPR_SYSTEM_ITEMS SYS_ITEM2\n" +
                "WHERE DEV.INVENTORY_ITEM_ID = SYS_ITEM2.INVENTORY_ITEM_ID AND SYS_ITEM2.ITEM_TYPE\n" +
                "= 'SB' AND DEV.ENABLED_FLAG = 'Y') D, ( SELECT SITE.* FROM TPC_CPR.TPC_CPR_SITES\n" +
                "SITE WHERE SITE.ENABLED_FLAG = 'Y') S, ( SELECT MOD.*,SYS_ITEM3.DESCRIPTION\n" +
                "FROM TPC_CPR.TPC_CPR_MODULES MOD,TPC_CPR.TPC_CPR_SYSTEM_ITEMS SYS_ITEM3\n" +
                "WHERE MOD.INVENTORY_ITEM_ID = SYS_ITEM3.INVENTORY_ITEM_ID AND SYS_ITEM3.ITEM_TYPE\n" +
                "= 'MK' AND MOD.ENABLED_FLAG = 'Y') M WHERE P.CONTRACT_PRODUCT_ID = D.CONTRACT_PRODUCT_ID\n" +
                "AND D.CONTRACT_DEVICE_ID = S.CONTRACT_DEVICE_ID AND S.SITE_ID = M.SITE_ID\n" +
                ") Mod LEFT JOIN ( ( SELECT ITEM.CONTRACT_MODULE_ID CONTRACT_MODULE_ID,\n" +
                "ITEM.INVENTORY_ITEM_ID, ITEM.CONTRACT_ITEM_ID, SYS_ITEM.DESCRIPTION, SYS_ITEM.ATTRIBUTE1,\n" +
                "ITEM.ITEM_QUANTITY, SYS_ITEM.CATALOG_SOFTWARE_FLAG, P.COST_PRICE, P.COST_PRICE_PARAMETER,\n" +
                "P.CONFIRM_FLAG,P.COST_PRICE04, P.CONFIRM_ITEM_PARAM, P.CONFIRM_FLAG04,\n" +
                "P.OLD_COST,P.LIST_PRICE, SYS_ITEM.ITEM_CODE, P.CONFIRM_COST_PRICE04, SYS_ITEM.ITEM_ID\n" +
                "PROD_ATTRIBUTE_ID,SYS_ITEM.ITEM_CHIP FROM TPC_CPR.TPC_CPR_ITEMS ITEM, TPC_CPR.TPC_CPR_SYSTEM_ITEMS\n" +
                "SYS_ITEM, TPC_CPR.TPC_CPR_PRICE P where ITEM.INVENTORY_ITEM_ID = P.INVENTORY_ITEM_ID\n" +
                "AND ITEM.INVENTORY_ITEM_ID = SYS_ITEM.INVENTORY_ITEM_ID AND ITEM.ENABLED_FLAG\n" +
                "= 'Y' ) ) I ON Mod.CONTRACT_MODULE_ID = I.CONTRACT_MODULE_ID ";

        List<SQLStatement> statementList = SQLUtils.parseStatements(sql, JdbcConstants.ORACLE);
        SQLStatement stmt = statementList.get(0);
        print(statementList);

        assertEquals(1, statementList.size());
//
        assertEquals("CREATE OR REPLACE VIEW \"TPC_CPR\".\"TPC_CPR_ITEM_PRICE_V\" (\n" +
                        "\t\"CONTRACT_HEADER_ID\", \n" +
                        "\t\"CONTRACT_NUMBER\", \n" +
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
                        "\t\"PROD_ATTRIBUTE_ID\", \n" +
                        "\t\"ITEM_CHIP\"\n" +
                        ")\n" +
                        "AS\n" +
                        "SELECT Mod.CONTRACT_HEADER_ID, NULL, Mod.CONTRACT_PRODUCT_ID, Mod.PROD_ID, Mod.PROD_DES\n" +
                        "\t, Mod.MI, Mod.CONTRACT_DEVICE_ID, Mod.DEV_ID, Mod.DEV_DES, Mod.SITE_ID\n" +
                        "\t, Mod.SITE_QUANTITY, Mod.SITE_DES, Mod.CONTRACT_MODULE_ID, Mod.MOD_ID, Mod.MOD_DES\n" +
                        "\t, Mod.module_quantity, I.CONTRACT_ITEM_ID, I.INVENTORY_ITEM_ID AS ITEM_ID, I.DESCRIPTION AS ITEM_DES, I.ATTRIBUTE1 AS ITEM_TYPE\n" +
                        "\t, I.item_quantity, Mod.hard_param, Mod.soft_param, Mod.make_param, Mod.risk_param\n" +
                        "\t, Mod.soft_cost_param, Mod.prod_manager, I.COST_PRICE04, I.CONFIRM_ITEM_PARAM, I.CONFIRM_FLAG04\n" +
                        "\t, I.cost_price, I.COST_PRICE_PARAMETER, I.OLD_COST, I.LIST_PRICE, I.ITEM_CODE\n" +
                        "\t, I.CONFIRM_COST_PRICE04, I.PROD_ATTRIBUTE_ID, I.ITEM_CHIP\n" +
                        "FROM (\n" +
                        "\tSELECT p.CONTRACT_HEADER_ID, NULL, P.CONTRACT_PRODUCT_ID, P.INVENTORY_ITEM_ID AS PROD_ID, P.DESCRIPTION AS PROD_DES\n" +
                        "\t\t, P.MI, D.CONTRACT_DEVICE_ID, D.INVENTORY_ITEM_ID AS DEV_ID, D.DESCRIPTION AS DEV_DES, S.SITE_ID\n" +
                        "\t\t, S.SITE_QUANTITY, S.SITE_ADDRESS || S.SECOND_LEVEL || S.THIRD_LEVEL || S.FOURTH_LEVEL AS SITE_DES\n" +
                        "\t\t, M.CONTRACT_MODULE_ID, M.INVENTORY_ITEM_ID AS MOD_ID, M.DESCRIPTION AS MOD_DES, M.module_quantity, p.hard_param\n" +
                        "\t\t, p.soft_param, p.make_param, p.risk_param, p.soft_cost_param, p.prod_manager\n" +
                        "\tFROM (\n" +
                        "\t\tSELECT PROD.*, SYS_ITEM1.ATTRIBUTE2 AS MI, SYS_ITEM1.DESCRIPTION, SYS_ITEM1.ATTRIBUTE3 AS PROD_MANAGER, SYS_ITEM1.HARD_PARAM\n" +
                        "\t\t\t, SYS_ITEM1.SOFT_PARAM, SYS_ITEM1.MAKE_PARAM, SYS_ITEM1.RISK_PARAM, SYS_ITEM1.SOFT_COST_PARAM\n" +
                        "\t\tFROM TPC_CPR.TPC_CPR_PRODUCTS PROD, TPC_CPR.TPC_CPR_SYSTEM_ITEMS SYS_ITEM1\n" +
                        "\t\tWHERE PROD.INVENTORY_ITEM_ID = SYS_ITEM1.INVENTORY_ITEM_ID\n" +
                        "\t\t\tAND SYS_ITEM1.ITEM_TYPE = 'CP'\n" +
                        "\t\t\tAND PROD.ENABLED_FLAG = 'Y'\n" +
                        "\t) P, (\n" +
                        "\t\tSELECT DEV.*, SYS_ITEM2.DESCRIPTION\n" +
                        "\t\tFROM TPC_CPR.TPC_CPR_DEVICES DEV, TPC_CPR.TPC_CPR_SYSTEM_ITEMS SYS_ITEM2\n" +
                        "\t\tWHERE DEV.INVENTORY_ITEM_ID = SYS_ITEM2.INVENTORY_ITEM_ID\n" +
                        "\t\t\tAND SYS_ITEM2.ITEM_TYPE = 'SB'\n" +
                        "\t\t\tAND DEV.ENABLED_FLAG = 'Y'\n" +
                        "\t) D, (\n" +
                        "\t\tSELECT SITE.*\n" +
                        "\t\tFROM TPC_CPR.TPC_CPR_SITES SITE\n" +
                        "\t\tWHERE SITE.ENABLED_FLAG = 'Y'\n" +
                        "\t) S, (\n" +
                        "\t\tSELECT MOD.*, SYS_ITEM3.DESCRIPTION\n" +
                        "\t\tFROM TPC_CPR.TPC_CPR_MODULES MOD, TPC_CPR.TPC_CPR_SYSTEM_ITEMS SYS_ITEM3\n" +
                        "\t\tWHERE MOD.INVENTORY_ITEM_ID = SYS_ITEM3.INVENTORY_ITEM_ID\n" +
                        "\t\t\tAND SYS_ITEM3.ITEM_TYPE = 'MK'\n" +
                        "\t\t\tAND MOD.ENABLED_FLAG = 'Y'\n" +
                        "\t) M\n" +
                        "\tWHERE P.CONTRACT_PRODUCT_ID = D.CONTRACT_PRODUCT_ID\n" +
                        "\t\tAND D.CONTRACT_DEVICE_ID = S.CONTRACT_DEVICE_ID\n" +
                        "\t\tAND S.SITE_ID = M.SITE_ID\n" +
                        ") Mod\n" +
                        "\tLEFT JOIN (\n" +
                        "\t\tSELECT ITEM.CONTRACT_MODULE_ID AS CONTRACT_MODULE_ID, ITEM.INVENTORY_ITEM_ID, ITEM.CONTRACT_ITEM_ID, SYS_ITEM.DESCRIPTION, SYS_ITEM.ATTRIBUTE1\n" +
                        "\t\t\t, ITEM.ITEM_QUANTITY, SYS_ITEM.CATALOG_SOFTWARE_FLAG, P.COST_PRICE, P.COST_PRICE_PARAMETER, P.CONFIRM_FLAG\n" +
                        "\t\t\t, P.COST_PRICE04, P.CONFIRM_ITEM_PARAM, P.CONFIRM_FLAG04, P.OLD_COST, P.LIST_PRICE\n" +
                        "\t\t\t, SYS_ITEM.ITEM_CODE, P.CONFIRM_COST_PRICE04, SYS_ITEM.ITEM_ID AS PROD_ATTRIBUTE_ID, SYS_ITEM.ITEM_CHIP\n" +
                        "\t\tFROM TPC_CPR.TPC_CPR_ITEMS ITEM, TPC_CPR.TPC_CPR_SYSTEM_ITEMS SYS_ITEM, TPC_CPR.TPC_CPR_PRICE P\n" +
                        "\t\tWHERE ITEM.INVENTORY_ITEM_ID = P.INVENTORY_ITEM_ID\n" +
                        "\t\t\tAND ITEM.INVENTORY_ITEM_ID = SYS_ITEM.INVENTORY_ITEM_ID\n" +
                        "\t\t\tAND ITEM.ENABLED_FLAG = 'Y'\n" +
                        "\t) I ON Mod.CONTRACT_MODULE_ID = I.CONTRACT_MODULE_ID ",//
                            SQLUtils.toSQLString(stmt, JdbcConstants.ORACLE));
//
//        SchemaStatVisitor visitor = SQLUtils.createSchemaStatVisitor(JdbcConstants.ORACLE);
//        stmt.accept(visitor);
//
//        System.out.println("Tables : " + visitor.getTables());
//        System.out.println("fields : " + visitor.getColumns());
//        System.out.println("coditions : " + visitor.getConditions());
//        System.out.println("relationships : " + visitor.getRelationships());
//        System.out.println("orderBy : " + visitor.getOrderByColumns());
//
//        assertEquals(1, visitor.getTables().size());
//
//        assertEquals(3, visitor.getColumns().size());
//
//        assertTrue(visitor.getColumns().contains(new TableStat.Column("JWGZPT.A", "XM")));
    }
}
