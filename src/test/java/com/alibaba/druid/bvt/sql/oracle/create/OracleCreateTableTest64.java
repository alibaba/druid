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

public class OracleCreateTableTest64 extends OracleTest {

    public void test_types() throws Exception {
        String sql = //
        "   CREATE TABLE \"SC_001\".\"TB_001\" \n" +
                "   (\t\"STOREID\" VARCHAR2(40) NOT NULL ENABLE, \n" +
                "\t\"OPPTIME\" DATE, \n" +
                "\t\"CREATETIME\" DATE, \n" +
                "\t\"NAME\" VARCHAR2(254), \n" +
                "\t\"FILEPATH\" VARCHAR2(254), \n" +
                "\t\"FILENAME\" VARCHAR2(64), \n" +
                "\t\"EVENTCODE\" VARCHAR2(200), \n" +
                "\t\"FILECODE\" VARCHAR2(200), \n" +
                "\t\"RESULTCODE\" VARCHAR2(200), \n" +
                "\t\"RESULTMESSAGE\" VARCHAR2(4000), \n" +
                "\t\"RESULTMEMO\" VARCHAR2(200), \n" +
                "\t\"STATUSCODE\" VARCHAR2(200), \n" +
                "\t\"STATUSMESSAGE\" VARCHAR2(4000), \n" +
                "\t\"UPLOADSUCCESS\" VARCHAR2(200), \n" +
                "\t\"BZ\" VARCHAR2(4000), \n" +
                "\t\"DATACOUNT\" NUMBER, \n" +
                "\t\"FILECONTS\" CLOB, \n" +
                "\t\"XFJBH\" VARCHAR2(38)\n" +
                "   ) SEGMENT CREATION IMMEDIATE \n" +
                "  PCTFREE 10 PCTUSED 40 INITRANS 1 MAXTRANS 255 \n" +
                " NOCOMPRESS NOLOGGING\n" +
                "  STORAGE(INITIAL 65536 NEXT 1048576 MINEXTENTS 1 MAXEXTENTS 2147483645\n" +
                "  PCTINCREASE 0 FREELISTS 1 FREELIST GROUPS 1\n" +
                "  BUFFER_POOL DEFAULT FLASH_CACHE DEFAULT CELL_FLASH_CACHE DEFAULT)\n" +
                "  TABLESPACE \"XINFANG\" \n" +
                " LOB (\"FILECONTS\") STORE AS BASICFILE (\n" +
                "  TABLESPACE \"XINFANG\" ENABLE STORAGE IN ROW CHUNK 8192 RETENTION \n" +
                "  NOCACHE NOLOGGING \n" +
                "  STORAGE(INITIAL 65536 NEXT 1048576 MINEXTENTS 1 MAXEXTENTS 2147483645\n" +
                "  PCTINCREASE 0 FREELISTS 1 FREELIST GROUPS 1\n" +
                "  BUFFER_POOL DEFAULT FLASH_CACHE DEFAULT CELL_FLASH_CACHE DEFAULT))   ";

        OracleStatementParser parser = new OracleStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();
        SQLStatement stmt = statementList.get(0);
        print(statementList);

        Assert.assertEquals(1, statementList.size());

        Assert.assertEquals("CREATE TABLE \"SC_001\".\"TB_001\" (\n" +
                        "\t\"STOREID\" VARCHAR2(40) NOT NULL ENABLE,\n" +
                        "\t\"OPPTIME\" DATE,\n" +
                        "\t\"CREATETIME\" DATE,\n" +
                        "\t\"NAME\" VARCHAR2(254),\n" +
                        "\t\"FILEPATH\" VARCHAR2(254),\n" +
                        "\t\"FILENAME\" VARCHAR2(64),\n" +
                        "\t\"EVENTCODE\" VARCHAR2(200),\n" +
                        "\t\"FILECODE\" VARCHAR2(200),\n" +
                        "\t\"RESULTCODE\" VARCHAR2(200),\n" +
                        "\t\"RESULTMESSAGE\" VARCHAR2(4000),\n" +
                        "\t\"RESULTMEMO\" VARCHAR2(200),\n" +
                        "\t\"STATUSCODE\" VARCHAR2(200),\n" +
                        "\t\"STATUSMESSAGE\" VARCHAR2(4000),\n" +
                        "\t\"UPLOADSUCCESS\" VARCHAR2(200),\n" +
                        "\t\"BZ\" VARCHAR2(4000),\n" +
                        "\t\"DATACOUNT\" NUMBER,\n" +
                        "\t\"FILECONTS\" CLOB,\n" +
                        "\t\"XFJBH\" VARCHAR2(38)\n" +
                        ")\n" +
                        "PCTFREE 10\n" +
                        "PCTUSED 40\n" +
                        "INITRANS 1\n" +
                        "MAXTRANS 255\n" +
                        "NOCOMPRESS\n" +
                        "NOLOGGING\n" +
                        "TABLESPACE \"XINFANG\"\n" +
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
                        "LOB (\"FILECONTS\") STORE AS BASICFILE (\n" +
                        "\tNOLOGGING\n" +
                        "\tTABLESPACE \"XINFANG\"\n" +
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

        Assert.assertEquals(18, visitor.getColumns().size());

        Assert.assertTrue(visitor.containsColumn("SC_001.TB_001", "STOREID"));
    }
}
