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

public class OracleCreateTableTest47 extends OracleTest {

    public void test_types() throws Exception {
        String sql = //
        "  CREATE TABLE \"SC_001\".\"TB_001\" \n" +
                "   (\t\"ID\" NUMBER, \n" +
                "\t\"IS_DELETED\" CHAR(1), \n" +
                "\t\"GMT_MODIFIED\" DATE, \n" +
                "\t\"GMT_CREATE\" DATE, \n" +
                "\t\"CREATOR\" VARCHAR2(32), \n" +
                "\t\"MODIFIER\" VARCHAR2(32), \n" +
                "\t\"SEND_TIME\" DATE, \n" +
                "\t\"SUBJECT\" VARCHAR2(256), \n" +
                "\t\"SENDER\" VARCHAR2(128), \n" +
                "\t\"PROVIDER\" VARCHAR2(16), \n" +
                "\t\"AVINFO_ID\" NUMBER, \n" +
                "\t\"CONTENT\" CLOB, \n" +
                "\t CONSTRAINT \"AV_EMAIL_PK\" PRIMARY KEY (\"ID\")\n" +
                "  USING INDEX PCTFREE 10 INITRANS 2 MAXTRANS 255 COMPUTE STATISTICS \n" +
                "  STORAGE(INITIAL 4194304 NEXT 1048576 MINEXTENTS 1 MAXEXTENTS 2147483645\n" +
                "  PCTINCREASE 0 FREELISTS 1 FREELIST GROUPS 1 BUFFER_POOL DEFAULT)\n" +
                "  TABLESPACE \"APPINDX1M\"  ENABLE\n" +
                "   ) PCTFREE 10 PCTUSED 40 INITRANS 1 MAXTRANS 255 NOCOMPRESS LOGGING\n" +
                "\n" +
                "  STORAGE(INITIAL 4194304 NEXT 1048576 MINEXTENTS 1 MAXEXTENTS 2147483645\n" +
                "  PCTINCREASE 0 FREELISTS 1 FREELIST GROUPS 1 BUFFER_POOL DEFAULT)\n" +
                "  TABLESPACE \"APPDATA1M\" \n" +
                " LOB (\"CONTENT\") STORE AS (\n" +
                "  TABLESPACE \"APPDATA1M\" ENABLE STORAGE IN ROW CHUNK 8192 PCTVERSION 10\n" +
                "  NOCACHE LOGGING \n" +
                "  STORAGE(INITIAL 4194304 NEXT 1048576 MINEXTENTS 1 MAXEXTENTS 2147483645\n" +
                "  PCTINCREASE 0 FREELISTS 1 FREELIST GROUPS 1 BUFFER_POOL DEFAULT))";

        OracleStatementParser parser = new OracleStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();
        SQLStatement stmt = statementList.get(0);
        print(statementList);

        Assert.assertEquals(1, statementList.size());

        Assert.assertEquals("CREATE TABLE \"SC_001\".\"TB_001\" (\n" +
                        "\t\"ID\" NUMBER,\n" +
                        "\t\"IS_DELETED\" CHAR(1),\n" +
                        "\t\"GMT_MODIFIED\" DATE,\n" +
                        "\t\"GMT_CREATE\" DATE,\n" +
                        "\t\"CREATOR\" VARCHAR2(32),\n" +
                        "\t\"MODIFIER\" VARCHAR2(32),\n" +
                        "\t\"SEND_TIME\" DATE,\n" +
                        "\t\"SUBJECT\" VARCHAR2(256),\n" +
                        "\t\"SENDER\" VARCHAR2(128),\n" +
                        "\t\"PROVIDER\" VARCHAR2(16),\n" +
                        "\t\"AVINFO_ID\" NUMBER,\n" +
                        "\t\"CONTENT\" CLOB,\n" +
                        "\tCONSTRAINT \"AV_EMAIL_PK\" PRIMARY KEY (\"ID\")\n" +
                        "\t\tUSING INDEX\n" +
                        "\t\tPCTFREE 10\n" +
                        "\t\tINITRANS 2\n" +
                        "\t\tMAXTRANS 255\n" +
                        "\t\tTABLESPACE \"APPINDX1M\"\n" +
                        "\t\tSTORAGE (\n" +
                        "\t\t\tINITIAL 4194304\n" +
                        "\t\t\tNEXT 1048576\n" +
                        "\t\t\tMINEXTENTS 1\n" +
                        "\t\t\tMAXEXTENTS 2147483645\n" +
                        "\t\t\tPCTINCREASE 0\n" +
                        "\t\t\tFREELISTS 1\n" +
                        "\t\t\tFREELIST GROUPS 1\n" +
                        "\t\t\tBUFFER_POOL DEFAULT\n" +
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
                        "TABLESPACE \"APPDATA1M\"\n" +
                        "STORAGE (\n" +
                        "\tINITIAL 4194304\n" +
                        "\tNEXT 1048576\n" +
                        "\tMINEXTENTS 1\n" +
                        "\tMAXEXTENTS 2147483645\n" +
                        "\tPCTINCREASE 0\n" +
                        "\tFREELISTS 1\n" +
                        "\tFREELIST GROUPS 1\n" +
                        "\tBUFFER_POOL DEFAULT\n" +
                        ")\n" +
                        "LOB (\"CONTENT\") STORE AS (\n" +
                        "\tLOGGING\n" +
                        "\tTABLESPACE \"APPDATA1M\"\n" +
                        "\tSTORAGE (\n" +
                        "\t\tINITIAL 4194304\n" +
                        "\t\tNEXT 1048576\n" +
                        "\t\tMINEXTENTS 1\n" +
                        "\t\tMAXEXTENTS 2147483645\n" +
                        "\t\tPCTINCREASE 0\n" +
                        "\t\tFREELISTS 1\n" +
                        "\t\tFREELIST GROUPS 1\n" +
                        "\t\tBUFFER_POOL DEFAULT\n" +
                        "\t)\n" +
                        "\tENABLE STORAGE IN ROW\n" +
                        "\tCHUNK 8192\n" +
                        "\tNOCACHE\n" +
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

        Assert.assertEquals(12, visitor.getColumns().size());

        Assert.assertTrue(visitor.containsColumn("SC_001.TB_001", "ID"));
    }
}
