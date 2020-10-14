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

public class OracleCreateTableTest79 extends OracleTest {

    public void test_types() throws Exception {
        String sql = //
                "CREATE TABLE rules (id NUMBER(38) NOT NULL PRIMARY KEY, name varchar(200), plugin_rule_key varchar(200) NOT NULL, plugin_config_key varchar(200), plugin_name varchar(255) NOT NULL, description source, priority NUMBER(38), cardinality varchar(10), parent_id NUMBER(38), status varchar(40), language varchar(20), created_at TIMESTAMP, updated_at TIMESTAMP) ";

        List<SQLStatement> statementList = SQLUtils.parseStatements(sql, JdbcConstants.ORACLE);
        SQLStatement stmt = statementList.get(0);
        print(statementList);

        assertEquals(1, statementList.size());
//
        assertEquals("CREATE TABLE rules (\n" +
                        "\tid NUMBER(38) NOT NULL PRIMARY KEY,\n" +
                        "\tname varchar(200),\n" +
                        "\tplugin_rule_key varchar(200) NOT NULL,\n" +
                        "\tplugin_config_key varchar(200),\n" +
                        "\tplugin_name varchar(255) NOT NULL,\n" +
                        "\tdescription source,\n" +
                        "\tpriority NUMBER(38),\n" +
                        "\tcardinality varchar(10),\n" +
                        "\tparent_id NUMBER(38),\n" +
                        "\tstatus varchar(40),\n" +
                        "\tlanguage varchar(20),\n" +
                        "\tcreated_at TIMESTAMP,\n" +
                        "\tupdated_at TIMESTAMP\n" +
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
