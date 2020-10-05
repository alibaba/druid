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
import com.alibaba.druid.sql.ast.statement.SQLCreateTableStatement;
import com.alibaba.druid.util.JdbcConstants;

import java.util.List;

public class OracleCreateTableTest84 extends OracleTest {

    public void test_types() throws Exception {
        String sql = //
                "CREATE TABLE \"IX\".\"ORDERS_QUEUETABLE\" \n" +
                        "   (\t\"Q_NAME\" VARCHAR2(30), \n" +
                        "\t\"MSGID\" RAW(16), \n" +
                        "\t\"CORRID\" VARCHAR2(128), \n" +
                        "\t\"PRIORITY\" NUMBER, \n" +
                        "\t\"STATE\" NUMBER, \n" +
                        "\t\"DELAY\" TIMESTAMP (6), \n" +
                        "\t\"EXPIRATION\" NUMBER, \n" +
                        "\t\"TIME_MANAGER_INFO\" TIMESTAMP (6), \n" +
                        "\t\"LOCAL_ORDER_NO\" NUMBER, \n" +
                        "\t\"CHAIN_NO\" NUMBER, \n" +
                        "\t\"CSCN\" NUMBER, \n" +
                        "\t\"DSCN\" NUMBER, \n" +
                        "\t\"ENQ_TIME\" TIMESTAMP (6), \n" +
                        "\t\"ENQ_UID\" VARCHAR2(30), \n" +
                        "\t\"ENQ_TID\" VARCHAR2(30), \n" +
                        "\t\"DEQ_TIME\" TIMESTAMP (6), \n" +
                        "\t\"DEQ_UID\" VARCHAR2(30), \n" +
                        "\t\"DEQ_TID\" VARCHAR2(30), \n" +
                        "\t\"RETRY_COUNT\" NUMBER, \n" +
                        "\t\"EXCEPTION_QSCHEMA\" VARCHAR2(30), \n" +
                        "\t\"EXCEPTION_QUEUE\" VARCHAR2(30), \n" +
                        "\t\"STEP_NO\" NUMBER, \n" +
                        "\t\"RECIPIENT_KEY\" NUMBER, \n" +
                        "\t\"DEQUEUE_MSGID\" RAW(16), \n" +
                        "\t\"SENDER_NAME\" VARCHAR2(30), \n" +
                        "\t\"SENDER_ADDRESS\" VARCHAR2(1024), \n" +
                        "\t\"SENDER_PROTOCOL\" NUMBER, \n" +
                        "\t\"USER_DATA\" \"IX\".\"ORDER_EVENT_TYP\" , \n" +
                        "\t\"USER_PROP\" \"SYS\".\"ANYDATA\" , \n" +
                        "\t PRIMARY KEY (\"MSGID\")\n" +
                        "  USING INDEX PCTFREE 10 INITRANS 2 MAXTRANS 255 COMPUTE STATISTICS NOLOGGING \n" +
                        "  STORAGE(INITIAL 65536 NEXT 1048576 MINEXTENTS 1 MAXEXTENTS 2147483645\n" +
                        "  PCTINCREASE 0 FREELISTS 1 FREELIST GROUPS 1\n" +
                        "  BUFFER_POOL DEFAULT FLASH_CACHE DEFAULT CELL_FLASH_CACHE DEFAULT)\n" +
                        "  TABLESPACE \"EXAMPLE\"  ENABLE\n" +
                        "   ) USAGE QUEUE PCTFREE 10 PCTUSED 40 INITRANS 1 MAXTRANS 255 \n" +
                        " NOCOMPRESS NOLOGGING\n" +
                        "  STORAGE(INITIAL 65536 NEXT 1048576 MINEXTENTS 1 MAXEXTENTS 2147483645\n" +
                        "  PCTINCREASE 0 FREELISTS 1 FREELIST GROUPS 1\n" +
                        "  BUFFER_POOL DEFAULT FLASH_CACHE DEFAULT CELL_FLASH_CACHE DEFAULT)\n" +
                        "  TABLESPACE \"EXAMPLE\" \n" +
                        " OPAQUE TYPE \"USER_PROP\" STORE AS BASICFILE LOB (\n" +
                        "  ENABLE STORAGE IN ROW CHUNK 8192 RETENTION \n" +
                        "  CACHE \n" +
                        "  STORAGE(INITIAL 65536 NEXT 1048576 MINEXTENTS 1 MAXEXTENTS 2147483645\n" +
                        "  PCTINCREASE 0 FREELISTS 1 FREELIST GROUPS 1\n" +
                        "  BUFFER_POOL DEFAULT FLASH_CACHE DEFAULT CELL_FLASH_CACHE DEFAULT)) ";

        List<SQLStatement> statementList = SQLUtils.parseStatements(sql, JdbcConstants.ORACLE);
        SQLCreateTableStatement stmt = (SQLCreateTableStatement) statementList.get(0);
        print(statementList);

        assertEquals(1, statementList.size());
//
        assertEquals("CREATE TABLE \"IX\".\"ORDERS_QUEUETABLE\" (\n" +
                        "\t\"Q_NAME\" VARCHAR2(30),\n" +
                        "\t\"MSGID\" RAW(16),\n" +
                        "\t\"CORRID\" VARCHAR2(128),\n" +
                        "\t\"PRIORITY\" NUMBER,\n" +
                        "\t\"STATE\" NUMBER,\n" +
                        "\t\"DELAY\" TIMESTAMP(6),\n" +
                        "\t\"EXPIRATION\" NUMBER,\n" +
                        "\t\"TIME_MANAGER_INFO\" TIMESTAMP(6),\n" +
                        "\t\"LOCAL_ORDER_NO\" NUMBER,\n" +
                        "\t\"CHAIN_NO\" NUMBER,\n" +
                        "\t\"CSCN\" NUMBER,\n" +
                        "\t\"DSCN\" NUMBER,\n" +
                        "\t\"ENQ_TIME\" TIMESTAMP(6),\n" +
                        "\t\"ENQ_UID\" VARCHAR2(30),\n" +
                        "\t\"ENQ_TID\" VARCHAR2(30),\n" +
                        "\t\"DEQ_TIME\" TIMESTAMP(6),\n" +
                        "\t\"DEQ_UID\" VARCHAR2(30),\n" +
                        "\t\"DEQ_TID\" VARCHAR2(30),\n" +
                        "\t\"RETRY_COUNT\" NUMBER,\n" +
                        "\t\"EXCEPTION_QSCHEMA\" VARCHAR2(30),\n" +
                        "\t\"EXCEPTION_QUEUE\" VARCHAR2(30),\n" +
                        "\t\"STEP_NO\" NUMBER,\n" +
                        "\t\"RECIPIENT_KEY\" NUMBER,\n" +
                        "\t\"DEQUEUE_MSGID\" RAW(16),\n" +
                        "\t\"SENDER_NAME\" VARCHAR2(30),\n" +
                        "\t\"SENDER_ADDRESS\" VARCHAR2(1024),\n" +
                        "\t\"SENDER_PROTOCOL\" NUMBER,\n" +
                        "\t\"USER_DATA\" \"IX\".\"ORDER_EVENT_TYP\",\n" +
                        "\t\"USER_PROP\" \"SYS\".\"ANYDATA\",\n" +
                        "\tPRIMARY KEY (\"MSGID\")\n" +
                        "\t\tUSING INDEX\n" +
                        "\t\tPCTFREE 10\n" +
                        "\t\tINITRANS 2\n" +
                        "\t\tMAXTRANS 255\n" +
                        "\t\tNOLOGGING\n" +
                        "\t\tTABLESPACE \"EXAMPLE\"\n" +
                        "\t\tSTORAGE (\n" +
                        "\t\t\tINITIAL 65536\n" +
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
                        "NOLOGGING\n" +
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
                        ")",//
                            SQLUtils.toSQLString(stmt, JdbcConstants.ORACLE));

        stmt.renameColumn("ID", "FID");

        assertEquals("CREATE TABLE \"IX\".\"ORDERS_QUEUETABLE\" (\n" +
                        "\t\"Q_NAME\" VARCHAR2(30),\n" +
                        "\t\"MSGID\" RAW(16),\n" +
                        "\t\"CORRID\" VARCHAR2(128),\n" +
                        "\t\"PRIORITY\" NUMBER,\n" +
                        "\t\"STATE\" NUMBER,\n" +
                        "\t\"DELAY\" TIMESTAMP(6),\n" +
                        "\t\"EXPIRATION\" NUMBER,\n" +
                        "\t\"TIME_MANAGER_INFO\" TIMESTAMP(6),\n" +
                        "\t\"LOCAL_ORDER_NO\" NUMBER,\n" +
                        "\t\"CHAIN_NO\" NUMBER,\n" +
                        "\t\"CSCN\" NUMBER,\n" +
                        "\t\"DSCN\" NUMBER,\n" +
                        "\t\"ENQ_TIME\" TIMESTAMP(6),\n" +
                        "\t\"ENQ_UID\" VARCHAR2(30),\n" +
                        "\t\"ENQ_TID\" VARCHAR2(30),\n" +
                        "\t\"DEQ_TIME\" TIMESTAMP(6),\n" +
                        "\t\"DEQ_UID\" VARCHAR2(30),\n" +
                        "\t\"DEQ_TID\" VARCHAR2(30),\n" +
                        "\t\"RETRY_COUNT\" NUMBER,\n" +
                        "\t\"EXCEPTION_QSCHEMA\" VARCHAR2(30),\n" +
                        "\t\"EXCEPTION_QUEUE\" VARCHAR2(30),\n" +
                        "\t\"STEP_NO\" NUMBER,\n" +
                        "\t\"RECIPIENT_KEY\" NUMBER,\n" +
                        "\t\"DEQUEUE_MSGID\" RAW(16),\n" +
                        "\t\"SENDER_NAME\" VARCHAR2(30),\n" +
                        "\t\"SENDER_ADDRESS\" VARCHAR2(1024),\n" +
                        "\t\"SENDER_PROTOCOL\" NUMBER,\n" +
                        "\t\"USER_DATA\" \"IX\".\"ORDER_EVENT_TYP\",\n" +
                        "\t\"USER_PROP\" \"SYS\".\"ANYDATA\",\n" +
                        "\tPRIMARY KEY (\"MSGID\")\n" +
                        "\t\tUSING INDEX\n" +
                        "\t\tPCTFREE 10\n" +
                        "\t\tINITRANS 2\n" +
                        "\t\tMAXTRANS 255\n" +
                        "\t\tNOLOGGING\n" +
                        "\t\tTABLESPACE \"EXAMPLE\"\n" +
                        "\t\tSTORAGE (\n" +
                        "\t\t\tINITIAL 65536\n" +
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
                        "NOLOGGING\n" +
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
                        ")",//
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
