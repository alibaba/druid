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
import org.junit.Assert;

import java.util.List;

public class OracleCreateTriggerTest6 extends OracleTest {

    public void test_0() throws Exception {
        String sql = //
                "CREATE OR REPLACE TRIGGER XON_EPM.EPM_TG_FIRSTCHECK\n" +
                        "\tAFTER INSERT OR UPDATE\n" +
                        "\tON XON_EPM.EPM_TBL_FIRST_CERTIFICATION_AP\n" +
                        "\tFOR EACH ROW\n" +
                        "DECLARE\n" +
                        "\tvar_nContractID number;\n" +
                        "\tvar_dFirstCheckTime date;\n" +
                        "\tvar_nRecordCount number;\n" +
                        "\tvar_sState varchar2(16);\n" +
                        "BEGIN\n" +
                        "\tvar_sState := :NEW.State;\n" +
                        "\tvar_dFirstCheckTime := :NEW.Commiteddate;\n" +
                        "\tIF var_sState = 'approved' THEN\n" +
                        "\t\t-- 鏌ヨ\uE1D7鍚堝悓 ID\n" +
                        "\t\tSELECT etfca_.contractid\n" +
                        "\t\tINTO var_nContractID\n" +
                        "\t\tFROM XON_EPM.EPM_TBL_FIRST_CERTIFICATION etfc_, XON_EPM.EPM_TBL_FIRST_CHECK_APPLICATIO etfca_\n" +
                        "\t\tWHERE etfca_.oid = etfc_.applicationid\n" +
                        "\t\t\tAND etfc_.oid = :NEW.CERTIFICATIONID;\n" +
                        "\t\tIF var_nContractID > 0 THEN\n" +
                        "\t\t\t-- 鏌ヨ\uE1D7 EPM_TB_CONTRACT_EX 鏄\uE21A惁鏈夎\uE187褰�\n" +
                        "\t\t\tSELECT COUNT(*)\n" +
                        "\t\t\tINTO var_nRecordCount\n" +
                        "\t\t\tFROM XON_EPM.EPM_TB_CONTRACT_EX etce_\n" +
                        "\t\t\tWHERE etce_.ncontractid = var_nContractID;\n" +
                        "\t\t\tIF var_nRecordCount <= 0 THEN\n" +
                        "\t\t\t\tUPDATE XON_EPM.EPM_TBL_CONTRACT\n" +
                        "\t\t\t\tSET oid = oid\n" +
                        "\t\t\t\tWHERE XON_EPM.EPM_TBL_CONTRACT.OID = var_nContractID;\n" +
                        "\t\t\tEND IF;\n" +
                        "\t\t\tUPDATE XON_EPM.EPM_TB_CONTRACT_EX\n" +
                        "\t\t\tSET dFirstCheckTime = var_dFirstCheckTime\n" +
                        "\t\t\tWHERE XON_EPM.EPM_TB_CONTRACT_EX.NCONTRACTID = var_nContractID;\n" +
                        "\t\tEND IF;\n" +
                        "\tEND IF;\n" +
                        "END;";

        OracleStatementParser parser = new OracleStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();
        SQLStatement stmt = statementList.get(0);
        print(statementList);

        Assert.assertEquals(1, statementList.size());

        Assert.assertEquals("CREATE OR REPLACE TRIGGER XON_EPM.EPM_TG_FIRSTCHECK\n" +
                        "\tAFTER INSERT OR UPDATE\n" +
                        "\tON XON_EPM.EPM_TBL_FIRST_CERTIFICATION_AP\n" +
                        "\tFOR EACH ROW\n" +
                        "DECLARE\n" +
                        "\tvar_nContractID number;\n" +
                        "\tvar_dFirstCheckTime date;\n" +
                        "\tvar_nRecordCount number;\n" +
                        "\tvar_sState varchar2(16);\n" +
                        "BEGIN\n" +
                        "\tvar_sState := :NEW.State;\n" +
                        "\tvar_dFirstCheckTime := :NEW.Commiteddate;\n" +
                        "\tIF var_sState = 'approved' THEN\n" +
                        "\t\t-- 鏌ヨ\uE1D7鍚堝悓 ID\n" +
                        "\t\tSELECT etfca_.contractid\n" +
                        "\t\tINTO var_nContractID\n" +
                        "\t\tFROM XON_EPM.EPM_TBL_FIRST_CERTIFICATION etfc_, XON_EPM.EPM_TBL_FIRST_CHECK_APPLICATIO etfca_\n" +
                        "\t\tWHERE etfca_.oid = etfc_.applicationid\n" +
                        "\t\t\tAND etfc_.oid = :NEW.CERTIFICATIONID;\n" +
                        "\t\tIF var_nContractID > 0 THEN\n" +
                        "\t\t\t-- 鏌ヨ\uE1D7 EPM_TB_CONTRACT_EX 鏄\uE21A惁鏈夎\uE187褰�\n" +
                        "\t\t\tSELECT COUNT(*)\n" +
                        "\t\t\tINTO var_nRecordCount\n" +
                        "\t\t\tFROM XON_EPM.EPM_TB_CONTRACT_EX etce_\n" +
                        "\t\t\tWHERE etce_.ncontractid = var_nContractID;\n" +
                        "\t\t\tIF var_nRecordCount <= 0 THEN\n" +
                        "\t\t\t\tUPDATE XON_EPM.EPM_TBL_CONTRACT\n" +
                        "\t\t\t\tSET oid = oid\n" +
                        "\t\t\t\tWHERE XON_EPM.EPM_TBL_CONTRACT.OID = var_nContractID;\n" +
                        "\t\t\tEND IF;\n" +
                        "\t\t\tUPDATE XON_EPM.EPM_TB_CONTRACT_EX\n" +
                        "\t\t\tSET dFirstCheckTime = var_dFirstCheckTime\n" +
                        "\t\t\tWHERE XON_EPM.EPM_TB_CONTRACT_EX.NCONTRACTID = var_nContractID;\n" +
                        "\t\tEND IF;\n" +
                        "\tEND IF;\n" +
                        "END;",//
                            SQLUtils.toSQLString(stmt, JdbcConstants.ORACLE));

        Assert.assertEquals("CREATE OR REPLACE TRIGGER XON_EPM.EPM_TG_FIRSTCHECK\n" +
                        "\tAFTER INSERT OR UPDATE\n" +
                        "\tON XON_EPM.EPM_TBL_FIRST_CERTIFICATION_AP\n" +
                        "\tFOR EACH ROW\n" +
                        "DECLARE\n" +
                        "\tvar_nContractID number;\n" +
                        "\tvar_dFirstCheckTime date;\n" +
                        "\tvar_nRecordCount number;\n" +
                        "\tvar_sState varchar2(16);\n" +
                        "BEGIN\n" +
                        "\tSET var_sState := :NEW.State;\n" +
                        "\tSET var_dFirstCheckTime := :NEW.Commiteddate;\n" +
                        "\tIF var_sState = 'approved' THEN\n" +
                        "\t\t-- 鏌ヨ\uE1D7鍚堝悓 ID\n" +
                        "\t\tSELECT etfca_.contractid\n" +
                        "\t\tINTO var_nContractID\n" +
                        "\t\tFROM XON_EPM.EPM_TBL_FIRST_CERTIFICATION etfc_, XON_EPM.EPM_TBL_FIRST_CHECK_APPLICATIO etfca_\n" +
                        "\t\tWHERE etfca_.oid = etfc_.applicationid\n" +
                        "\t\t\tAND etfc_.oid = :NEW.CERTIFICATIONID;\n" +
                        "\t\tIF var_nContractID > 0 THEN\n" +
                        "\t\t\t-- 鏌ヨ\uE1D7 EPM_TB_CONTRACT_EX 鏄\uE21A惁鏈夎\uE187褰�\n" +
                        "\t\t\tSELECT COUNT(*)\n" +
                        "\t\t\tINTO var_nRecordCount\n" +
                        "\t\t\tFROM XON_EPM.EPM_TB_CONTRACT_EX etce_\n" +
                        "\t\t\tWHERE etce_.ncontractid = var_nContractID;\n" +
                        "\t\t\tIF var_nRecordCount <= 0 THEN\n" +
                        "\t\t\t\tUPDATE XON_EPM.EPM_TBL_CONTRACT\n" +
                        "\t\t\t\tSET oid = oid\n" +
                        "\t\t\t\tWHERE XON_EPM.EPM_TBL_CONTRACT.OID = var_nContractID;\n" +
                        "\t\t\tEND IF;\n" +
                        "\t\t\tUPDATE XON_EPM.EPM_TB_CONTRACT_EX\n" +
                        "\t\t\tSET dFirstCheckTime = var_dFirstCheckTime\n" +
                        "\t\t\tWHERE XON_EPM.EPM_TB_CONTRACT_EX.NCONTRACTID = var_nContractID;\n" +
                        "\t\tEND IF;\n" +
                        "\tEND IF;\n" +
                        "END;",//
                SQLUtils.toSQLString(stmt, JdbcConstants.POSTGRESQL));

        OracleSchemaStatVisitor visitor = new OracleSchemaStatVisitor();
        stmt.accept(visitor);

        System.out.println("Tables : " + visitor.getTables());
        System.out.println("fields : " + visitor.getColumns());
        System.out.println("coditions : " + visitor.getConditions());
        System.out.println("relationships : " + visitor.getRelationships());
        System.out.println("orderBy : " + visitor.getOrderByColumns());

        Assert.assertEquals(1, visitor.getTables().size());

        // Assert.assertTrue(visitor.getTables().containsKey(new TableStat.Name("cdc.en_complaint_ipr_stat_fdt0")));

        Assert.assertEquals(0, visitor.getColumns().size());

        // Assert.assertTrue(visitor.getColumns().contains(new TableStat.Column("pivot_table", "*")));
        // Assert.assertTrue(visitor.getColumns().contains(new TableStat.Column("pivot_table", "YEAR")));
        // Assert.assertTrue(visitor.getColumns().contains(new TableStat.Column("pivot_table", "order_mode")));
    }
}
