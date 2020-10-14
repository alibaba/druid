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

public class OracleCreateTableTest42 extends OracleTest {

    public void test_types() throws Exception {
        String sql = //
        "CREATE TABLE \"SC_001\".\"TB_001\" \n" +
                "   (  \"GROUP_NAME\" VARCHAR2(8) NOT NULL ENABLE, \n" +
                "  \"GROUP_KEY\" NUMBER(19,0) NOT NULL ENABLE, \n" +
                "  \"LOG_CMPLT_CSN\" VARCHAR2(128) NOT NULL ENABLE, \n" +
                "  \"LOG_CMPLT_XIDS_SEQ\" NUMBER(5,0) NOT NULL ENABLE, \n" +
                "  \"LOG_CMPLT_XIDS\" VARCHAR2(2000) NOT NULL ENABLE, \n" +
                "   PRIMARY KEY (\"GROUP_NAME\", \"GROUP_KEY\", \"LOG_CMPLT_CSN\", \"LOG_CMPLT_XIDS_SEQ\")\n" +
                "  USING INDEX PCTFREE 10 INITRANS 2 MAXTRANS 255 COMPUTE STATISTICS NOCOMPRESS LOGGING\n" +
                "  TABLESPACE \"USERS\"  ENABLE, \n" +
                "   SUPPLEMENTAL LOG GROUP \"GGS_15754\" (\"GROUP_NAME\", \"GROUP_KEY\", \"LOG_CMPLT_CSN\", \"LOG_CMPLT_XIDS_SEQ\") ALWAYS, \n" +
                "   SUPPLEMENTAL LOG DATA (PRIMARY KEY) COLUMNS, \n" +
                "   SUPPLEMENTAL LOG DATA (UNIQUE INDEX) COLUMNS, \n" +
                "   SUPPLEMENTAL LOG DATA (FOREIGN KEY) COLUMNS\n" +
                "   ) SEGMENT CREATION DEFERRED \n" +
                "  PCTFREE 10 PCTUSED 40 INITRANS 1 MAXTRANS 255 NOCOMPRESS LOGGING\n" +
                "  TABLESPACE \"USERS\" \n";

        OracleStatementParser parser = new OracleStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();
        SQLStatement stmt = statementList.get(0);
        print(statementList);

        Assert.assertEquals(1, statementList.size());

        Assert.assertEquals("CREATE TABLE \"SC_001\".\"TB_001\" (\n" +
                        "\t\"GROUP_NAME\" VARCHAR2(8) NOT NULL ENABLE,\n" +
                        "\t\"GROUP_KEY\" NUMBER(19, 0) NOT NULL ENABLE,\n" +
                        "\t\"LOG_CMPLT_CSN\" VARCHAR2(128) NOT NULL ENABLE,\n" +
                        "\t\"LOG_CMPLT_XIDS_SEQ\" NUMBER(5, 0) NOT NULL ENABLE,\n" +
                        "\t\"LOG_CMPLT_XIDS\" VARCHAR2(2000) NOT NULL ENABLE,\n" +
                        "\tPRIMARY KEY (\"GROUP_NAME\", \"GROUP_KEY\", \"LOG_CMPLT_CSN\", \"LOG_CMPLT_XIDS_SEQ\")\n" +
                        "\t\tUSING INDEX\n" +
                        "\t\tPCTFREE 10\n" +
                        "\t\tINITRANS 2\n" +
                        "\t\tMAXTRANS 255\n" +
                        "\t\tNOCOMPRESS\n" +
                        "\t\tLOGGING\n" +
                        "\t\tTABLESPACE \"USERS\"\n" +
                        "\t\tCOMPUTE STATISTICS\n" +
                        "\t\tENABLE,\n" +
                        "\tSUPPLEMENTAL LOG GROUP \"GGS_15754\" (\"GROUP_NAME\", \"GROUP_KEY\", \"LOG_CMPLT_CSN\", \"LOG_CMPLT_XIDS_SEQ\") ALWAYS,\n" +
                        "\tSUPPLEMENTAL LOG DATA (PRIMARY KEY) COLUMNS,\n" +
                        "\tSUPPLEMENTAL LOG DATA (UNIQUE INDEX) COLUMNS,\n" +
                        "\tSUPPLEMENTAL LOG DATA (FOREIGN KEY) COLUMNS\n" +
                        ")\n" +
                        "PCTFREE 10\n" +
                        "PCTUSED 40\n" +
                        "INITRANS 1\n" +
                        "MAXTRANS 255\n" +
                        "NOCOMPRESS\n" +
                        "LOGGING\n" +
                        "TABLESPACE \"USERS\"",//
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

        Assert.assertTrue(visitor.containsColumn("SC_001.TB_001", "GROUP_NAME"));
    }
}
