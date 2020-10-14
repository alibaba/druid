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
package com.alibaba.druid.bvt.sql.hive;

import com.alibaba.druid.sql.OracleTest;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.visitor.SchemaStatVisitor;
import com.alibaba.druid.util.JdbcConstants;
import org.junit.Assert;

import java.util.List;

public class HiveCreateTableTest_32_properties extends OracleTest {

    public void test_0() throws Exception {
        String sql = //
                "create table aaaa (\n" +
                        "  id int not null COLPROPERTIES (name='pk',format='yyy',charset='utf8',type='string'）\n" +
                        ") MAPPED by (name='AAAA')"; //

        List<SQLStatement> statementList = SQLUtils.toStatementList(sql, JdbcConstants.HIVE);
        SQLStatement stmt = statementList.get(0);
        System.out.println(stmt.toString());

        Assert.assertEquals(1, statementList.size());

        SchemaStatVisitor visitor = SQLUtils.createSchemaStatVisitor(JdbcConstants.HIVE);
        stmt.accept(visitor);

        assertEquals("CREATE TABLE aaaa (\n" +
                "\tid int NOT NULL COLPROPERTIES (name = 'pk', format = 'yyy', charset = 'utf8', type = 'string')\n" +
                ")\n" +
                "MAPPED BY (name = 'AAAA')", stmt.toString());

        assertEquals("CREATE TABLE aaaa (\n" +
                "\tid int NOT NULL COLPROPERTIES (name = 'pk', format = 'yyy', charset = 'utf8', type = 'string')\n" +
                ")\n" +
                "MAPPED BY (name = 'AAAA')", stmt.clone().toString());

        assertEquals("create table aaaa (\n" +
                "\tid int not null colproperties (name = 'pk', format = 'yyy', charset = 'utf8', type = 'string')\n" +
                ")\n" +
                "mapped by (name = 'AAAA')", stmt.toLowerCaseString());

        System.out.println("Tables : " + visitor.getTables());
        System.out.println("fields : " + visitor.getColumns());
        System.out.println("coditions : " + visitor.getConditions());
        System.out.println("relationships : " + visitor.getRelationships());
        System.out.println("orderBy : " + visitor.getOrderByColumns());

        assertEquals(1, visitor.getTables().size());
        assertEquals(1, visitor.getColumns().size());
        assertEquals(0, visitor.getConditions().size());
        assertEquals(0, visitor.getRelationships().size());
        assertEquals(0, visitor.getOrderByColumns().size());

        assertTrue(visitor.containsTable("aaaa"));

    }

    public void test_mysql() throws Exception {
        String sql = //
                "create table aaaa (\n" +
                        "  id int not null COLPROPERTIES (name='pk',format='yyy',charset='utf8',type='string'）\n" +
                        ")"; //

        List<SQLStatement> statementList = SQLUtils.toStatementList(sql, JdbcConstants.MYSQL);
        SQLStatement stmt = statementList.get(0);
        System.out.println(stmt.toString());

        Assert.assertEquals(1, statementList.size());

        SchemaStatVisitor visitor = SQLUtils.createSchemaStatVisitor(JdbcConstants.MYSQL);
        stmt.accept(visitor);

        assertEquals("CREATE TABLE aaaa (\n" +
                "\tid int NOT NULL COLPROPERTIES (name = 'pk', format = 'yyy', CHARACTER SET 'utf8', type = 'string')\n" +
                ")", stmt.toString());

        assertEquals("CREATE TABLE aaaa (\n" +
                "\tid int NOT NULL COLPROPERTIES (name = 'pk', format = 'yyy', CHARACTER SET 'utf8', type = 'string')\n" +
                ")", stmt.clone().toString());

        assertEquals("create table aaaa (\n" +
                "\tid int not null colproperties (name = 'pk', format = 'yyy', character set 'utf8', type = 'string')\n" +
                ")", stmt.toLowerCaseString());

        System.out.println("Tables : " + visitor.getTables());
        System.out.println("fields : " + visitor.getColumns());
        System.out.println("coditions : " + visitor.getConditions());
        System.out.println("relationships : " + visitor.getRelationships());
        System.out.println("orderBy : " + visitor.getOrderByColumns());

        assertEquals(1, visitor.getTables().size());
        assertEquals(1, visitor.getColumns().size());
        assertEquals(0, visitor.getConditions().size());
        assertEquals(0, visitor.getRelationships().size());
        assertEquals(0, visitor.getOrderByColumns().size());

        assertTrue(visitor.containsTable("aaaa"));

    }

}
