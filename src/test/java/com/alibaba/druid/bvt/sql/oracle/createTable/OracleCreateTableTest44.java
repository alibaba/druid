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

public class OracleCreateTableTest44 extends OracleTest {

    public void test_types() throws Exception {
        String sql = //
        "CREATE TABLE \"SC_001\".\"TB_001\" \n" +
                "   (  \"PK1\" NUMBER, \n" +
                "  \"COL1\" CHAR(1), \n" +
                "  \"COL2\" NCHAR(20), \n" +
                "  \"COL3\" VARCHAR2(30), \n" +
                "  \"COL4\" NVARCHAR2(30), \n" +
                "  \"COL5\" NUMBER(*,0), \n" +
                "  \"COL6\" FLOAT(10), \n" +
                "  \"COL7\" DATE, \n" +
                "  \"COL8\" TIMESTAMP (6), \n" +
                "  \"COL9\" BLOB, \n" +
                "  \"COL10\" LONG, \n" +
                "   PRIMARY KEY (\"PK1\")\n" +
                "  USING INDEX PCTFREE 10 INITRANS 2 MAXTRANS 255 COMPUTE STATISTICS \n" +
                "  STORAGE(INITIAL 65536 NEXT 1048576 MINEXTENTS 1 MAXEXTENTS 2147483645\n" +
                "  PCTINCREASE 0 FREELISTS 1 FREELIST GROUPS 1 BUFFER_POOL DEFAULT FLASH_CACHE DEFAULT CELL_FLASH_CACHE DEFAULT)\n" +
                "  TABLESPACE \"USERS\"  ENABLE\n" +
                "   ) SEGMENT CREATION IMMEDIATE \n" +
                "  PCTFREE 10 PCTUSED 40 INITRANS 1 MAXTRANS 255 NOCOMPRESS LOGGING\n" +
                "  STORAGE(INITIAL 65536 NEXT 1048576 MINEXTENTS 1 MAXEXTENTS 2147483645\n" +
                "  PCTINCREASE 0 FREELISTS 1 FREELIST GROUPS 1 BUFFER_POOL DEFAULT FLASH_CACHE DEFAULT CELL_FLASH_CACHE DEFAULT)\n" +
                "  TABLESPACE \"USERS\" \n" +
                " LOB (\"COL9\") STORE AS BASICFILE (\n" +
                "  TABLESPACE \"USERS\" ENABLE STORAGE IN ROW CHUNK 8192 RETENTION \n" +
                "  NOCACHE LOGGING \n" +
                "  STORAGE(INITIAL 65536 NEXT 1048576 MINEXTENTS 1 MAXEXTENTS 2147483645\n" +
                "  PCTINCREASE 0 FREELISTS 1 FREELIST GROUPS 1 BUFFER_POOL DEFAULT FLASH_CACHE DEFAULT CELL_FLASH_CACHE DEFAULT))";

        OracleStatementParser parser = new OracleStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();
        SQLStatement stmt = statementList.get(0);
        print(statementList);

        Assert.assertEquals(1, statementList.size());

        assertEquals("CREATE TABLE \"SC_001\".\"TB_001\" (\n" +
                        "\t\"PK1\" NUMBER,\n" +
                        "\t\"COL1\" CHAR(1),\n" +
                        "\t\"COL2\" NCHAR(20),\n" +
                        "\t\"COL3\" VARCHAR2(30),\n" +
                        "\t\"COL4\" NVARCHAR2(30),\n" +
                        "\t\"COL5\" NUMBER(*, 0),\n" +
                        "\t\"COL6\" FLOAT(10),\n" +
                        "\t\"COL7\" DATE,\n" +
                        "\t\"COL8\" TIMESTAMP(6),\n" +
                        "\t\"COL9\" BLOB,\n" +
                        "\t\"COL10\" LONG,\n" +
                        "\tPRIMARY KEY (\"PK1\")\n" +
                        "\t\tUSING INDEX\n" +
                        "\t\tPCTFREE 10\n" +
                        "\t\tINITRANS 2\n" +
                        "\t\tMAXTRANS 255\n" +
                        "\t\tTABLESPACE \"USERS\"\n" +
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
                        "LOGGING\n" +
                        "TABLESPACE \"USERS\"\n" +
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
                        ")\n" +
                        "LOB (\"COL9\") STORE AS BASICFILE (\n" +
                        "\tLOGGING\n" +
                        "\tTABLESPACE \"USERS\"\n" +
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
                        "\tENABLE STORAGE IN ROW\n" +
                        "\tCHUNK 8192\n" +
                        "\tNOCACHE\n" +
                        "\tRETENTION\n" +
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

        Assert.assertEquals(11, visitor.getColumns().size());

        Assert.assertTrue(visitor.containsColumn("SC_001.TB_001", "PK1"));
    }
}
