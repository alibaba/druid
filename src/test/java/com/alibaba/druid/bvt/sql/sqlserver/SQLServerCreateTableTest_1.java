/*
 * Copyright 1999-2101 Alibaba Group Holding Ltd.
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
package com.alibaba.druid.bvt.sql.sqlserver;

import java.util.List;

import junit.framework.TestCase;

import org.junit.Assert;

import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.statement.SQLCreateTableStatement;
import com.alibaba.druid.sql.dialect.sqlserver.parser.SQLServerStatementParser;
import com.alibaba.druid.sql.dialect.sqlserver.visitor.SQLServerSchemaStatVisitor;
import com.alibaba.druid.stat.TableStat;
import com.alibaba.druid.stat.TableStat.Column;

public class SQLServerCreateTableTest_1 extends TestCase {

    public void test_0() throws Exception {
        String sql = "CREATE TABLE [projects] ("//
                     + "[id] int NOT NULL IDENTITY(1, 1) PRIMARY KEY, "//
                     + "[name] NVARCHAR(256), [description] NVARCHAR(2000), "//
                     + "[enabled] bit DEFAULT 1 NOT NULL, "//
                     + "[scope] NVARCHAR(3), "//
                     + "[qualifier] NVARCHAR(3), "//
                     + "[kee] NVARCHAR(400), "//
                     + "[root_id] int, " //
                     + "[profile_id] int, " //
                     + "[language] NVARCHAR(5), "//
                     + "[copy_resource_id] int, "//
                     + "[long_name] NVARCHAR(256)) ";

        SQLServerStatementParser parser = new SQLServerStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();
        SQLCreateTableStatement stmt = (SQLCreateTableStatement) statementList.get(0);

        Assert.assertEquals(1, statementList.size());

        SQLServerSchemaStatVisitor visitor = new SQLServerSchemaStatVisitor();
        stmt.accept(visitor);

        System.out.println("Tables : " + visitor.getTables());
        System.out.println("fields : " + visitor.getColumns());
        System.out.println("coditions : " + visitor.getConditions());
        System.out.println("orderBy : " + visitor.getOrderByColumns());

        Assert.assertEquals(1, visitor.getTables().size());
        Assert.assertEquals(12, visitor.getColumns().size());
        Assert.assertEquals(0, visitor.getConditions().size());

        Assert.assertTrue(visitor.getTables().containsKey(new TableStat.Name("projects")));

        Assert.assertTrue(visitor.getColumns().contains(new Column("projects", "id")));
        Assert.assertTrue(visitor.getColumns().contains(new Column("projects", "name")));
        Assert.assertTrue(visitor.getColumns().contains(new Column("projects", "enabled")));
        Assert.assertTrue(visitor.getColumns().contains(new Column("projects", "scope")));
    }
}
