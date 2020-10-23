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

public class OracleCreateTableTest91 extends OracleTest {

    public void test_0() throws Exception {
        String sql = //
                "CREATE TABLE \"IX\".\"AQ$_ORDERS_QUEUETABLE_G\" \n" +
                        "   (    \"MSGID\" RAW(16), \n" +
                        "    \"SUBSCRIBER#\" NUMBER, \n" +
                        "    \"NAME\" VARCHAR2(30), \n" +
                        "    \"ADDRESS#\" NUMBER, \n" +
                        "    \"SIGN\" \"SYS\".\"AQ$_SIG_PROP\" , \n" +
                        "    \"DBS_SIGN\" \"SYS\".\"AQ$_SIG_PROP\" , \n" +
                        "     PRIMARY KEY (\"MSGID\", \"SUBSCRIBER#\", \"NAME\", \"ADDRESS#\") ENABLE\n" +
                        "   ) USAGE QUEUE ORGANIZATION INDEX NOCOMPRESS PCTFREE 10 INITRANS 2 MAXTRANS 255 NOLOGGING\n" +
                        "  STORAGE(INITIAL 65536 NEXT 1048576 MINEXTENTS 1 MAXEXTENTS 2147483645\n" +
                        "  PCTINCREASE 0 FREELISTS 1 FREELIST GROUPS 1 BUFFER_POOL DEFAULT FLASH_CACHE DEFAULT CELL_FLASH_CACHE DEFAULT)\n" +
                        "  TABLESPACE \"EXAMPLE\" \n" +
                        " PCTTHRESHOLD 50 INCLUDING \"SIGN\" OVERFLOW\n" +
                        " PCTFREE 10 PCTUSED 40 INITRANS 1 MAXTRANS 255 LOGGING\n" +
                        "  STORAGE(INITIAL 65536 NEXT 1048576 MINEXTENTS 1 MAXEXTENTS 2147483645\n" +
                        "  PCTINCREASE 0 FREELISTS 1 FREELIST GROUPS 1 BUFFER_POOL DEFAULT FLASH_CACHE DEFAULT CELL_FLASH_CACHE DEFAULT)\n" +
                        "  TABLESPACE \"EXAMPLE\" ";

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

        assertEquals("CREATE TABLE \"IX\".\"AQ$_ORDERS_QUEUETABLE_G\" (\n" +
                "\t\"MSGID\" RAW(16),\n" +
                "\t\"SUBSCRIBER#\" NUMBER,\n" +
                "\t\"NAME\" VARCHAR2(30),\n" +
                "\t\"ADDRESS#\" NUMBER,\n" +
                "\t\"SIGN\" \"SYS\".\"AQ$_SIG_PROP\",\n" +
                "\t\"DBS_SIGN\" \"SYS\".\"AQ$_SIG_PROP\",\n" +
                "\tPRIMARY KEY (\"MSGID\", \"SUBSCRIBER#\", \"NAME\", \"ADDRESS#\") ENABLE\n" +
                ")\n" +
                "ORGANIZATION INDEX\n" +
                "\tPCTFREE 10\n" +
                "\tINITRANS 2\n" +
                "\tMAXTRANS 255\n" +
                "\tNOCOMPRESS\n" +
                "\tNOLOGGING\n" +
                "\tTABLESPACE \"EXAMPLE\"\n" +
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
                "\tPCTTHRESHOLD 50 INCLUDING \"SIGN\" OVERFLOW \n" +
                "PCTFREE 10\n" +
                "PCTUSED 40\n" +
                "INITRANS 1\n" +
                "MAXTRANS 255\n" +
                "LOGGING\n" +
                "TABLESPACE \"EXAMPLE\"\n" +
                "STORAGE (\n" +
                "\tINITIAL 65536\n" +
                "\tNEXT 1048576\n" +
                "\tMINEXTENTS 1\n" +
                "\tMAXEXTENTS 2147483645\n" +
                "\tPCTINCREASE 0\n" +
                "\tFREELISTS 1\n" +
                "\tFREELIST GROUPS 1\n" +
                "\tBUFFER_POOL DEFAULT\n" +
                "\tFLASH_CACHE DEFAULT\n" +
                "\tCELL_FLASH_CACHE DEFAULT\n" +
                ")", stmt.toString());

    }
}
