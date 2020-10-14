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

public class OracleCreateTableTest71 extends OracleTest {

    public void test_types() throws Exception {
        String sql = //
        " CREATE TABLE \"CMIS\".\"DR$IDX_FULL_BIZ_TABLE_NAME$N\" \n" +
                "   (\t\"NLT_DOCID\" NUMBER(38,0), \n" +
                "\t\"NLT_MARK\" CHAR(1) NOT NULL ENABLE, \n" +
                "\t PRIMARY KEY (\"NLT_DOCID\") ENABLE\n" +
                "   ) ORGANIZATION INDEX NOCOMPRESS PCTFREE 10 INITRANS 2 MAXTRANS 255 LOGGING\n" +
                "  STORAGE(INITIAL 65536 NEXT 1048576 MINEXTENTS 1 MAXEXTENTS 2147483645\n" +
                "  PCTINCREASE 0 FREELISTS 1 FREELIST GROUPS 1\n" +
                "  BUFFER_POOL DEFAULT FLASH_CACHE DEFAULT CELL_FLASH_CACHE DEFAULT)\n" +
                "  TABLESPACE \"CMISTBS\" \n" +
                " PCTTHRESHOLD 50\n" +
                "  MONITORING   ";

        List<SQLStatement> statementList = SQLUtils.parseStatements(sql, JdbcConstants.ORACLE);
        SQLStatement stmt = statementList.get(0);
        print(statementList);

        assertEquals(1, statementList.size());
//
        assertEquals("CREATE TABLE \"CMIS\".\"DR$IDX_FULL_BIZ_TABLE_NAME$N\" (\n" +
                        "\t\"NLT_DOCID\" NUMBER(38, 0),\n" +
                        "\t\"NLT_MARK\" CHAR(1) NOT NULL ENABLE,\n" +
                        "\tPRIMARY KEY (\"NLT_DOCID\") ENABLE\n" +
                        ")\n" +
                        "ORGANIZATION INDEX\n" +
                        "\tPCTFREE 10\n" +
                        "\tINITRANS 2\n" +
                        "\tMAXTRANS 255\n" +
                        "\tNOCOMPRESS\n" +
                        "\tLOGGING\n" +
                        "\tTABLESPACE \"CMISTBS\"\n" +
                        "\tSTORAGE (\n" +
                        "\t\tINITIAL 65536\n" +
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
                        "\tPCTTHRESHOLD 50\n" +
                        "MONITORING",//
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
