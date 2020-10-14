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

public class OracleCreateTableTest72 extends OracleTest {

    public void test_types() throws Exception {
        String sql = //
        " CREATE GLOBAL TEMPORARY TABLE \"RETL\".\"REFFERAL_MEMBER\" \n" +
                "   (\t\"ID\" NUMBER NOT NULL ENABLE, \n" +
                "\t\"GMT_CREATE\" DATE NOT NULL ENABLE, \n" +
                "\t\"GMT_MODIFIED\" DATE NOT NULL ENABLE, \n" +
                "\t\"FROM_NAME\" VARCHAR2(128) NOT NULL ENABLE, \n" +
                "\t\"FROM_EMAIL\" VARCHAR2(128) NOT NULL ENABLE, \n" +
                "\t\"TO_NAME\" VARCHAR2(128), \n" +
                "\t\"TO_EMAIL\" VARCHAR2(128) NOT NULL ENABLE, \n" +
                "\t\"MESSAGE\" VARCHAR2(512), \n" +
                "\t\"GMT_JOIN_MEMBER\" DATE, \n" +
                "\t\"SOURCE_CODE\" VARCHAR2(32) NOT NULL ENABLE, \n" +
                "\t\"IS_FROM_MEMBER\" CHAR(1) NOT NULL ENABLE, \n" +
                "\t\"IS_JOIN\" CHAR(1) NOT NULL ENABLE\n" +
                "   ) ON COMMIT DELETE ROWS    ";

        List<SQLStatement> statementList = SQLUtils.parseStatements(sql, JdbcConstants.ORACLE);
        SQLStatement stmt = statementList.get(0);
        print(statementList);

        assertEquals(1, statementList.size());
//
        assertEquals("CREATE GLOBAL TEMPORARY TABLE \"RETL\".\"REFFERAL_MEMBER\" (\n" +
                        "\t\"ID\" NUMBER NOT NULL ENABLE,\n" +
                        "\t\"GMT_CREATE\" DATE NOT NULL ENABLE,\n" +
                        "\t\"GMT_MODIFIED\" DATE NOT NULL ENABLE,\n" +
                        "\t\"FROM_NAME\" VARCHAR2(128) NOT NULL ENABLE,\n" +
                        "\t\"FROM_EMAIL\" VARCHAR2(128) NOT NULL ENABLE,\n" +
                        "\t\"TO_NAME\" VARCHAR2(128),\n" +
                        "\t\"TO_EMAIL\" VARCHAR2(128) NOT NULL ENABLE,\n" +
                        "\t\"MESSAGE\" VARCHAR2(512),\n" +
                        "\t\"GMT_JOIN_MEMBER\" DATE,\n" +
                        "\t\"SOURCE_CODE\" VARCHAR2(32) NOT NULL ENABLE,\n" +
                        "\t\"IS_FROM_MEMBER\" CHAR(1) NOT NULL ENABLE,\n" +
                        "\t\"IS_JOIN\" CHAR(1) NOT NULL ENABLE\n" +
                        ")\n" +
                        "ON COMMIT DELETE ROWS",//
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
