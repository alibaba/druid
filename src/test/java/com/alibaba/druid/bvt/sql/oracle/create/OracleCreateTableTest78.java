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
import com.alibaba.druid.util.JdbcConstants;

import java.util.List;

public class OracleCreateTableTest78 extends OracleTest {

    public void test_types() throws Exception {
        String sql = //
        "CREATE OR REPLACE FORCE VIEW \"TCP_EPM\".\"EPM_VW_CONTRACT\" (\"NID\", \"SCONTRACTNO\", \"SCONTRACTNAME\", \"NOFFICEID\", \"SOFFICENAME\", \"NSALEDEPTID\", \"SSALEDEPTNAME\", \"NPRODDEPTID\", \"SPRODDEPTNAME\", \"NCUSTOMERID\", \"SCUSTOMERNAME\", \"SSERVICEPROVIDERID\", \"SSERVICEPROVIDERNAME\", \"NMAINPRODUCTID\", \"SMAINPRODUCTNAME\", \"SCONTRACTTYPE\", \"SCONTRACTTYPETEXT\", \"SCONTRACTKIND\", \"SCONTRACTKINDTEXT\", \"SISFIRSTCHECKED\", \"SISLASTCHECKED\", \"SISOUTSOURCE\", \"SISRECONSIGN\", \"DGUARANTEE\", \"DCREATEDTIME\", \"NERPCONTRACTID\", \"NSTATE\", \"DFIRSTCHECKTIME\", \"DLASTCHECKTIME\", \"DPLANFIRSTCHECKTIME\", \"DPLANLASTCHECKTIME\") AS \n" +
                "  select\n" +
                "  EPM_TBL_CONTRACT.OID as nID,\n" +
                "  EPM_TBL_CONTRACT.CONTRACTNO as sContractNo,\n" +
                "  EPM_TBL_CONTRACT.CONTRACTNAME as sCONTRACTNAME,\n" +
                "  EPM_TBL_CONTRACT.ENGDUTYDEP as nOfficeID,\n" +
                "  TCP_FND_DEPT.DEPT_NAME as sOfficeName,\n" +
                "  TCP_EPM.EPM_TB_CONTRACT_EX.nSaleDeptID as nSaleDeptID,\n" +
                "  TCP_FND_DEPT1.DEPT_NAME as sSaleDeptName,\n" +
                "  TCP_EPM.EPM_TB_CONTRACT_EX.nProdDeptID as nProdDeptID,\n" +
                "  TCP_FND_DEPT2.DEPT_NAME as sProdDeptName,\n" +
                "  EPM_TBL_CONTRACT.CUSTOMID as nCustomerID,\n" +
                "  TCP_CUST_CUSTOMER.Name as sCustomerName,\n" +
                "  TCP_EPM.EPM_TB_CONTRACT_EX.sServiceProviderID as sServiceProviderID,\n" +
                "  TCP_CUST_ENUM.DESC_ as sServiceProviderName,\n" +
                "  EPM_TBL_CONTRACT.MASTERPRODUCT as nMainProductID,\n" +
                "  TCP_FND_PRODUCT.CN_NAME as sMainProductName,\n" +
                "  EPM_TBL_CONTRACT.CONTRACTTYPE as sContractType,\n" +
                "  TCP_FND_LOOKUP_CODE.MEANING as sContractTypeText,\n" +
                "  EPM_TBL_CONTRACT.Contrctkind as sContractKind,\n" +
                "  TCP_FND_LOOKUP_CODE1.MEANING as sContractKindText,\n" +
                "  TCP_EPM.EPM_TB_CONTRACT_EX.sIsFirstChecked as sIsFirstChecked,\n" +
                "  TCP_EPM.EPM_TB_CONTRACT_EX.sIsLastChecked as sIsLastChecked,\n" +
                "  case when (select count(*) from EPM_TBL_ENGINE_PROJECT,EPM_TBL_SUBCONTRACT_DISPATCH where EPM_TBL_ENGINE_PROJECT.CONTRACTID=EPM_TBL_CONTRACT.Oid and EPM_TBL_ENGINE_PROJECT.OID=EPM_TBL_SUBCONTRACT_DISPATCH.ENGINEID) > 0 then '/' else '&' end as sIsOutSource,\n" +
                "  case when (select count(*) from TCP_EPM.EPM_TBL_RECONSIGN_RECONSIGN where TCP_EPM.EPM_TBL_RECONSIGN_RECONSIGN.CONTRACTID=EPM_TBL_CONTRACT.Oid) > 0 then '/' else '&' end as sIsReconsign,\n" +
                "  TCP_EPM.EPM_TB_CONTRACT_EX.dGuarantee as dGuarantee,\n" +
                "  TCP_EPM.EPM_TB_CONTRACT_EX.dCreatedTime as dCreatedTime,\n" +
                "  TCP_EPM.EPM_TB_CONTRACT_EX.nERPContractID as nERPContractID,\n" +
                "  TCP_EPM.EPM_TB_CONTRACT_EX.nState as nState,\n" +
                "  TCP_EPM.EPM_TB_CONTRACT_EX.DFIRSTCHECKTIME as DFIRSTCHECKTIME,\n" +
                "  TCP_EPM.EPM_TB_CONTRACT_EX.DLASTCHECKTIME as DLASTCHECKTIME,\n" +
                "  TCP_EPM.EPM_TB_CONTRACT_EX.DPLANFIRSTCHECKTIME as DPLANFIRSTCHECKTIME,\n" +
                "  TCP_EPM.EPM_TB_CONTRACT_EX.DPLANLASTCHECKTIME as DPLANLASTCHECKTIME\n" +
                "    from\n" +
                "    ((((((((( TCP_EPM.Epm_Tbl_Contract left join TCP_FND.TCP_FND_DEPT on TCP_FND_DEPT.Dept_Id=Epm_Tbl_Contract.Engdutydep )\n" +
                "    left join TCP_EPM.EPM_TB_CONTRACT_EX on EPM_TB_CONTRACT_EX.NCONTRACTID=Epm_Tbl_Contract.Oid)\n" +
                "    left join TCP_FND.TCP_FND_DEPT TCP_FND_DEPT1 on TCP_FND_DEPT1.Dept_Id=EPM_TB_CONTRACT_EX.nSaleDeptID)\n" +
                "    left join TCP_FND.TCP_FND_DEPT TCP_FND_DEPT2 on TCP_FND_DEPT2.Dept_Id=EPM_TB_CONTRACT_EX.nProdDeptID)\n" +
                "    left join TCP_CUST.TCP_CUST_CUSTOMER on TCP_CUST_CUSTOMER.ID=EPM_TBL_CONTRACT.Customid)\n" +
                "    left join TCP_CUST.TCP_CUST_ENUM on TCP_CUST_ENUM.Codeid=EPM_TB_CONTRACT_EX.sServiceProviderID)\n" +
                "    left join TCP_FND.TCP_FND_PRODUCT on TCP_FND_PRODUCT.INVENTORY_ITEM_ID=EPM_TBL_CONTRACT.MASTERPRODUCT)\n" +
                "    left join TCP_FND.TCP_FND_LOOKUP_CODE on TCP_FND_LOOKUP_CODE.LOOKUP_TYPE='CONTRACT_TYPE' and TCP_FND_LOOKUP_CODE.LOOKUP_CODE=EPM_TBL_CONTRACT.CONTRACTTYPE)\n" +
                "    left join TCP_FND.TCP_FND_LOOKUP_CODE TCP_FND_LOOKUP_CODE1 on TCP_FND_LOOKUP_CODE1.LOOKUP_TYPE='CONTRACT_CATE' and TCP_FND_LOOKUP_CODE1.LOOKUP_CODE=EPM_TBL_CONTRACT.Contrctkind)";

        List<SQLStatement> statementList = SQLUtils.parseStatements(sql, JdbcConstants.ORACLE);
        SQLStatement stmt = statementList.get(0);
        print(statementList);

        assertEquals(1, statementList.size());
//
        assertEquals("CREATE OR REPLACE VIEW \"TCP_EPM\".\"EPM_VW_CONTRACT\" (\n" +
                        "\t\"NID\", \n" +
                        "\t\"SCONTRACTNO\", \n" +
                        "\t\"SCONTRACTNAME\", \n" +
                        "\t\"NOFFICEID\", \n" +
                        "\t\"SOFFICENAME\", \n" +
                        "\t\"NSALEDEPTID\", \n" +
                        "\t\"SSALEDEPTNAME\", \n" +
                        "\t\"NPRODDEPTID\", \n" +
                        "\t\"SPRODDEPTNAME\", \n" +
                        "\t\"NCUSTOMERID\", \n" +
                        "\t\"SCUSTOMERNAME\", \n" +
                        "\t\"SSERVICEPROVIDERID\", \n" +
                        "\t\"SSERVICEPROVIDERNAME\", \n" +
                        "\t\"NMAINPRODUCTID\", \n" +
                        "\t\"SMAINPRODUCTNAME\", \n" +
                        "\t\"SCONTRACTTYPE\", \n" +
                        "\t\"SCONTRACTTYPETEXT\", \n" +
                        "\t\"SCONTRACTKIND\", \n" +
                        "\t\"SCONTRACTKINDTEXT\", \n" +
                        "\t\"SISFIRSTCHECKED\", \n" +
                        "\t\"SISLASTCHECKED\", \n" +
                        "\t\"SISOUTSOURCE\", \n" +
                        "\t\"SISRECONSIGN\", \n" +
                        "\t\"DGUARANTEE\", \n" +
                        "\t\"DCREATEDTIME\", \n" +
                        "\t\"NERPCONTRACTID\", \n" +
                        "\t\"NSTATE\", \n" +
                        "\t\"DFIRSTCHECKTIME\", \n" +
                        "\t\"DLASTCHECKTIME\", \n" +
                        "\t\"DPLANFIRSTCHECKTIME\", \n" +
                        "\t\"DPLANLASTCHECKTIME\"\n" +
                        ")\n" +
                        "AS\n" +
                        "SELECT EPM_TBL_CONTRACT.OID AS nID, EPM_TBL_CONTRACT.CONTRACTNO AS sContractNo, EPM_TBL_CONTRACT.CONTRACTNAME AS sCONTRACTNAME, EPM_TBL_CONTRACT.ENGDUTYDEP AS nOfficeID, TCP_FND_DEPT.DEPT_NAME AS sOfficeName\n" +
                        "\t, TCP_EPM.EPM_TB_CONTRACT_EX.nSaleDeptID AS nSaleDeptID, TCP_FND_DEPT1.DEPT_NAME AS sSaleDeptName, TCP_EPM.EPM_TB_CONTRACT_EX.nProdDeptID AS nProdDeptID, TCP_FND_DEPT2.DEPT_NAME AS sProdDeptName, EPM_TBL_CONTRACT.CUSTOMID AS nCustomerID\n" +
                        "\t, TCP_CUST_CUSTOMER.Name AS sCustomerName, TCP_EPM.EPM_TB_CONTRACT_EX.sServiceProviderID AS sServiceProviderID, TCP_CUST_ENUM.DESC_ AS sServiceProviderName, EPM_TBL_CONTRACT.MASTERPRODUCT AS nMainProductID, TCP_FND_PRODUCT.CN_NAME AS sMainProductName\n" +
                        "\t, EPM_TBL_CONTRACT.CONTRACTTYPE AS sContractType, TCP_FND_LOOKUP_CODE.MEANING AS sContractTypeText, EPM_TBL_CONTRACT.Contrctkind AS sContractKind, TCP_FND_LOOKUP_CODE1.MEANING AS sContractKindText, TCP_EPM.EPM_TB_CONTRACT_EX.sIsFirstChecked AS sIsFirstChecked\n" +
                        "\t, TCP_EPM.EPM_TB_CONTRACT_EX.sIsLastChecked AS sIsLastChecked\n" +
                        "\t, CASE \n" +
                        "\t\tWHEN (\n" +
                        "\t\t\tSELECT count(*)\n" +
                        "\t\t\tFROM EPM_TBL_ENGINE_PROJECT, EPM_TBL_SUBCONTRACT_DISPATCH\n" +
                        "\t\t\tWHERE EPM_TBL_ENGINE_PROJECT.CONTRACTID = EPM_TBL_CONTRACT.Oid\n" +
                        "\t\t\t\tAND EPM_TBL_ENGINE_PROJECT.OID = EPM_TBL_SUBCONTRACT_DISPATCH.ENGINEID\n" +
                        "\t\t) > 0 THEN '/'\n" +
                        "\t\tELSE '&'\n" +
                        "\tEND AS sIsOutSource\n" +
                        "\t, CASE \n" +
                        "\t\tWHEN (\n" +
                        "\t\t\tSELECT count(*)\n" +
                        "\t\t\tFROM TCP_EPM.EPM_TBL_RECONSIGN_RECONSIGN\n" +
                        "\t\t\tWHERE TCP_EPM.EPM_TBL_RECONSIGN_RECONSIGN.CONTRACTID = EPM_TBL_CONTRACT.Oid\n" +
                        "\t\t) > 0 THEN '/'\n" +
                        "\t\tELSE '&'\n" +
                        "\tEND AS sIsReconsign, TCP_EPM.EPM_TB_CONTRACT_EX.dGuarantee AS dGuarantee, TCP_EPM.EPM_TB_CONTRACT_EX.dCreatedTime AS dCreatedTime, TCP_EPM.EPM_TB_CONTRACT_EX.nERPContractID AS nERPContractID, TCP_EPM.EPM_TB_CONTRACT_EX.nState AS nState\n" +
                        "\t, TCP_EPM.EPM_TB_CONTRACT_EX.DFIRSTCHECKTIME AS DFIRSTCHECKTIME, TCP_EPM.EPM_TB_CONTRACT_EX.DLASTCHECKTIME AS DLASTCHECKTIME, TCP_EPM.EPM_TB_CONTRACT_EX.DPLANFIRSTCHECKTIME AS DPLANFIRSTCHECKTIME, TCP_EPM.EPM_TB_CONTRACT_EX.DPLANLASTCHECKTIME AS DPLANLASTCHECKTIME\n" +
                        "FROM TCP_EPM.Epm_Tbl_Contract\n" +
                        "LEFT JOIN TCP_FND.TCP_FND_DEPT ON TCP_FND_DEPT.Dept_Id = Epm_Tbl_Contract.Engdutydep \n" +
                        "LEFT JOIN TCP_EPM.EPM_TB_CONTRACT_EX ON EPM_TB_CONTRACT_EX.NCONTRACTID = Epm_Tbl_Contract.Oid \n" +
                        "LEFT JOIN TCP_FND.TCP_FND_DEPT TCP_FND_DEPT1 ON TCP_FND_DEPT1.Dept_Id = EPM_TB_CONTRACT_EX.nSaleDeptID \n" +
                        "LEFT JOIN TCP_FND.TCP_FND_DEPT TCP_FND_DEPT2 ON TCP_FND_DEPT2.Dept_Id = EPM_TB_CONTRACT_EX.nProdDeptID \n" +
                        "LEFT JOIN TCP_CUST.TCP_CUST_CUSTOMER ON TCP_CUST_CUSTOMER.ID = EPM_TBL_CONTRACT.Customid \n" +
                        "LEFT JOIN TCP_CUST.TCP_CUST_ENUM ON TCP_CUST_ENUM.Codeid = EPM_TB_CONTRACT_EX.sServiceProviderID \n" +
                        "LEFT JOIN TCP_FND.TCP_FND_PRODUCT ON TCP_FND_PRODUCT.INVENTORY_ITEM_ID = EPM_TBL_CONTRACT.MASTERPRODUCT \n" +
                        "LEFT JOIN TCP_FND.TCP_FND_LOOKUP_CODE ON TCP_FND_LOOKUP_CODE.LOOKUP_TYPE = 'CONTRACT_TYPE'\n" +
                        "AND TCP_FND_LOOKUP_CODE.LOOKUP_CODE = EPM_TBL_CONTRACT.CONTRACTTYPE \n" +
                        "\tLEFT JOIN TCP_FND.TCP_FND_LOOKUP_CODE TCP_FND_LOOKUP_CODE1 ON TCP_FND_LOOKUP_CODE1.LOOKUP_TYPE = 'CONTRACT_CATE'\n" +
                        "AND TCP_FND_LOOKUP_CODE1.LOOKUP_CODE = EPM_TBL_CONTRACT.Contrctkind ",//
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
