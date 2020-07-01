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

public class OracleCreateViewTest11_error_encoding extends OracleTest {

    public void test_types() throws Exception {
        String sql = //
        " CREATE OR REPLACE FORCE VIEW \"TCP_CPR\".\"CPR_SYS_BOM_MODULE_V\" (\"PROD_TYPE_ID\", \"PROD_TYPE_NAME\", \"PROD_ID\", \"PROD_NAME\", \"DEVICE_ID\", \"DEVICE_NAME\", \"MODULE_ID\", \"STRUCTURE_CODE\", \"MODULE_NAME\", \"ENG_DESCRIPTION\", \"ZH_UNIT\", \"EN_UNIT\", \"ITEM_CODE\", \"SALES_DEPT_ID\", \"CLUEIDS\", \"SEQ\", \"ORGANIZATION_ID\", \"ORGANIZATION_NAME\", \"ATTRIBUTE1\", \"ATTRIBUTE2\", \"ATTRIBUTE3\", \"ATTRIBUTE4\", \"ATTRIBUTE5\", \"ATTRIBUTE6\", \"TSM_FLAG\", \"TSM_CODE\", \"HARD_PARAM\", \"SOFT_PARAM\", \"MAKE_PARAM\", \"RISK_PARAM\", \"SOFT_COST_PARAM\", \"FIXED_RATE\", \"TEMP_FIELD1\", \"TEMP_FIELD2\") AS \n" +
                "  SELECT CP.Parent_System_Bom_Id PROD_TYPE_ID,--Я'{ID\n" +
                "       FL.DESCRIPTION PROD_TYPE_NAME,--Я'{\n" +
                "       CP.System_Bom_Id PROD_ID,--ЯID\n" +
                "       CPD.DESCRIPTION PROD_NAME,--Я\n" +
                "       DV.System_Bom_Id DEVICE_ID,--:婭D\n" +
                "       DVD.DESCRIPTION DEVICE_NAME,--:�\n" +
                "       B1.SYSTEM_BOM_ID MODULE_ID, -- !WID\n" +
                "       B1.STRUCTURE_CODE, \n" +
                "       B2.DESCRIPTION MODULE_NAME, --!W\n" +
                "       B3.DESCRIPTION ENG_DESCRIPTION,--駠\n" +
                "       B2.PRIMARY_UNIT_OF_MEASURE ZH_UNIT,---嘦M\n" +
                "       B3.PRIMARY_UNIT_OF_MEASURE EN_UNIT,--駠UM\n" +
                "       B1.ITEM_CODE, \n" +
                "       B1.SALES_DEPT_ID, \n" +
                "       B1.CLUEIDS, \n" +
                "       B1.SEQ, \n" +
                "       B1.ORGANIZATION_ID, \n" +
                "       ORGN.NAME ORGANIZATION_NAME,\n" +
                "       B1.ATTRIBUTE1, \n" +
                "       B1.ATTRIBUTE2, \n" +
                "       B1.ATTRIBUTE3, \n" +
                "       B1.ATTRIBUTE4, \n" +
                "       B1.ATTRIBUTE5, \n" +
                "       B1.ATTRIBUTE6, \n" +
                "       B1.TSM_FLAG, \n" +
                "       B1.TSM_CODE, \n" +
                "       B1.HARD_PARAM, \n" +
                "       B1.SOFT_PARAM, \n" +
                "       B1.MAKE_PARAM, \n" +
                "       B1.RISK_PARAM, \n" +
                "       B1.SOFT_COST_PARAM, \n" +
                "       B1.FIXED_RATE,\n" +
                "       B1.TEMP_FIELD1,\n" +
                "       B1.TEMP_FIELD2 \n" +
                "  FROM TCP_CPR.SYSTEM_BOM      B1,\n" +
                "       TCP_CPR.SYSTEM_BOM_DESC B2,\n" +
                "       TCP_CPR.SYSTEM_BOM_DESC B3,\n" +
                "       TCP_CPR.SYSTEM_BOM_DESC FL,\n" +
                "       TCP_CPR.SYSTEM_BOM CP,\n" +
                "       TCP_CPR.SYSTEM_BOM_DESC CPD,\n" +
                "       TCP_CPR.SYSTEM_BOM DV,\n" +
                "       TCP_CPR.SYSTEM_BOM_DESC DVD,\n" +
                "       TCP_FND.TCP_FND_ORGANIZATION ORGN\n" +
                " WHERE B1.SYSTEM_BOM_ID = B2.SYSTEM_BOM_ID(+)\n" +
                "   AND B2.ENABLED_FLAG(+) = 'Y'\n" +
                "   AND B1.SYSTEM_BOM_ID = B3.SYSTEM_BOM_ID(+)\n" +
                "   AND B3.ENABLED_FLAG(+) = 'Y'\n" +
                "   AND B1.ENABLED_FLAG(+) = 'Y'\n" +
                "   AND B1.LAYER = 4\n" +
                "   AND B3.LANGUAGE(+) = 'en-US'\n" +
                "   AND B2.LANGUAGE(+) = 'zh-CN'\n" +
                "   AND B1.Parent_System_Bom_Id = DV.SYSTEM_BOM_ID(+)\n" +
                "   AND DV.ENABLED_FLAG='Y'\n" +
                "   AND DV.SYSTEM_BOM_ID=DVD.SYSTEM_BOM_ID\n" +
                "   AND DVD.ENABLED_FLAG(+) = 'Y'\n" +
                "   AND DVD.LANGUAGE(+) = 'zh-CN'\n" +
                "   AND DV.Parent_System_Bom_Id = CP.SYSTEM_BOM_ID(+)\n" +
                "   AND CP.ENABLED_FLAG='Y'\n" +
                "   AND CP.SYSTEM_BOM_ID=CPD.SYSTEM_BOM_ID\n" +
                "   AND CPD.ENABLED_FLAG(+) = 'Y'\n" +
                "   AND CPD.LANGUAGE(+) = 'zh-CN'\n" +
                "   AND CP.Parent_System_Bom_Id = FL.SYSTEM_BOM_ID(+)\n" +
                "   AND FL.ENABLED_FLAG(+) = 'Y'\n" +
                "   AND FL.LANGUAGE(+) = 'zh-CN'\n" +
                "   AND B1.ORGANIZATION_ID=ORGN.ORGANIZATION_ID(+)";

        System.out.println(sql);

        OracleStatementParser parser = new OracleStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();
        SQLStatement stmt = statementList.get(0);
        print(statementList);

        assertEquals(1, statementList.size());

        assertEquals("CREATE OR REPLACE VIEW \"TCP_CPR\".\"CPR_SYS_BOM_MODULE_V\" (\n" +
                        "\t\"PROD_TYPE_ID\", \n" +
                        "\t\"PROD_TYPE_NAME\", \n" +
                        "\t\"PROD_ID\", \n" +
                        "\t\"PROD_NAME\", \n" +
                        "\t\"DEVICE_ID\", \n" +
                        "\t\"DEVICE_NAME\", \n" +
                        "\t\"MODULE_ID\", \n" +
                        "\t\"STRUCTURE_CODE\", \n" +
                        "\t\"MODULE_NAME\", \n" +
                        "\t\"ENG_DESCRIPTION\", \n" +
                        "\t\"ZH_UNIT\", \n" +
                        "\t\"EN_UNIT\", \n" +
                        "\t\"ITEM_CODE\", \n" +
                        "\t\"SALES_DEPT_ID\", \n" +
                        "\t\"CLUEIDS\", \n" +
                        "\t\"SEQ\", \n" +
                        "\t\"ORGANIZATION_ID\", \n" +
                        "\t\"ORGANIZATION_NAME\", \n" +
                        "\t\"ATTRIBUTE1\", \n" +
                        "\t\"ATTRIBUTE2\", \n" +
                        "\t\"ATTRIBUTE3\", \n" +
                        "\t\"ATTRIBUTE4\", \n" +
                        "\t\"ATTRIBUTE5\", \n" +
                        "\t\"ATTRIBUTE6\", \n" +
                        "\t\"TSM_FLAG\", \n" +
                        "\t\"TSM_CODE\", \n" +
                        "\t\"HARD_PARAM\", \n" +
                        "\t\"SOFT_PARAM\", \n" +
                        "\t\"MAKE_PARAM\", \n" +
                        "\t\"RISK_PARAM\", \n" +
                        "\t\"SOFT_COST_PARAM\", \n" +
                        "\t\"FIXED_RATE\", \n" +
                        "\t\"TEMP_FIELD1\", \n" +
                        "\t\"TEMP_FIELD2\"\n" +
                        ")\n" +
                        "AS\n" +
                        "SELECT CP.Parent_System_Bom_Id AS PROD_TYPE_ID, FL.DESCRIPTION AS PROD_TYPE_NAME, CP.System_Bom_Id AS PROD_ID, CPD.DESCRIPTION AS PROD_NAME, DV.System_Bom_Id AS DEVICE_ID\n" +
                        "\t, DVD.DESCRIPTION AS DEVICE_NAME, B1.SYSTEM_BOM_ID AS MODULE_ID, B1.STRUCTURE_CODE, B2.DESCRIPTION AS MODULE_NAME, B3.DESCRIPTION AS ENG_DESCRIPTION\n" +
                        "\t, B2.PRIMARY_UNIT_OF_MEASURE AS ZH_UNIT, B3.PRIMARY_UNIT_OF_MEASURE AS EN_UNIT, B1.ITEM_CODE, B1.SALES_DEPT_ID, B1.CLUEIDS\n" +
                        "\t, B1.SEQ, B1.ORGANIZATION_ID, ORGN.NAME AS ORGANIZATION_NAME, B1.ATTRIBUTE1, B1.ATTRIBUTE2\n" +
                        "\t, B1.ATTRIBUTE3, B1.ATTRIBUTE4, B1.ATTRIBUTE5, B1.ATTRIBUTE6, B1.TSM_FLAG\n" +
                        "\t, B1.TSM_CODE, B1.HARD_PARAM, B1.SOFT_PARAM, B1.MAKE_PARAM, B1.RISK_PARAM\n" +
                        "\t, B1.SOFT_COST_PARAM, B1.FIXED_RATE, B1.TEMP_FIELD1, B1.TEMP_FIELD2\n" +
                        "FROM TCP_CPR.SYSTEM_BOM B1, TCP_CPR.SYSTEM_BOM_DESC B2, TCP_CPR.SYSTEM_BOM_DESC B3, TCP_CPR.SYSTEM_BOM_DESC FL, TCP_CPR.SYSTEM_BOM CP, TCP_CPR.SYSTEM_BOM_DESC CPD, TCP_CPR.SYSTEM_BOM DV, TCP_CPR.SYSTEM_BOM_DESC DVD, TCP_FND.TCP_FND_ORGANIZATION ORGN\n" +
                        "WHERE B1.SYSTEM_BOM_ID = B2.SYSTEM_BOM_ID(+)\n" +
                        "\tAND B2.ENABLED_FLAG(+) = 'Y'\n" +
                        "\tAND B1.SYSTEM_BOM_ID = B3.SYSTEM_BOM_ID(+)\n" +
                        "\tAND B3.ENABLED_FLAG(+) = 'Y'\n" +
                        "\tAND B1.ENABLED_FLAG(+) = 'Y'\n" +
                        "\tAND B1.LAYER = 4\n" +
                        "\tAND B3.LANGUAGE(+) = 'en-US'\n" +
                        "\tAND B2.LANGUAGE(+) = 'zh-CN'\n" +
                        "\tAND B1.Parent_System_Bom_Id = DV.SYSTEM_BOM_ID(+)\n" +
                        "\tAND DV.ENABLED_FLAG = 'Y'\n" +
                        "\tAND DV.SYSTEM_BOM_ID = DVD.SYSTEM_BOM_ID\n" +
                        "\tAND DVD.ENABLED_FLAG(+) = 'Y'\n" +
                        "\tAND DVD.LANGUAGE(+) = 'zh-CN'\n" +
                        "\tAND DV.Parent_System_Bom_Id = CP.SYSTEM_BOM_ID(+)\n" +
                        "\tAND CP.ENABLED_FLAG = 'Y'\n" +
                        "\tAND CP.SYSTEM_BOM_ID = CPD.SYSTEM_BOM_ID\n" +
                        "\tAND CPD.ENABLED_FLAG(+) = 'Y'\n" +
                        "\tAND CPD.LANGUAGE(+) = 'zh-CN'\n" +
                        "\tAND CP.Parent_System_Bom_Id = FL.SYSTEM_BOM_ID(+)\n" +
                        "\tAND FL.ENABLED_FLAG(+) = 'Y'\n" +
                        "\tAND FL.LANGUAGE(+) = 'zh-CN'\n" +
                        "\tAND B1.ORGANIZATION_ID = ORGN.ORGANIZATION_ID(+)",//
                            SQLUtils.toSQLString(stmt, JdbcConstants.ORACLE));

        OracleSchemaStatVisitor visitor = new OracleSchemaStatVisitor();
        stmt.accept(visitor);

        System.out.println("Tables : " + visitor.getTables());
        System.out.println("fields : " + visitor.getColumns());
        System.out.println("coditions : " + visitor.getConditions());
        System.out.println("relationships : " + visitor.getRelationships());
        System.out.println("orderBy : " + visitor.getOrderByColumns());

        assertEquals(3, visitor.getTables().size());

        assertEquals(33, visitor.getColumns().size());

        assertTrue(visitor.getColumns().contains(new TableStat.Column("TCP_CPR.SYSTEM_BOM", "Parent_System_Bom_Id")));
    }
}
