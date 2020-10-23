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

public class OracleCreateTableTest48 extends OracleTest {

    public void test_types() throws Exception {
        String sql = //
        "  \n" +
                "  CREATE TABLE \"SC_001\".\"TB_001\" \n" +
                "   (  \"ID\" NUMBER, \n" +
                "  \"GMT_CREATE\" DATE, \n" +
                "  \"GMT_MODIFIED\" DATE, \n" +
                "  \"TEMPLATE\" VARCHAR2(64), \n" +
                "  \"COMPANY_ID\" NUMBER, \n" +
                "  \"COLUMN_NAME\" VARCHAR2(64), \n" +
                "  \"COLUMN_PROFILE\" VARCHAR2(4000), \n" +
                "  \"COLUMN_IMAGE\" VARCHAR2(128), \n" +
                "  \"ORDERING\" NUMBER, \n" +
                "  \"STATUS\" VARCHAR2(16), \n" +
                "  \"CREATE_TYPE\" VARCHAR2(256), \n" +
                "   CONSTRAINT \"COLUMNS_PK\" PRIMARY KEY (\"ID\")\n" +
                "  USING INDEX PCTFREE 10 INITRANS 2 MAXTRANS 255 COMPUTE STATISTICS \n" +
                "  STORAGE(INITIAL 65536 NEXT 1048576 MINEXTENTS 1 MAXEXTENTS 2147483645\n" +
                "  PCTINCREASE 0 FREELISTS 1 FREELIST GROUPS 1 BUFFER_POOL DEFAULT)\n" +
                "  TABLESPACE \"BOPSINDXTS\"  ENABLE\n" +
                "   ) PCTFREE 10 PCTUSED 40 INITRANS 1 MAXTRANS 255 NOCOMPRESS NOLOGGING\n" +
                "  STORAGE(INITIAL 65536 NEXT 1048576 MINEXTENTS 1 MAXEXTENTS 2147483645\n" +
                "  PCTINCREASE 0 FREELISTS 1 FREELIST GROUPS 1 BUFFER_POOL DEFAULT)\n" +
                "  TABLESPACE \"BOPSDATATS\" ";

        OracleStatementParser parser = new OracleStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();
        SQLStatement stmt = statementList.get(0);
        print(statementList);

        Assert.assertEquals(1, statementList.size());

        assertEquals("CREATE TABLE \"SC_001\".\"TB_001\" (\n" +
                        "\t\"ID\" NUMBER,\n" +
                        "\t\"GMT_CREATE\" DATE,\n" +
                        "\t\"GMT_MODIFIED\" DATE,\n" +
                        "\t\"TEMPLATE\" VARCHAR2(64),\n" +
                        "\t\"COMPANY_ID\" NUMBER,\n" +
                        "\t\"COLUMN_NAME\" VARCHAR2(64),\n" +
                        "\t\"COLUMN_PROFILE\" VARCHAR2(4000),\n" +
                        "\t\"COLUMN_IMAGE\" VARCHAR2(128),\n" +
                        "\t\"ORDERING\" NUMBER,\n" +
                        "\t\"STATUS\" VARCHAR2(16),\n" +
                        "\t\"CREATE_TYPE\" VARCHAR2(256),\n" +
                        "\tCONSTRAINT \"COLUMNS_PK\" PRIMARY KEY (\"ID\")\n" +
                        "\t\tUSING INDEX\n" +
                        "\t\tPCTFREE 10\n" +
                        "\t\tINITRANS 2\n" +
                        "\t\tMAXTRANS 255\n" +
                        "\t\tTABLESPACE \"BOPSINDXTS\"\n" +
                        "\t\tSTORAGE (\n" +
                        "\t\t\tINITIAL 65536\n" +
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
                        "NOLOGGING\n" +
                        "TABLESPACE \"BOPSDATATS\"\n" +
                        "STORAGE (\n" +
                        "\tINITIAL 65536\n" +
                        "\tNEXT 1048576\n" +
                        "\tMINEXTENTS 1\n" +
                        "\tMAXEXTENTS 2147483645\n" +
                        "\tPCTINCREASE 0\n" +
                        "\tFREELISTS 1\n" +
                        "\tFREELIST GROUPS 1\n" +
                        "\tBUFFER_POOL DEFAULT\n" +
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

        Assert.assertTrue(visitor.containsColumn("SC_001.TB_001", "ID"));
    }
}
