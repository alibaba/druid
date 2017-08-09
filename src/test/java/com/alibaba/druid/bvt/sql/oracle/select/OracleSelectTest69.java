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

public class OracleSelectTest69 extends OracleTest {

    public void test_0() throws Exception {
        String sql = //
                "CREATE TABLE \"SC01\".\"TB01\" \n" +
                        "   (\t\"KPPZXH\" VARCHAR2(80) NOT NULL ENABLE, \n" +
                        "\t\"NSRDZDAH\" VARCHAR2(80) NOT NULL ENABLE, \n" +
                        "\t\"NSRSBH\" VARCHAR2(80) DEFAULT '', \n" +
                        "\t\"NSRMC\" VARCHAR2(160) DEFAULT '', \n" +
                        "\t\"KPFDZJDH\" VARCHAR2(400) DEFAULT '', \n" +
                        "\t\"KPFYHJZH\" VARCHAR2(400) DEFAULT '', \n" +
                        "\t\"FPZLDM\" VARCHAR2(20) NOT NULL ENABLE, \n" +
                        "\t\"DZFPHM\" VARCHAR2(40), \n" +
                        "\t\"KPRQ\" DATE NOT NULL ENABLE, \n" +
                        "\t\"HYFL\" VARCHAR2(40) NOT NULL ENABLE, \n" +
                        "\t\"SPFNSRMC\" VARCHAR2(400) DEFAULT '', \n" +
                        "\t\"SPFYHJZH\" VARCHAR2(400) DEFAULT '', \n" +
                        "\t\"HJ\" NUMBER(20,2) DEFAULT 0, \n" +
                        "\t\"SL\" NUMBER(20,2) DEFAULT 0, \n" +
                        "\t\"SE\" NUMBER(20,2) DEFAULT 0, \n" +
                        "\t\"KPR\" VARCHAR2(40) DEFAULT '', \n" +
                        "\t\"SKR\" VARCHAR2(40) DEFAULT '', \n" +
                        "\t\"LRRQ\" DATE, \n" +
                        "\t\"ZFBZ\" CHAR(1) DEFAULT 'N' NOT NULL ENABLE, \n" +
                        "\t\"YJBZ\" CHAR(1) DEFAULT 'N' NOT NULL ENABLE, \n" +
                        "\t\"GHFQYLX\" VARCHAR2(20), \n" +
                        "\t\"SPFDZJDH\" VARCHAR2(400), \n" +
                        "\t\"SPFNSRSBH\" VARCHAR2(60), \n" +
                        "\t\"FPCJBZ\" CHAR(1), \n" +
                        "\t\"FPBZ\" CHAR(1) DEFAULT '0', \n" +
                        "\t\"JSJE\" NUMBER(20,2), \n" +
                        "\t\"ZJLX\" VARCHAR2(20), \n" +
                        "\t\"KJFSBH\" VARCHAR2(40), \n" +
                        "\t\"SQBHM\" VARCHAR2(160), \n" +
                        "\t\"WSPZHM\" VARCHAR2(80), \n" +
                        "\t\"LZFPHM\" VARCHAR2(40), \n" +
                        "\t\"HCJE\" NUMBER(20,2) DEFAULT 0, \n" +
                        "\t\"XHFQYLX_DM\" VARCHAR2(20), \n" +
                        "\t\"YNSE\" NUMBER(20,2), \n" +
                        "\t\"BZ\" VARCHAR2(600), \n" +
                        "\t\"ZFYY\" VARCHAR2(20), \n" +
                        "\t\"HCYY\" VARCHAR2(20), \n" +
                        "\t\"ZFRQ\" DATE, \n" +
                        "\t\"FPZL\" VARCHAR2(20), \n" +
                        "\t\"SWJG_DM\" VARCHAR2(40), \n" +
                        "\t\"HTHM\" VARCHAR2(160), \n" +
                        "\t\"XYZHS\" VARCHAR2(160), \n" +
                        "\t\"JGRQ\" DATE, \n" +
                        "\t\"SHBZ\" VARCHAR2(40), \n" +
                        "\t\"BIZHONG\" VARCHAR2(40), \n" +
                        "\t\"DKJE\" NUMBER(20,2), \n" +
                        "\t\"DSWSPZHM\" VARCHAR2(80), \n" +
                        "\t\"DKYF\" DATE, \n" +
                        "\t\"JGTK\" VARCHAR2(200), \n" +
                        "\t\"HXDH\" VARCHAR2(80), \n" +
                        "\t\"HL\" NUMBER(20,6), \n" +
                        "\t\"LAJE\" NUMBER(20,2), \n" +
                        "\t\"HC_LCBZ\" CHAR(1), \n" +
                        "\t\"SPR_BHYY\" VARCHAR2(400), \n" +
                        "\t\"CONTENT\" BLOB, \n" +
                        "\t\"ZF_LCBZ\" CHAR(1), \n" +
                        "\t\"FPDM\" VARCHAR2(40), \n" +
                        "\t\"FPHM\" VARCHAR2(40), \n" +
                        "\t\"GHFSJH\" VARCHAR2(30), \n" +
                        "\t\"GHFDZYX\" VARCHAR2(100), \n" +
                        "\t\"GHFZH\" VARCHAR2(100), \n" +
                        "\t\"KPLX\" VARCHAR2(2), \n" +
                        "\t\"JSHJ\" NUMBER(18,2), \n" +
                        "\t\"HJJE\" NUMBER(18,2), \n" +
                        "\t\"HJSE\" NUMBER(18,2), \n" +
                        "\t\"YFPDM\" VARCHAR2(24), \n" +
                        "\t\"YFPHM\" VARCHAR2(16), \n" +
                        "\t\"FHR\" VARCHAR2(16), \n" +
                        "\t\"EWM\" VARCHAR2(4000), \n" +
                        "\t\"FPMW\" VARCHAR2(224), \n" +
                        "\t\"JYM\" VARCHAR2(40), \n" +
                        "\t\"JQBH\" VARCHAR2(24), \n" +
                        "\t\"ERPTID\" VARCHAR2(80), \n" +
                        "\t\"DSPTDM\" VARCHAR2(40), \n" +
                        "\t CONSTRAINT \"PK_FP_TY_KP_MFP1\" PRIMARY KEY (\"KPPZXH\")\n" +
                        "  USING INDEX PCTFREE 10 INITRANS 2 MAXTRANS 255 COMPUTE STATISTICS NOLOGGING \n" +
                        "  STORAGE(INITIAL 40960 NEXT 1048576 MINEXTENTS 1 MAXEXTENTS 2147483645\n" +
                        "  PCTINCREASE 0 FREELISTS 1 FREELIST GROUPS 1\n" +
                        "  BUFFER_POOL DEFAULT FLASH_CACHE DEFAULT CELL_FLASH_CACHE DEFAULT)\n" +
                        "  TABLESPACE \"DZFP_HWXS1_IDX\"  ENABLE\n" +
                        "   ) SEGMENT CREATION IMMEDIATE \n" +
                        "  PCTFREE 10 PCTUSED 40 INITRANS 1 MAXTRANS 255 \n" +
                        " NOCOMPRESS LOGGING\n" +
                        "  STORAGE(INITIAL 40960 NEXT 1048576 MINEXTENTS 1 MAXEXTENTS 2147483645\n" +
                        "  PCTINCREASE 0 FREELISTS 1 FREELIST GROUPS 1\n" +
                        "  BUFFER_POOL DEFAULT FLASH_CACHE DEFAULT CELL_FLASH_CACHE DEFAULT)\n" +
                        "  TABLESPACE \"DZFP_HWXS1_IDX\" \n" +
                        " LOB (\"CONTENT\") STORE AS BASICFILE \"DZFP_HWXS1_IDX\"(\n" +
                        "  TABLESPACE \"DZFP_HWXS1_IDX\" ENABLE STORAGE IN ROW CHUNK 8192 PCTVERSION 10\n" +
                        "  NOCACHE LOGGING \n" +
                        "  STORAGE(INITIAL 1048576 NEXT 1048576 MINEXTENTS 1 MAXEXTENTS 2147483645\n" +
                        "  PCTINCREASE 0 FREELISTS 1 FREELIST GROUPS 1\n" +
                        "  BUFFER_POOL DEFAULT FLASH_CACHE DEFAULT CELL_FLASH_CACHE DEFAULT)) "; //

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

        assertEquals(1, visitor.getTables().size());

        assertEquals(74, visitor.getColumns().size());

        {
            String text = SQLUtils.toOracleString(stmt);

            assertEquals("CREATE TABLE \"SC01\".\"TB01\" (\n" +
                    "\t\"KPPZXH\" VARCHAR2(80) NOT NULL ENABLE,\n" +
                    "\t\"NSRDZDAH\" VARCHAR2(80) NOT NULL ENABLE,\n" +
                    "\t\"NSRSBH\" VARCHAR2(80) DEFAULT NULL,\n" +
                    "\t\"NSRMC\" VARCHAR2(160) DEFAULT NULL,\n" +
                    "\t\"KPFDZJDH\" VARCHAR2(400) DEFAULT NULL,\n" +
                    "\t\"KPFYHJZH\" VARCHAR2(400) DEFAULT NULL,\n" +
                    "\t\"FPZLDM\" VARCHAR2(20) NOT NULL ENABLE,\n" +
                    "\t\"DZFPHM\" VARCHAR2(40),\n" +
                    "\t\"KPRQ\" DATE NOT NULL ENABLE,\n" +
                    "\t\"HYFL\" VARCHAR2(40) NOT NULL ENABLE,\n" +
                    "\t\"SPFNSRMC\" VARCHAR2(400) DEFAULT NULL,\n" +
                    "\t\"SPFYHJZH\" VARCHAR2(400) DEFAULT NULL,\n" +
                    "\t\"HJ\" NUMBER(20, 2) DEFAULT 0,\n" +
                    "\t\"SL\" NUMBER(20, 2) DEFAULT 0,\n" +
                    "\t\"SE\" NUMBER(20, 2) DEFAULT 0,\n" +
                    "\t\"KPR\" VARCHAR2(40) DEFAULT NULL,\n" +
                    "\t\"SKR\" VARCHAR2(40) DEFAULT NULL,\n" +
                    "\t\"LRRQ\" DATE,\n" +
                    "\t\"ZFBZ\" CHAR(1) DEFAULT 'N' NOT NULL ENABLE,\n" +
                    "\t\"YJBZ\" CHAR(1) DEFAULT 'N' NOT NULL ENABLE,\n" +
                    "\t\"GHFQYLX\" VARCHAR2(20),\n" +
                    "\t\"SPFDZJDH\" VARCHAR2(400),\n" +
                    "\t\"SPFNSRSBH\" VARCHAR2(60),\n" +
                    "\t\"FPCJBZ\" CHAR(1),\n" +
                    "\t\"FPBZ\" CHAR(1) DEFAULT '0',\n" +
                    "\t\"JSJE\" NUMBER(20, 2),\n" +
                    "\t\"ZJLX\" VARCHAR2(20),\n" +
                    "\t\"KJFSBH\" VARCHAR2(40),\n" +
                    "\t\"SQBHM\" VARCHAR2(160),\n" +
                    "\t\"WSPZHM\" VARCHAR2(80),\n" +
                    "\t\"LZFPHM\" VARCHAR2(40),\n" +
                    "\t\"HCJE\" NUMBER(20, 2) DEFAULT 0,\n" +
                    "\t\"XHFQYLX_DM\" VARCHAR2(20),\n" +
                    "\t\"YNSE\" NUMBER(20, 2),\n" +
                    "\t\"BZ\" VARCHAR2(600),\n" +
                    "\t\"ZFYY\" VARCHAR2(20),\n" +
                    "\t\"HCYY\" VARCHAR2(20),\n" +
                    "\t\"ZFRQ\" DATE,\n" +
                    "\t\"FPZL\" VARCHAR2(20),\n" +
                    "\t\"SWJG_DM\" VARCHAR2(40),\n" +
                    "\t\"HTHM\" VARCHAR2(160),\n" +
                    "\t\"XYZHS\" VARCHAR2(160),\n" +
                    "\t\"JGRQ\" DATE,\n" +
                    "\t\"SHBZ\" VARCHAR2(40),\n" +
                    "\t\"BIZHONG\" VARCHAR2(40),\n" +
                    "\t\"DKJE\" NUMBER(20, 2),\n" +
                    "\t\"DSWSPZHM\" VARCHAR2(80),\n" +
                    "\t\"DKYF\" DATE,\n" +
                    "\t\"JGTK\" VARCHAR2(200),\n" +
                    "\t\"HXDH\" VARCHAR2(80),\n" +
                    "\t\"HL\" NUMBER(20, 6),\n" +
                    "\t\"LAJE\" NUMBER(20, 2),\n" +
                    "\t\"HC_LCBZ\" CHAR(1),\n" +
                    "\t\"SPR_BHYY\" VARCHAR2(400),\n" +
                    "\t\"CONTENT\" BLOB,\n" +
                    "\t\"ZF_LCBZ\" CHAR(1),\n" +
                    "\t\"FPDM\" VARCHAR2(40),\n" +
                    "\t\"FPHM\" VARCHAR2(40),\n" +
                    "\t\"GHFSJH\" VARCHAR2(30),\n" +
                    "\t\"GHFDZYX\" VARCHAR2(100),\n" +
                    "\t\"GHFZH\" VARCHAR2(100),\n" +
                    "\t\"KPLX\" VARCHAR2(2),\n" +
                    "\t\"JSHJ\" NUMBER(18, 2),\n" +
                    "\t\"HJJE\" NUMBER(18, 2),\n" +
                    "\t\"HJSE\" NUMBER(18, 2),\n" +
                    "\t\"YFPDM\" VARCHAR2(24),\n" +
                    "\t\"YFPHM\" VARCHAR2(16),\n" +
                    "\t\"FHR\" VARCHAR2(16),\n" +
                    "\t\"EWM\" VARCHAR2(4000),\n" +
                    "\t\"FPMW\" VARCHAR2(224),\n" +
                    "\t\"JYM\" VARCHAR2(40),\n" +
                    "\t\"JQBH\" VARCHAR2(24),\n" +
                    "\t\"ERPTID\" VARCHAR2(80),\n" +
                    "\t\"DSPTDM\" VARCHAR2(40),\n" +
                    "\tCONSTRAINT \"PK_FP_TY_KP_MFP1\" PRIMARY KEY (\"KPPZXH\")\n" +
                    "\t\tUSING INDEX\n" +
                    "\t\tPCTFREE 10\n" +
                    "\t\tINITRANS 2\n" +
                    "\t\tMAXTRANS 255\n" +
                    "\t\tNOLOGGING\n" +
                    "\t\tTABLESPACE \"DZFP_HWXS1_IDX\"\n" +
                    "\t\tSTORAGE (\n" +
                    "\t\t\tINITIAL 40960\n" +
                    "\t\t\tNEXT 1048576\n" +
                    "\t\t\tMINEXTENTS 1\n" +
                    "\t\t\tMAXEXTENTS 2147483645\n" +
                    "\t\t\tPCTINCREASE 0\n" +
                    "\t\t\tFREELISTS 1\n" +
                    "\t\t\tFREELIST GROUPS 1\n" +
                    "\t\t\tBUFFER_POOL DEFAULT\n" +
                    "\t\t\tFLASH_CACHE DEFAULT\n" +
                    "\t\t\tCELL_FLASH_CACHE DEFAULT\n" +
                    "\t\t)\n" +
                    "\t\tCOMPUTE STATISTICS\n" +
                    "\t\tENABLE\n" +
                    ")\n" +
                    "PCTFREE 10\n" +
                    "PCTUSED 40\n" +
                    "INITRANS 1\n" +
                    "MAXTRANS 255\n" +
                    "NOCOMPRESS\n" +
                    "LOGGING\n" +
                    "TABLESPACE \"DZFP_HWXS1_IDX\"\n" +
                    "STORAGE (\n" +
                    "\tINITIAL 40960\n" +
                    "\tNEXT 1048576\n" +
                    "\tMINEXTENTS 1\n" +
                    "\tMAXEXTENTS 2147483645\n" +
                    "\tPCTINCREASE 0\n" +
                    "\tFREELISTS 1\n" +
                    "\tFREELIST GROUPS 1\n" +
                    "\tBUFFER_POOL DEFAULT\n" +
                    "\tFLASH_CACHE DEFAULT\n" +
                    "\tCELL_FLASH_CACHE DEFAULT\n" +
                    ")\n" +
                    "LOB (\"CONTENT\") STORE AS BASICFILE \"DZFP_HWXS1_IDX\" (\n" +
                    "\tLOGGING\n" +
                    "\tTABLESPACE \"DZFP_HWXS1_IDX\"\n" +
                    "\tSTORAGE (\n" +
                    "\t\tINITIAL 1048576\n" +
                    "\t\tNEXT 1048576\n" +
                    "\t\tMINEXTENTS 1\n" +
                    "\t\tMAXEXTENTS 2147483645\n" +
                    "\t\tPCTINCREASE 0\n" +
                    "\t\tFREELISTS 1\n" +
                    "\t\tFREELIST GROUPS 1\n" +
                    "\t\tBUFFER_POOL DEFAULT\n" +
                    "\t\tFLASH_CACHE DEFAULT\n" +
                    "\t\tCELL_FLASH_CACHE DEFAULT\n" +
                    "\t)\n" +
                    "\tENABLE STORAGE IN ROW\n" +
                    "\tCHUNK 8192\n" +
                    "\tNOCACHE\n" +
                    ")", text);
        }
        // Assert.assertTrue(visitor.getColumns().contains(new TableStat.Column("acduser.vw_acd_info", "xzqh")));

        // Assert.assertTrue(visitor.getOrderByColumns().contains(new TableStat.Column("employees", "last_name")));
    }
}
