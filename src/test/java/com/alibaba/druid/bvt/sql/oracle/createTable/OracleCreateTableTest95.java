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

public class OracleCreateTableTest95 extends OracleTest {

    public void test_0() throws Exception {
        String sql = //
                "CREATE TABLE \"HN3_3\".\"BGG_EXTEND_BULLETIN\" \n" +
                        "   (    \"ID\" NUMBER NOT NULL ENABLE, \n" +
                        "    \"BULLETIN_ID\" NUMBER, \n" +
                        "    \"STATUS\" VARCHAR2(20), \n" +
                        "    \"XMLDOC\" \"SYS\".\"XMLTYPE\" , \n" +
                        "    \"IS_DELETED\" VARCHAR2(20), \n" +
                        "    \"CREATE_USER_ID\" NUMBER, \n" +
                        "    \"CREATE_TIME\" DATE, \n" +
                        "    \"UPDATE_USER_ID\" NUMBER, \n" +
                        "    \"UPDATE_TIME\" DATE, \n" +
                        "    \"BID_ID\" NUMBER, \n" +
                        "    \"BULLETIN_TYPE\" VARCHAR2(20), \n" +
                        "     CONSTRAINT \"PK_BGG_EXTEND_BULLETIN\" PRIMARY KEY (\"ID\")\n" +
                        "  USING INDEX PCTFREE 10 INITRANS 2 MAXTRANS 255 COMPUTE STATISTICS \n" +
                        "  STORAGE(INITIAL 196608 NEXT 10485760 MINEXTENTS 1 MAXEXTENTS 2147483645\n" +
                        "  PCTINCREASE 0 FREELISTS 1 FREELIST GROUPS 1\n" +
                        "  BUFFER_POOL DEFAULT FLASH_CACHE DEFAULT CELL_FLASH_CACHE DEFAULT)\n" +
                        "  TABLESPACE \"HN3_3\"  ENABLE\n" +
                        "   ) SEGMENT CREATION IMMEDIATE \n" +
                        "  PCTFREE 10 PCTUSED 40 INITRANS 1 MAXTRANS 255 \n" +
                        " NOCOMPRESS LOGGING\n" +
                        "  STORAGE(INITIAL 131072 NEXT 10485760 MINEXTENTS 1 MAXEXTENTS 2147483645\n" +
                        "  PCTINCREASE 0 FREELISTS 1 FREELIST GROUPS 1\n" +
                        "  BUFFER_POOL DEFAULT FLASH_CACHE DEFAULT CELL_FLASH_CACHE DEFAULT)\n" +
                        "  TABLESPACE \"HN3_3\" \n" +
                        " XMLTYPE COLUMN \"XMLDOC\" STORE AS BASICFILE CLOB (\n" +
                        "  TABLESPACE \"HN3_3\" ENABLE STORAGE IN ROW CHUNK 8192 PCTVERSION 10\n" +
                        "  NOCACHE LOGGING \n" +
                        "  STORAGE(INITIAL 10485760 NEXT 10485760 MINEXTENTS 1 MAXEXTENTS 2147483645\n" +
                        "  PCTINCREASE 0 FREELISTS 1 FREELIST GROUPS 1\n" +
                        "  BUFFER_POOL DEFAULT FLASH_CACHE DEFAULT CELL_FLASH_CACHE DEFAULT)) ";

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

        assertEquals("CREATE TABLE \"HN3_3\".\"BGG_EXTEND_BULLETIN\" (\n" +
                "\t\"ID\" NUMBER NOT NULL ENABLE,\n" +
                "\t\"BULLETIN_ID\" NUMBER,\n" +
                "\t\"STATUS\" VARCHAR2(20),\n" +
                "\t\"XMLDOC\" \"SYS\".\"XMLTYPE\",\n" +
                "\t\"IS_DELETED\" VARCHAR2(20),\n" +
                "\t\"CREATE_USER_ID\" NUMBER,\n" +
                "\t\"CREATE_TIME\" DATE,\n" +
                "\t\"UPDATE_USER_ID\" NUMBER,\n" +
                "\t\"UPDATE_TIME\" DATE,\n" +
                "\t\"BID_ID\" NUMBER,\n" +
                "\t\"BULLETIN_TYPE\" VARCHAR2(20),\n" +
                "\tCONSTRAINT \"PK_BGG_EXTEND_BULLETIN\" PRIMARY KEY (\"ID\")\n" +
                "\t\tUSING INDEX\n" +
                "\t\tPCTFREE 10\n" +
                "\t\tINITRANS 2\n" +
                "\t\tMAXTRANS 255\n" +
                "\t\tTABLESPACE \"HN3_3\"\n" +
                "\t\tSTORAGE (\n" +
                "\t\t\tINITIAL 196608\n" +
                "\t\t\tNEXT 10485760\n" +
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
                "TABLESPACE \"HN3_3\"\n" +
                "STORAGE (\n" +
                "\tINITIAL 131072\n" +
                "\tNEXT 10485760\n" +
                "\tMINEXTENTS 1\n" +
                "\tMAXEXTENTS 2147483645\n" +
                "\tPCTINCREASE 0\n" +
                "\tFREELISTS 1\n" +
                "\tFREELIST GROUPS 1\n" +
                "\tBUFFER_POOL DEFAULT\n" +
                "\tFLASH_CACHE DEFAULT\n" +
                "\tCELL_FLASH_CACHE DEFAULT\n" +
                ")\n" +
                "XMLTYPE \"XMLDOC\"", stmt.toString());

    }
}
