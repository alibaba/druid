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
package com.alibaba.druid.bvt.sql.oracle.visitor;

import java.util.List;

import static org.junit.Assert.*;
import junit.framework.TestCase;

import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.oracle.parser.OracleStatementParser;
import com.alibaba.druid.sql.dialect.oracle.visitor.OracleSchemaStatVisitor;
import com.alibaba.druid.stat.TableStat.Column;

public class OracleSchemaStatVisitorTest1 extends TestCase {
    public void test_0() throws Exception {
        String sql = "select a.name, b.name FROM users a, usergroups b on a.groupId = b.id";

        OracleStatementParser parser = new OracleStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();
        SQLStatement statemen = statementList.get(0);

        assertEquals(1, statementList.size());

        OracleSchemaStatVisitor visitor = new OracleSchemaStatVisitor();
        statemen.accept(visitor);

        System.out.println(sql);
        System.out.println("Tables : " + visitor.getTables());
        System.out.println("fields : " + visitor.getColumns());
        System.out.println("relationShip : " + visitor.getRelationships());

        assertEquals(2, visitor.getTables().size());
        assertEquals(true, visitor.containsTable("users"));
        assertEquals(true, visitor.containsTable("usergroups"));

        assertEquals(4, visitor.getColumns().size());
        assertEquals(true, visitor.getColumns().contains(new Column("users", "groupId")));
        assertEquals(true, visitor.getColumns().contains(new Column("users", "name")));
        assertEquals(true, visitor.getColumns().contains(new Column("usergroups", "id")));
        assertEquals(true, visitor.getColumns().contains(new Column("usergroups", "name")));

        assertEquals(1, visitor.getRelationships().size());
        assertEquals("users.groupId = usergroups.id", visitor.getRelationships().iterator().next().toString());

    }

    public void test_1() throws Exception {
        String sql = "select a.name, b.name FROM users a, usergroups b on a.groupId = b.id where a.groupID = ?";

        OracleStatementParser parser = new OracleStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();
        SQLStatement statemen = statementList.get(0);

        assertEquals(1, statementList.size());

        OracleSchemaStatVisitor visitor = new OracleSchemaStatVisitor();
        statemen.accept(visitor);

        System.out.println(sql);
        System.out.println("Tables : " + visitor.getTables());
        System.out.println("fields : " + visitor.getColumns());

        assertEquals(2, visitor.getTables().size());
        assertEquals(true, visitor.containsTable("users"));
        assertEquals(true, visitor.containsTable("usergroups"));

        assertEquals(4, visitor.getColumns().size());
        assertEquals(true, visitor.getColumns().contains(new Column("users", "groupId")));
        assertEquals(true, visitor.getColumns().contains(new Column("users", "name")));
        assertEquals(true, visitor.getColumns().contains(new Column("usergroups", "id")));
        assertEquals(true, visitor.getColumns().contains(new Column("usergroups", "name")));

    }
}
