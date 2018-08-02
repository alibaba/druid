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

public class OracleCreateTableTest74 extends OracleTest {

    public void test_types() throws Exception {
        String sql = //
        "CREATE TABLE \"WEB_FPXX\".\"XX_MX_0901\" \n" +
                "   (\t\"NSRSBH\" VARCHAR2(20) NOT NULL ENABLE, \n" +
                "\t\"SBRQ\" DATE NOT NULL ENABLE, \n" +
                "\t\"TBRQ\" DATE, \n" +
                "\t\"SSSQQ\" DATE NOT NULL ENABLE, \n" +
                "\t\"FPDM\" CHAR(10) NOT NULL ENABLE, \n" +
                "\t\"FPHM\" CHAR(8) NOT NULL ENABLE, \n" +
                "\t\"KPRQ\" DATE, \n" +
                "\t\"JE\" NUMBER(14,2) NOT NULL ENABLE, \n" +
                "\t\"SE\" NUMBER(14,2) NOT NULL ENABLE, \n" +
                "\t\"GHF_NSRSBH\" VARCHAR2(20) NOT NULL ENABLE, \n" +
                "\t\"ZF_BZ\" CHAR(1) NOT NULL ENABLE, \n" +
                "\t\"SWJG_DM\" CHAR(11) NOT NULL ENABLE, \n" +
                "\t\"BZ\" CHAR(1), \n" +
                "\t\"FP_LB\" VARCHAR2(4), \n" +
                "\t CONSTRAINT \"PK_XX_MX_0901\" PRIMARY KEY (\"FPDM\", \"FPHM\")\n" +
                "  USING INDEX PCTFREE 10 INITRANS 2 MAXTRANS 255 COMPUTE STATISTICS \n" +
                "  STORAGE(INITIAL 1048576 NEXT 1048576 MINEXTENTS 1 MAXEXTENTS 2147483645\n" +
                "  PCTINCREASE 0 FREELISTS 1 FREELIST GROUPS 1\n" +
                "  BUFFER_POOL DEFAULT FLASH_CACHE DEFAULT CELL_FLASH_CACHE DEFAULT)\n" +
                "  TABLESPACE \"FPXX_IDX\"  ENABLE\n" +
                "   ) SEGMENT CREATION IMMEDIATE \n" +
                "  PCTFREE 10 PCTUSED 40 INITRANS 2 MAXTRANS 255 \n" +
                " NOCOMPRESS NOLOGGING\n" +
                "  STORAGE(INITIAL 24576 NEXT 1048576 MINEXTENTS 1 MAXEXTENTS 2147483645\n" +
                "  PCTINCREASE 0 FREELISTS 1 FREELIST GROUPS 1\n" +
                "  BUFFER_POOL DEFAULT FLASH_CACHE DEFAULT CELL_FLASH_CACHE DEFAULT)\n" +
                "  TABLESPACE \"FPXX_DAT\" \n" +
                "   CACHE PARALLEL ";

        List<SQLStatement> statementList = SQLUtils.parseStatements(sql, JdbcConstants.ORACLE);
        SQLStatement stmt = statementList.get(0);
        print(statementList);

        assertEquals(1, statementList.size());
//
        assertEquals("CREATE TABLE \"WEB_FPXX\".\"XX_MX_0901\" (\n" +
                        "\t\"NSRSBH\" VARCHAR2(20) NOT NULL ENABLE,\n" +
                        "\t\"SBRQ\" DATE NOT NULL ENABLE,\n" +
                        "\t\"TBRQ\" DATE,\n" +
                        "\t\"SSSQQ\" DATE NOT NULL ENABLE,\n" +
                        "\t\"FPDM\" CHAR(10) NOT NULL ENABLE,\n" +
                        "\t\"FPHM\" CHAR(8) NOT NULL ENABLE,\n" +
                        "\t\"KPRQ\" DATE,\n" +
                        "\t\"JE\" NUMBER(14, 2) NOT NULL ENABLE,\n" +
                        "\t\"SE\" NUMBER(14, 2) NOT NULL ENABLE,\n" +
                        "\t\"GHF_NSRSBH\" VARCHAR2(20) NOT NULL ENABLE,\n" +
                        "\t\"ZF_BZ\" CHAR(1) NOT NULL ENABLE,\n" +
                        "\t\"SWJG_DM\" CHAR(11) NOT NULL ENABLE,\n" +
                        "\t\"BZ\" CHAR(1),\n" +
                        "\t\"FP_LB\" VARCHAR2(4),\n" +
                        "\tCONSTRAINT \"PK_XX_MX_0901\" PRIMARY KEY (\"FPDM\", \"FPHM\")\n" +
                        "\t\tUSING INDEX\n" +
                        "\t\tPCTFREE 10\n" +
                        "\t\tINITRANS 2\n" +
                        "\t\tMAXTRANS 255\n" +
                        "\t\tTABLESPACE \"FPXX_IDX\"\n" +
                        "\t\tSTORAGE (\n" +
                        "\t\t\tINITIAL 1048576\n" +
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
                        "INITRANS 2\n" +
                        "MAXTRANS 255\n" +
                        "NOCOMPRESS\n" +
                        "NOLOGGING\n" +
                        "TABLESPACE \"FPXX_DAT\"\n" +
                        "STORAGE (\n" +
                        "\tINITIAL 24576\n" +
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
                        "PARALLEL\n" +
                        "CACHE",//
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
