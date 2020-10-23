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

public class OracleCreateTableTest96 extends OracleTest {

    public void test_0() throws Exception {
        String sql = //
                "CREATE TABLE \"CAOBOHAHA_IAU\".\"IAU_USERSESSION\" \n" +
                        "   (    \"IAU_ID\" NUMBER, \n" +
                        "    \"IAU_AUTHENTICATIONMETHOD\" VARCHAR2(255), \n" +
                        "     PRIMARY KEY (\"IAU_ID\")\n" +
                        "  USING INDEX (CREATE INDEX \"CAOBOHAHA_IAU\".\"DYN_IAU_USERSESSION_INDEX\" ON \"CAOBOHAHA_IAU\".\"IAU_USERSESSION\" (\"IAU_ID\") \n" +
                        "  PCTFREE 10 INITRANS 2 MAXTRANS 255 COMPUTE STATISTICS \n" +
                        "  TABLESPACE \"CAOBOHAHA_IAU\" )  ENABLE\n" +
                        "   ) SEGMENT CREATION DEFERRED \n" +
                        "  PCTFREE 10 PCTUSED 40 INITRANS 1 MAXTRANS 255 \n" +
                        " NOCOMPRESS LOGGING\n" +
                        "  TABLESPACE \"CAOBOHAHA_IAU\" ";

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

        assertEquals("CREATE TABLE \"CAOBOHAHA_IAU\".\"IAU_USERSESSION\" (\n" +
                "\t\"IAU_ID\" NUMBER,\n" +
                "\t\"IAU_AUTHENTICATIONMETHOD\" VARCHAR2(255),\n" +
                "\tPRIMARY KEY (\"IAU_ID\")\n" +
                "\t\tUSING INDEX (CREATE INDEX \"CAOBOHAHA_IAU\".\"DYN_IAU_USERSESSION_INDEX\" ON \"CAOBOHAHA_IAU\".\"IAU_USERSESSION\"(\"IAU_ID\")\n" +
                "\t\tCOMPUTE STATISTICS\n" +
                "\t\tPCTFREE 10\n" +
                "\t\tINITRANS 2\n" +
                "\t\tMAXTRANS 255\n" +
                "\t\tTABLESPACE \"CAOBOHAHA_IAU\")\n" +
                "\t\tENABLE\n" +
                ")\n" +
                "PCTFREE 10\n" +
                "PCTUSED 40\n" +
                "INITRANS 1\n" +
                "MAXTRANS 255\n" +
                "NOCOMPRESS\n" +
                "LOGGING\n" +
                "TABLESPACE \"CAOBOHAHA_IAU\"", stmt.toString());

    }
}
