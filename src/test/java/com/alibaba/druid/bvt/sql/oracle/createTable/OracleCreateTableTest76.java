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
import com.alibaba.druid.util.JdbcConstants;

import java.util.List;

public class OracleCreateTableTest76 extends OracleTest {

    public void test_types() throws Exception {
        String sql = //
        " CREATE TABLE \"TCC_CPR\".\"KTV_CONFIG_SCALE_DETAILS_TEMP\" \n" +
                "   (\t\"ID\" NUMBER(10,0) NOT NULL ENABLE, \n" +
                "\t\"CONTRACT_HEAD_ID\" NUMBER(10,0) NOT NULL ENABLE, \n" +
                "\t\"PRODUCT_BIG_ID\" NUMBER(10,0), \n" +
                "\t\"PRODUCT_ID\" NUMBER(10,0), \n" +
                "\t\"SCALE\" NUMBER(10,2), \n" +
                "\t\"ENABLED_FLAG\" CHAR(1), \n" +
                "\t\"CREATED_BY\" NUMBER(10,0), \n" +
                "\t\"CREATION_DATE\" TIMESTAMP (6), \n" +
                "\t\"LAST_UPDATED_BY\" NUMBER(10,0), \n" +
                "\t\"LAST_UPDATE_DATE\" TIMESTAMP (6), \n" +
                "\t\"CON_TYPE\" VARCHAR2(2), \n" +
                "\t CONSTRAINT \"P_ID\" PRIMARY KEY (\"ID\") DEFERRABLE INITIALLY DEFERRED\n" +
                "  USING INDEX PCTFREE 10 INITRANS 2 MAXTRANS 255 COMPUTE STATISTICS \n" +
                "  STORAGE(INITIAL 65536 NEXT 1048576 MINEXTENTS 1 MAXEXTENTS 2147483645\n" +
                "  PCTINCREASE 0 FREELISTS 1 FREELIST GROUPS 1 BUFFER_POOL DEFAULT)\n" +
                "  TABLESPACE \"TCC_CPR_TSP\"  ENABLE\n" +
                "   ) PCTFREE 10 PCTUSED 40 INITRANS 1 MAXTRANS 255 NOCOMPRESS LOGGING\n" +
                "  STORAGE(INITIAL 65536 NEXT 1048576 MINEXTENTS 1 MAXEXTENTS 2147483645\n" +
                "  PCTINCREASE 0 FREELISTS 1 FREELIST GROUPS 1 BUFFER_POOL DEFAULT)\n" +
                "  TABLESPACE \"TCC_CPR_TSP\"  ";

        List<SQLStatement> statementList = SQLUtils.parseStatements(sql, JdbcConstants.ORACLE);
        SQLStatement stmt = statementList.get(0);
        print(statementList);

        assertEquals(1, statementList.size());
//
        assertEquals("CREATE TABLE \"TCC_CPR\".\"KTV_CONFIG_SCALE_DETAILS_TEMP\" (\n" +
                        "\t\"ID\" NUMBER(10, 0) NOT NULL ENABLE,\n" +
                        "\t\"CONTRACT_HEAD_ID\" NUMBER(10, 0) NOT NULL ENABLE,\n" +
                        "\t\"PRODUCT_BIG_ID\" NUMBER(10, 0),\n" +
                        "\t\"PRODUCT_ID\" NUMBER(10, 0),\n" +
                        "\t\"SCALE\" NUMBER(10, 2),\n" +
                        "\t\"ENABLED_FLAG\" CHAR(1),\n" +
                        "\t\"CREATED_BY\" NUMBER(10, 0),\n" +
                        "\t\"CREATION_DATE\" TIMESTAMP(6),\n" +
                        "\t\"LAST_UPDATED_BY\" NUMBER(10, 0),\n" +
                        "\t\"LAST_UPDATE_DATE\" TIMESTAMP(6),\n" +
                        "\t\"CON_TYPE\" VARCHAR2(2),\n" +
                        "\tCONSTRAINT \"P_ID\" PRIMARY KEY (\"ID\")\n" +
                        "\t\tUSING INDEX\n" +
                        "\t\tPCTFREE 10\n" +
                        "\t\tINITRANS 2\n" +
                        "\t\tMAXTRANS 255\n" +
                        "\t\tTABLESPACE \"TCC_CPR_TSP\"\n" +
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
                        "\t\tENABLE INITIALLY DEFERRED DEFERRABLE\n" +
                        ")\n" +
                        "PCTFREE 10\n" +
                        "PCTUSED 40\n" +
                        "INITRANS 1\n" +
                        "MAXTRANS 255\n" +
                        "NOCOMPRESS\n" +
                        "LOGGING\n" +
                        "TABLESPACE \"TCC_CPR_TSP\"\n" +
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
