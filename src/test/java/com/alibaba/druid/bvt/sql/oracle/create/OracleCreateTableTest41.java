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
import com.alibaba.druid.sql.dialect.oracle.parser.OracleStatementParser;
import com.alibaba.druid.sql.dialect.oracle.visitor.OracleSchemaStatVisitor;
import com.alibaba.druid.stat.TableStat;
import com.alibaba.druid.util.JdbcConstants;
import org.junit.Assert;

import java.util.List;

public class OracleCreateTableTest41 extends OracleTest {

    public void test_types() throws Exception {
        String sql = //
        "  CREATE TABLE \"SC_001\".\"TB_001\" \n" +
                "   (\t\"NUM\" NUMBER(2,0), \n" +
                "\t\"BLOB_COL\" BLOB, \n" +
                "\t\"CLOB_COL\" CLOB, \n" +
                "\t\"CH\" VARCHAR2(55), \n" +
                "\t CONSTRAINT \"PK_A\" PRIMARY KEY (\"NUM\")\n" +
                "  USING INDEX PCTFREE 10 INITRANS 2 MAXTRANS 255 COMPUTE STATISTICS \n" +
                "  STORAGE(INITIAL 65536 NEXT 1048576 MINEXTENTS 1 MAXEXTENTS 2147483645\n" +
                "  PCTINCREASE 0 FREELISTS 1 FREELIST GROUPS 1 BUFFER_POOL DEFAULT FLASH_CACHE DEFAULT CELL_FLASH_CACHE DEFAULT)\n" +
                "  TABLESPACE \"USERS\"  ENABLE, \n" +
                "\t SUPPLEMENTAL LOG GROUP \"GGS_16346\" (\"NUM\") ALWAYS, \n" +
                "\t SUPPLEMENTAL LOG DATA (PRIMARY KEY) COLUMNS, \n" +
                "\t SUPPLEMENTAL LOG DATA (UNIQUE INDEX) COLUMNS, \n" +
                "\t SUPPLEMENTAL LOG DATA (FOREIGN KEY) COLUMNS, \n" +
                "\t SUPPLEMENTAL LOG DATA (ALL) COLUMNS, \n" +
                "\t SUPPLEMENTAL LOG GROUP \"ALLINS_POLNO\" (\"NUM\") ALWAYS\n" +
                "   ) SEGMENT CREATION IMMEDIATE \n" +
                "  PCTFREE 10 PCTUSED 40 INITRANS 1 MAXTRANS 255 NOCOMPRESS LOGGING\n" +
                "  STORAGE(INITIAL 65536 NEXT 1048576 MINEXTENTS 1 MAXEXTENTS 2147483645\n" +
                "  PCTINCREASE 0 FREELISTS 1 FREELIST GROUPS 1 BUFFER_POOL DEFAULT FLASH_CACHE DEFAULT CELL_FLASH_CACHE DEFAULT)\n" +
                "  TABLESPACE \"USERS\" \n" +
                " LOB (\"BLOB_COL\") STORE AS BASICFILE (\n" +
                "  TABLESPACE \"USERS\" ENABLE STORAGE IN ROW CHUNK 8192 RETENTION \n" +
                "  NOCACHE LOGGING \n" +
                "  STORAGE(INITIAL 65536 NEXT 1048576 MINEXTENTS 1 MAXEXTENTS 2147483645\n" +
                "  PCTINCREASE 0 FREELISTS 1 FREELIST GROUPS 1 BUFFER_POOL DEFAULT FLASH_CACHE DEFAULT CELL_FLASH_CACHE DEFAULT)) \n" +
                " LOB (\"CLOB_COL\") STORE AS BASICFILE (\n" +
                "  TABLESPACE \"USERS\" ENABLE STORAGE IN ROW CHUNK 8192 RETENTION \n" +
                "  NOCACHE LOGGING \n" +
                "  STORAGE(INITIAL 65536 NEXT 1048576 MINEXTENTS 1 MAXEXTENTS 2147483645\n" +
                "  PCTINCREASE 0 FREELISTS 1 FREELIST GROUPS 1 BUFFER_POOL DEFAULT FLASH_CACHE DEFAULT CELL_FLASH_CACHE DEFAULT)) ";

//        System.out.println(sql);

        OracleStatementParser parser = new OracleStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();
        SQLStatement stmt = statementList.get(0);
        print(statementList);

        Assert.assertEquals(1, statementList.size());

        Assert.assertEquals("CREATE TABLE \"SC_001\".\"TB_001\" (\n" +
                        "\t\"NUM\" NUMBER(2, 0),\n" +
                        "\t\"BLOB_COL\" BLOB,\n" +
                        "\t\"CLOB_COL\" CLOB,\n" +
                        "\t\"CH\" VARCHAR2(55),\n" +
                        "\tCONSTRAINT \"PK_A\" PRIMARY KEY (\"NUM\")\n" +
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
                        "\t\tENABLE,\n" +
                        "\tSUPPLEMENTAL LOG GROUP \"GGS_16346\" (\"NUM\") ALWAYS,\n" +
                        "\tSUPPLEMENTAL LOG DATA (PRIMARY KEY) COLUMNS,\n" +
                        "\tSUPPLEMENTAL LOG DATA (UNIQUE INDEX) COLUMNS,\n" +
                        "\tSUPPLEMENTAL LOG DATA (FOREIGN KEY) COLUMNS,\n" +
                        "\tSUPPLEMENTAL LOG DATA (ALL) COLUMNS,\n" +
                        "\tSUPPLEMENTAL LOG GROUP \"ALLINS_POLNO\" (\"NUM\") ALWAYS\n" +
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
                        "LOB (\"CLOB_COL\") STORE AS BASICFILE (\n" +
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

        Assert.assertEquals(6, visitor.getColumns().size());

        Assert.assertTrue(visitor.containsColumn("SC_001.TB_001", "BLOB_COL"));
    }
}
