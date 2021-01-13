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
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.oracle.parser.OracleStatementParser;
import com.alibaba.druid.sql.dialect.oracle.visitor.OracleSchemaStatVisitor;

import java.util.List;

public class OracleCreateTableTest92 extends OracleTest {

    public void test_0() throws Exception {
        String sql = //
                "CREATE TABLE \"ACCOUNT\".\"BROADBAND_USERS_RESULT\" \n" +
                        "   (    \"HOME_CITY\" NUMBER(3,0) NOT NULL ENABLE, \n" +
                        "    \"HOME_COUNTY\" NUMBER(3,0) NOT NULL ENABLE, \n" +
                        "    \"MONTH_KEY\" NUMBER(2,0) NOT NULL ENABLE, \n" +
                        "    \"MONTH\" NUMBER(2,0) NOT NULL ENABLE, \n" +
                        "    \"BROAD_USER_ID\" NUMBER(15,0) NOT NULL ENABLE, \n" +
                        "    \"PAYMENT_HOME_CITY\" NUMBER(3,0) NOT NULL ENABLE, \n" +
                        "    \"PAYMENT_HOME_COUNTY\" NUMBER(3,0) NOT NULL ENABLE, \n" +
                        "    \"PAYMENT_USER_ID\" NUMBER(15,0) NOT NULL ENABLE, \n" +
                        "    \"CONNECT_TYPE\" NUMBER(2,0), \n" +
                        "    \"PRODUCT_ID\" NUMBER(12,0) NOT NULL ENABLE, \n" +
                        "    \"INURE_TIME\" DATE, \n" +
                        "    \"EXPIRE_TIME\" DATE, \n" +
                        "    \"BAND_WIDTH\" NUMBER(8,0) NOT NULL ENABLE, \n" +
                        "     CONSTRAINT \"PK_BROADBAND_USERS_RESULT\" PRIMARY KEY (\"BROAD_USER_ID\", \"PAYMENT_USER_ID\", \"PAYMENT_HOME_CITY\", \"MONTH_KEY\")\n" +
                        "  USING INDEX PCTFREE 10 INITRANS 20 MAXTRANS 255 \n" +
                        "  STORAGE(\n" +
                        "  BUFFER_POOL DEFAULT FLASH_CACHE DEFAULT CELL_FLASH_CACHE DEFAULT)\n" +
                        "  TABLESPACE \"IDX_ACCOUNT_DATA_TS\"  LOCAL\n" +
                        " (PARTITION \"FZ\" \n" +
                        "PCTFREE 10 INITRANS 20 MAXTRANS 255 \n" +
                        "  STORAGE(\n" +
                        "  BUFFER_POOL DEFAULT FLASH_CACHE DEFAULT CELL_FLASH_CACHE DEFAULT)\n" +
                        "  TABLESPACE \"IDX_ACCOUNT_DATA_TS\" \n" +
                        " ( SUBPARTITION \"FZ_KEY_1\" \n" +
                        "  TABLESPACE \"IDX_ACCOUNT_DATA_TS\" , \n" +
                        "  SUBPARTITION \"FZ_KEY_2\" \n" +
                        "  TABLESPACE \"IDX_ACCOUNT_DATA_TS\" ) , \n" +
                        " PARTITION \"XM\" \n" +
                        "PCTFREE 10 INITRANS 20 MAXTRANS 255 \n" +
                        "  STORAGE(\n" +
                        "  BUFFER_POOL DEFAULT FLASH_CACHE DEFAULT CELL_FLASH_CACHE DEFAULT)\n" +
                        "  TABLESPACE \"IDX_ACCOUNT_DATA_TS\" \n" +
                        " ( SUBPARTITION \"XM_KEY_1\" \n" +
                        "  TABLESPACE \"IDX_ACCOUNT_DATA_TS\" , \n" +
                        "  SUBPARTITION \"XM_KEY_2\" \n" +
                        "  TABLESPACE \"IDX_ACCOUNT_DATA_TS\" ) , \n" +
                        " PARTITION \"ND\" \n" +
                        "PCTFREE 10 INITRANS 20 MAXTRANS 255 \n" +
                        "  STORAGE(\n" +
                        "  BUFFER_POOL DEFAULT FLASH_CACHE DEFAULT CELL_FLASH_CACHE DEFAULT)\n" +
                        "  TABLESPACE \"IDX_ACCOUNT_DATA_TS\" \n" +
                        " ( SUBPARTITION \"ND_KEY_1\" \n" +
                        "  TABLESPACE \"IDX_ACCOUNT_DATA_TS\" , \n" +
                        "  SUBPARTITION \"ND_KEY_2\" \n" +
                        "  TABLESPACE \"IDX_ACCOUNT_DATA_TS\" ) , \n" +
                        " PARTITION \"PT\" \n" +
                        "PCTFREE 10 INITRANS 20 MAXTRANS 255 \n" +
                        "  STORAGE(\n" +
                        "  BUFFER_POOL DEFAULT FLASH_CACHE DEFAULT CELL_FLASH_CACHE DEFAULT)\n" +
                        "  TABLESPACE \"IDX_ACCOUNT_DATA_TS\" \n" +
                        " ( SUBPARTITION \"PT_KEY_1\" \n" +
                        "  TABLESPACE \"IDX_ACCOUNT_DATA_TS\" , \n" +
                        "  SUBPARTITION \"PT_KEY_2\" \n" +
                        "  TABLESPACE \"IDX_ACCOUNT_DATA_TS\" ) , \n" +
                        " PARTITION \"QZ\" \n" +
                        "PCTFREE 10 INITRANS 20 MAXTRANS 255 \n" +
                        "  STORAGE(\n" +
                        "  BUFFER_POOL DEFAULT FLASH_CACHE DEFAULT CELL_FLASH_CACHE DEFAULT)\n" +
                        "  TABLESPACE \"IDX_ACCOUNT_DATA_TS\" \n" +
                        " ( SUBPARTITION \"QZ_KEY_1\" \n" +
                        "  TABLESPACE \"IDX_ACCOUNT_DATA_TS\" , \n" +
                        "  SUBPARTITION \"QZ_KEY_2\" \n" +
                        "  TABLESPACE \"IDX_ACCOUNT_DATA_TS\" ) , \n" +
                        " PARTITION \"ZZ\" \n" +
                        "PCTFREE 10 INITRANS 20 MAXTRANS 255 \n" +
                        "  STORAGE(\n" +
                        "  BUFFER_POOL DEFAULT FLASH_CACHE DEFAULT CELL_FLASH_CACHE DEFAULT)\n" +
                        "  TABLESPACE \"IDX_ACCOUNT_DATA_TS\" \n" +
                        " ( SUBPARTITION \"ZZ_KEY_1\" \n" +
                        "  TABLESPACE \"IDX_ACCOUNT_DATA_TS\" , \n" +
                        "  SUBPARTITION \"ZZ_KEY_2\" \n" +
                        "  TABLESPACE \"IDX_ACCOUNT_DATA_TS\" ) , \n" +
                        " PARTITION \"LY\" \n" +
                        "PCTFREE 10 INITRANS 20 MAXTRANS 255 \n" +
                        "  STORAGE(\n" +
                        "  BUFFER_POOL DEFAULT FLASH_CACHE DEFAULT CELL_FLASH_CACHE DEFAULT)\n" +
                        "  TABLESPACE \"IDX_ACCOUNT_DATA_TS\" \n" +
                        " ( SUBPARTITION \"LY_KEY_1\" \n" +
                        "  TABLESPACE \"IDX_ACCOUNT_DATA_TS\" , \n" +
                        "  SUBPARTITION \"LY_KEY_2\" \n" +
                        "  TABLESPACE \"IDX_ACCOUNT_DATA_TS\" ) , \n" +
                        " PARTITION \"SM\" \n" +
                        "PCTFREE 10 INITRANS 20 MAXTRANS 255 \n" +
                        "  STORAGE(\n" +
                        "  BUFFER_POOL DEFAULT FLASH_CACHE DEFAULT CELL_FLASH_CACHE DEFAULT)\n" +
                        "  TABLESPACE \"IDX_ACCOUNT_DATA_TS\" \n" +
                        " ( SUBPARTITION \"SM_KEY_1\" \n" +
                        "  TABLESPACE \"IDX_ACCOUNT_DATA_TS\" , \n" +
                        "  SUBPARTITION \"SM_KEY_2\" \n" +
                        "  TABLESPACE \"IDX_ACCOUNT_DATA_TS\" ) , \n" +
                        " PARTITION \"NP\" \n" +
                        "PCTFREE 10 INITRANS 20 MAXTRANS 255 \n" +
                        "  STORAGE(\n" +
                        "  BUFFER_POOL DEFAULT FLASH_CACHE DEFAULT CELL_FLASH_CACHE DEFAULT)\n" +
                        "  TABLESPACE \"IDX_ACCOUNT_DATA_TS\" \n" +
                        " ( SUBPARTITION \"NP_KEY_1\" \n" +
                        "  TABLESPACE \"IDX_ACCOUNT_DATA_TS\" , \n" +
                        "  SUBPARTITION \"NP_KEY_2\" \n" +
                        "  TABLESPACE \"IDX_ACCOUNT_DATA_TS\" ) )  ENABLE, \n" +
                        "     SUPPLEMENTAL LOG GROUP \"BROADBAND_USERS_RESULT_LGP1\" (\"BROAD_USER_ID\", \"PAYMENT_USER_ID\", \"PAYMENT_HOME_CITY\", \"MONTH_KEY\") ALWAYS\n" +
                        "   ) PCTFREE 10 PCTUSED 40 INITRANS 10 MAXTRANS 255 \n" +
                        "  STORAGE(\n" +
                        "  BUFFER_POOL DEFAULT FLASH_CACHE DEFAULT CELL_FLASH_CACHE DEFAULT)\n" +
                        "  TABLESPACE \"ACCOUNT_DATA_TS\" \n" +
                        "  PARTITION BY RANGE (\"PAYMENT_HOME_CITY\") \n" +
                        "  SUBPARTITION BY LIST (\"MONTH_KEY\") \n" +
                        "  SUBPARTITION TEMPLATE ( \n" +
                        "    SUBPARTITION \"KEY_1\" VALUES ( 1 ), \n" +
                        "    SUBPARTITION \"KEY_2\" VALUES ( 2 ) ) \n" +
                        " (PARTITION \"FZ\"  VALUES LESS THAN (592) \n" +
                        "PCTFREE 10 PCTUSED 40 INITRANS 10 MAXTRANS 255 \n" +
                        "  STORAGE(\n" +
                        "  BUFFER_POOL DEFAULT FLASH_CACHE DEFAULT CELL_FLASH_CACHE DEFAULT)\n" +
                        "  TABLESPACE \"ACCOUNT_DATA_TS\" , \n" +
                        " PARTITION \"XM\"  VALUES LESS THAN (593) \n" +
                        "PCTFREE 10 PCTUSED 40 INITRANS 10 MAXTRANS 255 \n" +
                        "  STORAGE(\n" +
                        "  BUFFER_POOL DEFAULT FLASH_CACHE DEFAULT CELL_FLASH_CACHE DEFAULT)\n" +
                        "  TABLESPACE \"ACCOUNT_DATA_TS\" , \n" +
                        " PARTITION \"ND\"  VALUES LESS THAN (594) \n" +
                        "PCTFREE 10 PCTUSED 40 INITRANS 10 MAXTRANS 255 \n" +
                        "  STORAGE(\n" +
                        "  BUFFER_POOL DEFAULT FLASH_CACHE DEFAULT CELL_FLASH_CACHE DEFAULT)\n" +
                        "  TABLESPACE \"ACCOUNT_DATA_TS\" , \n" +
                        " PARTITION \"PT\"  VALUES LESS THAN (595) \n" +
                        "PCTFREE 10 PCTUSED 40 INITRANS 10 MAXTRANS 255 \n" +
                        "  STORAGE(\n" +
                        "  BUFFER_POOL DEFAULT FLASH_CACHE DEFAULT CELL_FLASH_CACHE DEFAULT)\n" +
                        "  TABLESPACE \"ACCOUNT_DATA_TS\" , \n" +
                        " PARTITION \"QZ\"  VALUES LESS THAN (596) \n" +
                        "PCTFREE 10 PCTUSED 40 INITRANS 10 MAXTRANS 255 \n" +
                        "  STORAGE(\n" +
                        "  BUFFER_POOL DEFAULT FLASH_CACHE DEFAULT CELL_FLASH_CACHE DEFAULT)\n" +
                        "  TABLESPACE \"ACCOUNT_DATA_TS\" , \n" +
                        " PARTITION \"ZZ\"  VALUES LESS THAN (597) \n" +
                        "PCTFREE 10 PCTUSED 40 INITRANS 10 MAXTRANS 255 \n" +
                        "  STORAGE(\n" +
                        "  BUFFER_POOL DEFAULT FLASH_CACHE DEFAULT CELL_FLASH_CACHE DEFAULT)\n" +
                        "  TABLESPACE \"ACCOUNT_DATA_TS\" , \n" +
                        " PARTITION \"LY\"  VALUES LESS THAN (598) \n" +
                        "PCTFREE 10 PCTUSED 40 INITRANS 10 MAXTRANS 255 \n" +
                        "  STORAGE(\n" +
                        "  BUFFER_POOL DEFAULT FLASH_CACHE DEFAULT CELL_FLASH_CACHE DEFAULT)\n" +
                        "  TABLESPACE \"ACCOUNT_DATA_TS\" , \n" +
                        " PARTITION \"SM\"  VALUES LESS THAN (599) \n" +
                        "PCTFREE 10 PCTUSED 40 INITRANS 10 MAXTRANS 255 \n" +
                        "  STORAGE(\n" +
                        "  BUFFER_POOL DEFAULT FLASH_CACHE DEFAULT CELL_FLASH_CACHE DEFAULT)\n" +
                        "  TABLESPACE \"ACCOUNT_DATA_TS\" , \n" +
                        " PARTITION \"NP\"  VALUES LESS THAN (MAXVALUE) \n" +
                        "PCTFREE 10 PCTUSED 40 INITRANS 10 MAXTRANS 255 \n" +
                        "  STORAGE(\n" +
                        "  BUFFER_POOL DEFAULT FLASH_CACHE DEFAULT CELL_FLASH_CACHE DEFAULT)\n" +
                        "  TABLESPACE \"ACCOUNT_DATA_TS\" )  ENABLE ROW MOVEMENT ";

        OracleStatementParser parser = new OracleStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();
        SQLStatement stmt = statementList.get(0);
        print(statementList);

        assertEquals(1, statementList.size());

        OracleSchemaStatVisitor visitor = new OracleSchemaStatVisitor();
        stmt.accept(visitor);

        System.out.println("Tables : " + visitor.getTables());
        System.out.println("fields : " + visitor.getColumns());
        System.out.println("coditions : " + visitor.getConditions());
        System.out.println("relationships : " + visitor.getRelationships());
        System.out.println("orderBy : " + visitor.getOrderByColumns());

        assertEquals(1, visitor.getTables().size());

        assertEquals("CREATE TABLE \"ACCOUNT\".\"BROADBAND_USERS_RESULT\" (\n" +
                "\t\"HOME_CITY\" NUMBER(3, 0) NOT NULL ENABLE,\n" +
                "\t\"HOME_COUNTY\" NUMBER(3, 0) NOT NULL ENABLE,\n" +
                "\t\"MONTH_KEY\" NUMBER(2, 0) NOT NULL ENABLE,\n" +
                "\t\"MONTH\" NUMBER(2, 0) NOT NULL ENABLE,\n" +
                "\t\"BROAD_USER_ID\" NUMBER(15, 0) NOT NULL ENABLE,\n" +
                "\t\"PAYMENT_HOME_CITY\" NUMBER(3, 0) NOT NULL ENABLE,\n" +
                "\t\"PAYMENT_HOME_COUNTY\" NUMBER(3, 0) NOT NULL ENABLE,\n" +
                "\t\"PAYMENT_USER_ID\" NUMBER(15, 0) NOT NULL ENABLE,\n" +
                "\t\"CONNECT_TYPE\" NUMBER(2, 0),\n" +
                "\t\"PRODUCT_ID\" NUMBER(12, 0) NOT NULL ENABLE,\n" +
                "\t\"INURE_TIME\" DATE,\n" +
                "\t\"EXPIRE_TIME\" DATE,\n" +
                "\t\"BAND_WIDTH\" NUMBER(8, 0) NOT NULL ENABLE,\n" +
                "\tCONSTRAINT \"PK_BROADBAND_USERS_RESULT\" PRIMARY KEY (\"BROAD_USER_ID\", \"PAYMENT_USER_ID\", \"PAYMENT_HOME_CITY\", \"MONTH_KEY\")\n" +
                "\t\tUSING INDEX\n" +
                "\t\tPCTFREE 10\n" +
                "\t\tINITRANS 20\n" +
                "\t\tMAXTRANS 255\n" +
                "\t\tTABLESPACE \"IDX_ACCOUNT_DATA_TS\"\n" +
                "\t\tSTORAGE (\n" +
                "\t\t\tBUFFER_POOL DEFAULT\n" +
                "\t\t\tFLASH_CACHE DEFAULT\n" +
                "\t\t\tCELL_FLASH_CACHE DEFAULT\n" +
                "\t\t)\n" +
                "\t\tENABLE,\n" +
                "\tSUPPLEMENTAL LOG GROUP \"BROADBAND_USERS_RESULT_LGP1\" (\"BROAD_USER_ID\", \"PAYMENT_USER_ID\", \"PAYMENT_HOME_CITY\", \"MONTH_KEY\") ALWAYS\n" +
                ")\n" +
                "PCTFREE 10\n" +
                "PCTUSED 40\n" +
                "INITRANS 10\n" +
                "MAXTRANS 255\n" +
                "TABLESPACE \"ACCOUNT_DATA_TS\"\n" +
                "STORAGE (\n" +
                "\tBUFFER_POOL DEFAULT\n" +
                "\tFLASH_CACHE DEFAULT\n" +
                "\tCELL_FLASH_CACHE DEFAULT\n" +
                ")\n" +
                "PARTITION BY RANGE (\"PAYMENT_HOME_CITY\")\n" +
                "SUBPARTITION BY HASH (\"MONTH_KEY\")\n" +
                "\tSUBPARTITION TEMPLATE (\n" +
                "\t\tSUBPARTITION \"KEY_1\" VALUES (1),\n" +
                "\t\tSUBPARTITION \"KEY_2\" VALUES (2)\n" +
                "\t) (\n" +
                "\tPARTITION \"FZ\" VALUES LESS THAN (592)\n" +
                "\t\tPCTFREE 10\n" +
                "\t\tPCTUSED 40\n" +
                "\t\tINITRANS 10\n" +
                "\t\tMAXTRANS 255\n" +
                "\t\tTABLESPACE \"ACCOUNT_DATA_TS\"\n" +
                "\t\tSTORAGE (\n" +
                "\t\t\tBUFFER_POOL DEFAULT\n" +
                "\t\t\tFLASH_CACHE DEFAULT\n" +
                "\t\t\tCELL_FLASH_CACHE DEFAULT\n" +
                "\t\t),\n" +
                "\tPARTITION \"XM\" VALUES LESS THAN (593)\n" +
                "\t\tPCTFREE 10\n" +
                "\t\tPCTUSED 40\n" +
                "\t\tINITRANS 10\n" +
                "\t\tMAXTRANS 255\n" +
                "\t\tTABLESPACE \"ACCOUNT_DATA_TS\"\n" +
                "\t\tSTORAGE (\n" +
                "\t\t\tBUFFER_POOL DEFAULT\n" +
                "\t\t\tFLASH_CACHE DEFAULT\n" +
                "\t\t\tCELL_FLASH_CACHE DEFAULT\n" +
                "\t\t),\n" +
                "\tPARTITION \"ND\" VALUES LESS THAN (594)\n" +
                "\t\tPCTFREE 10\n" +
                "\t\tPCTUSED 40\n" +
                "\t\tINITRANS 10\n" +
                "\t\tMAXTRANS 255\n" +
                "\t\tTABLESPACE \"ACCOUNT_DATA_TS\"\n" +
                "\t\tSTORAGE (\n" +
                "\t\t\tBUFFER_POOL DEFAULT\n" +
                "\t\t\tFLASH_CACHE DEFAULT\n" +
                "\t\t\tCELL_FLASH_CACHE DEFAULT\n" +
                "\t\t),\n" +
                "\tPARTITION \"PT\" VALUES LESS THAN (595)\n" +
                "\t\tPCTFREE 10\n" +
                "\t\tPCTUSED 40\n" +
                "\t\tINITRANS 10\n" +
                "\t\tMAXTRANS 255\n" +
                "\t\tTABLESPACE \"ACCOUNT_DATA_TS\"\n" +
                "\t\tSTORAGE (\n" +
                "\t\t\tBUFFER_POOL DEFAULT\n" +
                "\t\t\tFLASH_CACHE DEFAULT\n" +
                "\t\t\tCELL_FLASH_CACHE DEFAULT\n" +
                "\t\t),\n" +
                "\tPARTITION \"QZ\" VALUES LESS THAN (596)\n" +
                "\t\tPCTFREE 10\n" +
                "\t\tPCTUSED 40\n" +
                "\t\tINITRANS 10\n" +
                "\t\tMAXTRANS 255\n" +
                "\t\tTABLESPACE \"ACCOUNT_DATA_TS\"\n" +
                "\t\tSTORAGE (\n" +
                "\t\t\tBUFFER_POOL DEFAULT\n" +
                "\t\t\tFLASH_CACHE DEFAULT\n" +
                "\t\t\tCELL_FLASH_CACHE DEFAULT\n" +
                "\t\t),\n" +
                "\tPARTITION \"ZZ\" VALUES LESS THAN (597)\n" +
                "\t\tPCTFREE 10\n" +
                "\t\tPCTUSED 40\n" +
                "\t\tINITRANS 10\n" +
                "\t\tMAXTRANS 255\n" +
                "\t\tTABLESPACE \"ACCOUNT_DATA_TS\"\n" +
                "\t\tSTORAGE (\n" +
                "\t\t\tBUFFER_POOL DEFAULT\n" +
                "\t\t\tFLASH_CACHE DEFAULT\n" +
                "\t\t\tCELL_FLASH_CACHE DEFAULT\n" +
                "\t\t),\n" +
                "\tPARTITION \"LY\" VALUES LESS THAN (598)\n" +
                "\t\tPCTFREE 10\n" +
                "\t\tPCTUSED 40\n" +
                "\t\tINITRANS 10\n" +
                "\t\tMAXTRANS 255\n" +
                "\t\tTABLESPACE \"ACCOUNT_DATA_TS\"\n" +
                "\t\tSTORAGE (\n" +
                "\t\t\tBUFFER_POOL DEFAULT\n" +
                "\t\t\tFLASH_CACHE DEFAULT\n" +
                "\t\t\tCELL_FLASH_CACHE DEFAULT\n" +
                "\t\t),\n" +
                "\tPARTITION \"SM\" VALUES LESS THAN (599)\n" +
                "\t\tPCTFREE 10\n" +
                "\t\tPCTUSED 40\n" +
                "\t\tINITRANS 10\n" +
                "\t\tMAXTRANS 255\n" +
                "\t\tTABLESPACE \"ACCOUNT_DATA_TS\"\n" +
                "\t\tSTORAGE (\n" +
                "\t\t\tBUFFER_POOL DEFAULT\n" +
                "\t\t\tFLASH_CACHE DEFAULT\n" +
                "\t\t\tCELL_FLASH_CACHE DEFAULT\n" +
                "\t\t),\n" +
                "\tPARTITION \"NP\" VALUES LESS THAN (MAXVALUE)\n" +
                "\t\tPCTFREE 10\n" +
                "\t\tPCTUSED 40\n" +
                "\t\tINITRANS 10\n" +
                "\t\tMAXTRANS 255\n" +
                "\t\tTABLESPACE \"ACCOUNT_DATA_TS\"\n" +
                "\t\tSTORAGE (\n" +
                "\t\t\tBUFFER_POOL DEFAULT\n" +
                "\t\t\tFLASH_CACHE DEFAULT\n" +
                "\t\t\tCELL_FLASH_CACHE DEFAULT\n" +
                "\t\t)\n" +
                ")", stmt.toString());

    }
}
