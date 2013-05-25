/*
 * Copyright 1999-2011 Alibaba Group Holding Ltd.
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
package com.alibaba.druid.bvt.sql.mysql.visitor;

import java.util.List;

import junit.framework.TestCase;

import org.junit.Assert;

import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.mysql.parser.MySqlStatementParser;
import com.alibaba.druid.sql.dialect.mysql.visitor.MySqlSchemaStatVisitor;
import com.alibaba.druid.stat.TableStat.Column;

public class MySqlSchemaStatVisitorTest1 extends TestCase {

    public void test_0() throws Exception {
        String sql = "select a.name, b.name FROM users a, usergroups b on a.groupId = b.id";

        MySqlStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();
        SQLStatement statemen = statementList.get(0);

        Assert.assertEquals(1, statementList.size());

        MySqlSchemaStatVisitor visitor = new MySqlSchemaStatVisitor();
        statemen.accept(visitor);

        System.out.println(sql);
        System.out.println("Tables : " + visitor.getTables());
        System.out.println("fields : " + visitor.getColumns());

        Assert.assertEquals(2, visitor.getTables().size());
        Assert.assertEquals(true, visitor.containsTable("users"));
        Assert.assertEquals(true, visitor.containsTable("usergroups"));

        Assert.assertEquals(4, visitor.getColumns().size());
        Assert.assertEquals(true, visitor.getColumns().contains(new Column("users", "groupId")));
        Assert.assertEquals(true, visitor.getColumns().contains(new Column("users", "name")));
        Assert.assertEquals(true, visitor.getColumns().contains(new Column("usergroups", "id")));
        Assert.assertEquals(true, visitor.getColumns().contains(new Column("usergroups", "name")));

    }

    public void test_1() throws Exception {
        String sql = "select a.name, b.name FROM users a, usergroups b on a.groupId = b.id where a.groupID = ?";

        MySqlStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();
        SQLStatement statemen = statementList.get(0);

        Assert.assertEquals(1, statementList.size());

        MySqlSchemaStatVisitor visitor = new MySqlSchemaStatVisitor();
        statemen.accept(visitor);

        System.out.println(sql);
        System.out.println("Tables : " + visitor.getTables());
        System.out.println("fields : " + visitor.getColumns());

        Assert.assertEquals(2, visitor.getTables().size());
        Assert.assertEquals(true, visitor.containsTable("users"));
        Assert.assertEquals(true, visitor.containsTable("usergroups"));

        Assert.assertEquals(4, visitor.getColumns().size());
        Assert.assertEquals(true, visitor.getColumns().contains(new Column("users", "groupId")));
        Assert.assertEquals(true, visitor.getColumns().contains(new Column("users", "name")));
        Assert.assertEquals(true, visitor.getColumns().contains(new Column("usergroups", "id")));
        Assert.assertEquals(true, visitor.getColumns().contains(new Column("usergroups", "name")));

        Assert.assertTrue(visitor.getColumn("users", "name").isSelect());
        Assert.assertTrue(visitor.getColumn("usergroups", "name").isSelect());
        
        Assert.assertTrue(visitor.getColumn("users", "groupId").isJoin());
        Assert.assertTrue(visitor.getColumn("usergroups", "id").isJoin());
        
        Assert.assertTrue(visitor.getColumn("users", "groupId").isWhere());

    }
}
