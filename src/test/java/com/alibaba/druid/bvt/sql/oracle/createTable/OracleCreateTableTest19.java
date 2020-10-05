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

public class OracleCreateTableTest19 extends OracleTest {

    public void test_types() throws Exception {
        String sql = //
        "   CREATE TABLE \"SONAR\".\"ACTIVE_DASHBOARDS\" " //
                + "   (    \"ID\" NUMBER(38,0) NOT NULL ENABLE, " //
                + "    \"DASHBOARD_ID\" NUMBER(38,0) NOT NULL ENABLE, " //
                + "    \"USER_ID\" NUMBER(38,0), " //
                + "    \"ORDER_INDEX\" NUMBER(38,0), " //
                + "     PRIMARY KEY (\"ID\")" //
                + "  USING INDEX PCTFREE 10 INITRANS 2 MAXTRANS 255 COMPUTE STATISTICS " //
                + "  STORAGE(INITIAL 65536 NEXT 1048576 MINEXTENTS 1 MAXEXTENTS 2147483645" //
                + "  PCTINCREASE 0 FREELISTS 1 FREELIST GROUPS 1" //
                + "  BUFFER_POOL DEFAULT FLASH_CACHE DEFAULT CELL_FLASH_CACHE DEFAULT)" //
                + "  TABLESPACE \"USERS\"  ENABLE" //
                + "   ) SEGMENT CREATION IMMEDIATE " //
                + "  PCTFREE 10 PCTUSED 40 INITRANS 1 MAXTRANS 255 " //
                + " NOCOMPRESS LOGGING" //
                + "  STORAGE(INITIAL 65536 NEXT 1048576 MINEXTENTS 1 MAXEXTENTS 2147483645" //
                + "  PCTINCREASE 0 FREELISTS 1 FREELIST GROUPS 1" //
                + "  BUFFER_POOL DEFAULT FLASH_CACHE DEFAULT CELL_FLASH_CACHE DEFAULT)" //
                + "  TABLESPACE \"USERS\" ;";

        OracleStatementParser parser = new OracleStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();
        SQLStatement statement = statementList.get(0);
        print(statementList);

        assertEquals(1, statementList.size());

        assertEquals("CREATE TABLE \"SONAR\".\"ACTIVE_DASHBOARDS\" (\n" +
                        "\t\"ID\" NUMBER(38, 0) NOT NULL ENABLE,\n" +
                        "\t\"DASHBOARD_ID\" NUMBER(38, 0) NOT NULL ENABLE,\n" +
                        "\t\"USER_ID\" NUMBER(38, 0),\n" +
                        "\t\"ORDER_INDEX\" NUMBER(38, 0),\n" +
                        "\tPRIMARY KEY (\"ID\")\n" +
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
                        ");", //
                            SQLUtils.toSQLString(statement, JdbcConstants.ORACLE));

        OracleSchemaStatVisitor visitor = new OracleSchemaStatVisitor();
        statement.accept(visitor);

        System.out.println("Tables : " + visitor.getTables());
        System.out.println("fields : " + visitor.getColumns());
        System.out.println("coditions : " + visitor.getConditions());
        System.out.println("relationships : " + visitor.getRelationships());
        System.out.println("orderBy : " + visitor.getOrderByColumns());

        Assert.assertEquals(1, visitor.getTables().size());

        Assert.assertEquals(4, visitor.getColumns().size());

        Assert.assertTrue(visitor.containsColumn("SONAR.ACTIVE_DASHBOARDS", "ID"));
    }
}
