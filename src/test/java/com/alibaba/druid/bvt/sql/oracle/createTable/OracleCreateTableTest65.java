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
import com.alibaba.druid.sql.dialect.oracle.parser.OracleStatementParser;
import com.alibaba.druid.sql.dialect.oracle.visitor.OracleSchemaStatVisitor;
import com.alibaba.druid.util.JdbcConstants;
import org.junit.Assert;

import java.util.List;

public class OracleCreateTableTest65 extends OracleTest {

    public void test_types() throws Exception {
        String sql = //
        "   CREATE TABLE \"SC_001\".\"TB_001\" \n" +
                "   (\t\"ID_\" NVARCHAR2(64) NOT NULL ENABLE, \n" +
                "\t\"PROC_DEF_ID_\" NVARCHAR2(64), \n" +
                "\t\"TASK_DEF_KEY_\" NVARCHAR2(255), \n" +
                "\t\"PROC_INST_ID_\" NVARCHAR2(64), \n" +
                "\t\"EXECUTION_ID_\" NVARCHAR2(64), \n" +
                "\t\"PARENT_TASK_ID_\" NVARCHAR2(64), \n" +
                "\t\"NAME_\" NVARCHAR2(255), \n" +
                "\t\"DESCRIPTION_\" NVARCHAR2(2000), \n" +
                "\t\"OWNER_\" NVARCHAR2(255), \n" +
                "\t\"ASSIGNEE_\" NVARCHAR2(255), \n" +
                "\t\"START_TIME_\" TIMESTAMP (6) NOT NULL ENABLE, \n" +
                "\t\"CLAIM_TIME_\" TIMESTAMP (6), \n" +
                "\t\"END_TIME_\" TIMESTAMP (6), \n" +
                "\t\"DURATION_\" NUMBER(19,0), \n" +
                "\t\"DELETE_REASON_\" NVARCHAR2(2000), \n" +
                "\t\"PRIORITY_\" NUMBER(*,0), \n" +
                "\t\"DUE_DATE_\" TIMESTAMP (6), \n" +
                "\t\"FORM_KEY_\" NVARCHAR2(255), \n" +
                "\t PRIMARY KEY (\"ID_\")\n" +
                "  USING INDEX REVERSE PCTFREE 10 INITRANS 2 MAXTRANS 255 COMPUTE STATISTICS \n" +
                "  STORAGE(INITIAL 196608 NEXT 8388608 MINEXTENTS 1 MAXEXTENTS 2147483645\n" +
                "  PCTINCREASE 0 FREELISTS 1 FREELIST GROUPS 1\n" +
                "  BUFFER_POOL DEFAULT FLASH_CACHE DEFAULT CELL_FLASH_CACHE DEFAULT)\n" +
                "  TABLESPACE \"RDP_IDX\"  ENABLE\n" +
                "   ) SEGMENT CREATION IMMEDIATE \n" +
                "  PCTFREE 10 PCTUSED 40 INITRANS 1 MAXTRANS 255 \n" +
                " NOCOMPRESS LOGGING\n" +
                "  STORAGE(INITIAL 2097152 NEXT 8388608 MINEXTENTS 1 MAXEXTENTS 2147483645\n" +
                "  PCTINCREASE 0 FREELISTS 1 FREELIST GROUPS 1\n" +
                "  BUFFER_POOL DEFAULT FLASH_CACHE DEFAULT CELL_FLASH_CACHE DEFAULT)\n" +
                "  TABLESPACE \"RDP_DATA\"   ";

        OracleStatementParser parser = new OracleStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();
        SQLStatement stmt = statementList.get(0);
        print(statementList);

        Assert.assertEquals(1, statementList.size());

        Assert.assertEquals("CREATE TABLE \"SC_001\".\"TB_001\" (\n" +
                        "\t\"ID_\" NVARCHAR2(64) NOT NULL ENABLE,\n" +
                        "\t\"PROC_DEF_ID_\" NVARCHAR2(64),\n" +
                        "\t\"TASK_DEF_KEY_\" NVARCHAR2(255),\n" +
                        "\t\"PROC_INST_ID_\" NVARCHAR2(64),\n" +
                        "\t\"EXECUTION_ID_\" NVARCHAR2(64),\n" +
                        "\t\"PARENT_TASK_ID_\" NVARCHAR2(64),\n" +
                        "\t\"NAME_\" NVARCHAR2(255),\n" +
                        "\t\"DESCRIPTION_\" NVARCHAR2(2000),\n" +
                        "\t\"OWNER_\" NVARCHAR2(255),\n" +
                        "\t\"ASSIGNEE_\" NVARCHAR2(255),\n" +
                        "\t\"START_TIME_\" TIMESTAMP(6) NOT NULL ENABLE,\n" +
                        "\t\"CLAIM_TIME_\" TIMESTAMP(6),\n" +
                        "\t\"END_TIME_\" TIMESTAMP(6),\n" +
                        "\t\"DURATION_\" NUMBER(19, 0),\n" +
                        "\t\"DELETE_REASON_\" NVARCHAR2(2000),\n" +
                        "\t\"PRIORITY_\" NUMBER(*, 0),\n" +
                        "\t\"DUE_DATE_\" TIMESTAMP(6),\n" +
                        "\t\"FORM_KEY_\" NVARCHAR2(255),\n" +
                        "\tPRIMARY KEY (\"ID_\")\n" +
                        "\t\tUSING INDEX\n" +
                        "\t\tPCTFREE 10\n" +
                        "\t\tINITRANS 2\n" +
                        "\t\tMAXTRANS 255\n" +
                        "\t\tTABLESPACE \"RDP_IDX\"\n" +
                        "\t\tSTORAGE (\n" +
                        "\t\t\tINITIAL 196608\n" +
                        "\t\t\tNEXT 8388608\n" +
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
                        "\t\tREVERSE\n" +
                        ")\n" +
                        "PCTFREE 10\n" +
                        "PCTUSED 40\n" +
                        "INITRANS 1\n" +
                        "MAXTRANS 255\n" +
                        "NOCOMPRESS\n" +
                        "LOGGING\n" +
                        "TABLESPACE \"RDP_DATA\"\n" +
                        "STORAGE (\n" +
                        "\tINITIAL 2097152\n" +
                        "\tNEXT 8388608\n" +
                        "\tMINEXTENTS 1\n" +
                        "\tMAXEXTENTS 2147483645\n" +
                        "\tPCTINCREASE 0\n" +
                        "\tFREELISTS 1\n" +
                        "\tFREELIST GROUPS 1\n" +
                        "\tBUFFER_POOL DEFAULT\n" +
                        "\tFLASH_CACHE DEFAULT\n" +
                        "\tCELL_FLASH_CACHE DEFAULT\n" +
                        ")",//
                            SQLUtils.toSQLString(stmt, JdbcConstants.ORACLE));

        OracleSchemaStatVisitor visitor = new OracleSchemaStatVisitor();
        stmt.accept(visitor);

        System.out.println("Tables : " + visitor.getTables());
        System.out.println("fields : " + visitor.getColumns());
        System.out.println("coditions : " + visitor.getConditions());
        System.out.println("relationships : " + visitor.getRelationships());
        System.out.println("orderBy : " + visitor.getOrderByColumns());

        Assert.assertEquals(1, visitor.getTables().size());

        Assert.assertEquals(18, visitor.getColumns().size());

        Assert.assertTrue(visitor.containsColumn("SC_001.TB_001", "ID_"));
    }
}
