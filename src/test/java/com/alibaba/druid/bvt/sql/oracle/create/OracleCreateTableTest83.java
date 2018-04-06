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
import com.alibaba.druid.sql.ast.statement.SQLCreateTableStatement;
import com.alibaba.druid.util.JdbcConstants;

import java.util.List;

public class OracleCreateTableTest83 extends OracleTest {

    public void test_types() throws Exception {
        String sql = //
                "CREATE TABLE NIRVANA.CS_MATURE_ADVICE (\n" +
                        "\tID DECIMAL(38) NOT NULL,\n" +
                        "\tGMT_CREATED TIMESTAMP(0) NOT NULL,\n" +
                        "\tGMT_MODIFIED TIMESTAMP(0),\n" +
                        "\tCREATOR VARCHAR(32) NOT NULL,\n" +
                        "\tMODIFIER VARCHAR(32),\n" +
                        "\tTYPE VARCHAR(32) NOT NULL,\n" +
                        "\tEXPLAIN VARCHAR(256),\n" +
                        "\tMATURITY_REF DECIMAL(38) NOT NULL,\n" +
                        "\tSERVICE VARCHAR(512) NOT NULL,\n" +
                        "\tPRIORITY BIGINT,\n" +
                        "\tREPOSITORY_URL VARCHAR(512),\n" +
                        "\tSERVICE_URL VARCHAR(512),\n" +
                        "\tPRIMARY KEY (ID)\n" +
                        ");";

        List<SQLStatement> statementList = SQLUtils.parseStatements(sql, JdbcConstants.ORACLE);
        SQLCreateTableStatement stmt = (SQLCreateTableStatement) statementList.get(0);
        print(statementList);

        assertEquals(1, statementList.size());
//
        assertEquals("CREATE TABLE NIRVANA.CS_MATURE_ADVICE (\n" +
                        "\tID DECIMAL(38) NOT NULL,\n" +
                        "\tGMT_CREATED TIMESTAMP(0) NOT NULL,\n" +
                        "\tGMT_MODIFIED TIMESTAMP(0),\n" +
                        "\tCREATOR VARCHAR(32) NOT NULL,\n" +
                        "\tMODIFIER VARCHAR(32),\n" +
                        "\tTYPE VARCHAR(32) NOT NULL,\n" +
                        "\tEXPLAIN VARCHAR(256),\n" +
                        "\tMATURITY_REF DECIMAL(38) NOT NULL,\n" +
                        "\tSERVICE VARCHAR(512) NOT NULL,\n" +
                        "\tPRIORITY BIGINT,\n" +
                        "\tREPOSITORY_URL VARCHAR(512),\n" +
                        "\tSERVICE_URL VARCHAR(512),\n" +
                        "\tPRIMARY KEY (ID)\n" +
                        ");",//
                            SQLUtils.toSQLString(stmt, JdbcConstants.ORACLE));

        stmt.renameColumn("ID", "FID");

        assertEquals("CREATE TABLE NIRVANA.CS_MATURE_ADVICE (\n" +
                        "\tFID DECIMAL(38) NOT NULL,\n" +
                        "\tGMT_CREATED TIMESTAMP(0) NOT NULL,\n" +
                        "\tGMT_MODIFIED TIMESTAMP(0),\n" +
                        "\tCREATOR VARCHAR(32) NOT NULL,\n" +
                        "\tMODIFIER VARCHAR(32),\n" +
                        "\tTYPE VARCHAR(32) NOT NULL,\n" +
                        "\tEXPLAIN VARCHAR(256),\n" +
                        "\tMATURITY_REF DECIMAL(38) NOT NULL,\n" +
                        "\tSERVICE VARCHAR(512) NOT NULL,\n" +
                        "\tPRIORITY BIGINT,\n" +
                        "\tREPOSITORY_URL VARCHAR(512),\n" +
                        "\tSERVICE_URL VARCHAR(512),\n" +
                        "\tPRIMARY KEY (ID)\n" +
                        ");",//
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
