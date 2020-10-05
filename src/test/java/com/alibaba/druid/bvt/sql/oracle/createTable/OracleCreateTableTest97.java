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

public class OracleCreateTableTest97 extends OracleTest {

    public void test_0() throws Exception {
        String sql = //
                "CREATE TABLE \"ADAM\".\"TAB_PART_RANGE_HASH\" \n" +
                        "   (  \"RANGE_KEY\" DATE, \n" +
                        "  \"HASH_KEY\" NUMBER(*,0), \n" +
                        "  \"DATA\" DATE\n" +
                        "   ) PCTFREE 10 PCTUSED 40 INITRANS 1 MAXTRANS 255 \n" +
                        "  STORAGE(\n" +
                        "  BUFFER_POOL DEFAULT FLASH_CACHE DEFAULT CELL_FLASH_CACHE DEFAULT)\n" +
                        "  TABLESPACE \"USERS\" \n" +
                        "  PARTITION BY RANGE (\"RANGE_KEY\") \n" +
                        "  SUBPARTITION BY HASH (\"HASH_KEY\") \n" +
                        "  SUBPARTITIONS 2\n" +
                        " (PARTITION \"P1\"  VALUES LESS THAN (TO_DATE(' 2014-04-01 00:00:00', 'SYYYY-MM-DD HH24:MI:SS', 'NLS_CALENDAR=GREGORIAN')) \n" +
                        "PCTFREE 10 PCTUSED 40 INITRANS 1 MAXTRANS 255 \n" +
                        "  STORAGE(\n" +
                        "  BUFFER_POOL DEFAULT FLASH_CACHE DEFAULT CELL_FLASH_CACHE DEFAULT)\n" +
                        "  TABLESPACE \"USERS\" \n" +
                        " ( SUBPARTITION \"H1\" \n" +
                        "  TABLESPACE \"USERS\" \n" +
                        " NOCOMPRESS , \n" +
                        "  SUBPARTITION \"H2\" \n" +
                        "  TABLESPACE \"USERS\" \n" +
                        " NOCOMPRESS ) , \n" +
                        " PARTITION \"P2\"  VALUES LESS THAN (TO_DATE(' 2015-04-01 00:00:00', 'SYYYY-MM-DD HH24:MI:SS', 'NLS_CALENDAR=GREGORIAN')) \n" +
                        "PCTFREE 10 PCTUSED 40 INITRANS 1 MAXTRANS 255 \n" +
                        "  STORAGE(\n" +
                        "  BUFFER_POOL DEFAULT FLASH_CACHE DEFAULT CELL_FLASH_CACHE DEFAULT)\n" +
                        "  TABLESPACE \"USERS\" \n" +
                        " ( SUBPARTITION \"H11\" \n" +
                        "  TABLESPACE \"USERS\" \n" +
                        " NOCOMPRESS , \n" +
                        "  SUBPARTITION \"H22\" \n" +
                        "  TABLESPACE \"USERS\" \n" +
                        " NOCOMPRESS ) );";

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

        assertEquals("CREATE TABLE \"ADAM\".\"TAB_PART_RANGE_HASH\" (\n" +
                "\t\"RANGE_KEY\" DATE,\n" +
                "\t\"HASH_KEY\" NUMBER(*, 0),\n" +
                "\t\"DATA\" DATE\n" +
                ")\n" +
                "PCTFREE 10\n" +
                "PCTUSED 40\n" +
                "INITRANS 1\n" +
                "MAXTRANS 255\n" +
                "TABLESPACE \"USERS\"\n" +
                "STORAGE (\n" +
                "\tBUFFER_POOL DEFAULT\n" +
                "\tFLASH_CACHE DEFAULT\n" +
                "\tCELL_FLASH_CACHE DEFAULT\n" +
                ")\n" +
                "PARTITION BY RANGE (\"RANGE_KEY\")\n" +
                "SUBPARTITION BY HASH (\"HASH_KEY\") SUBPARTITIONS 2 (\n" +
                "\tPARTITION \"P1\" VALUES LESS THAN (TO_DATE(' 2014-04-01 00:00:00', 'SYYYY-MM-DD HH24:MI:SS', 'NLS_CALENDAR=GREGORIAN'))\n" +
                "\t\tPCTFREE 10\n" +
                "\t\tPCTUSED 40\n" +
                "\t\tINITRANS 1\n" +
                "\t\tMAXTRANS 255\n" +
                "\t\tTABLESPACE \"USERS\"\n" +
                "\t\tSTORAGE (\n" +
                "\t\t\tBUFFER_POOL DEFAULT\n" +
                "\t\t\tFLASH_CACHE DEFAULT\n" +
                "\t\t\tCELL_FLASH_CACHE DEFAULT\n" +
                "\t\t) (\n" +
                "\t\tSUBPARTITION \"H1\" TABLESPACE \"USERS\"\n" +
                "\t\t\tNOCOMPRESS,\n" +
                "\t\tSUBPARTITION \"H2\" TABLESPACE \"USERS\"\n" +
                "\t\t\tNOCOMPRESS\n" +
                "\t),\n" +
                "\tPARTITION \"P2\" VALUES LESS THAN (TO_DATE(' 2015-04-01 00:00:00', 'SYYYY-MM-DD HH24:MI:SS', 'NLS_CALENDAR=GREGORIAN'))\n" +
                "\t\tPCTFREE 10\n" +
                "\t\tPCTUSED 40\n" +
                "\t\tINITRANS 1\n" +
                "\t\tMAXTRANS 255\n" +
                "\t\tTABLESPACE \"USERS\"\n" +
                "\t\tSTORAGE (\n" +
                "\t\t\tBUFFER_POOL DEFAULT\n" +
                "\t\t\tFLASH_CACHE DEFAULT\n" +
                "\t\t\tCELL_FLASH_CACHE DEFAULT\n" +
                "\t\t) (\n" +
                "\t\tSUBPARTITION \"H11\" TABLESPACE \"USERS\"\n" +
                "\t\t\tNOCOMPRESS,\n" +
                "\t\tSUBPARTITION \"H22\" TABLESPACE \"USERS\"\n" +
                "\t\t\tNOCOMPRESS\n" +
                "\t)\n" +
                ");", stmt.toString());

    }
}
