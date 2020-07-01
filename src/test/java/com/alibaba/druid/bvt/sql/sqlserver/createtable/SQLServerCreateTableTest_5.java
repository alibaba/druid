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
package com.alibaba.druid.bvt.sql.sqlserver.createtable;

import java.util.List;

import junit.framework.TestCase;

import org.junit.Assert;

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.statement.SQLCreateTableStatement;
import com.alibaba.druid.sql.dialect.sqlserver.parser.SQLServerStatementParser;
import com.alibaba.druid.sql.dialect.sqlserver.visitor.SQLServerSchemaStatVisitor;
import com.alibaba.druid.stat.TableStat;
import com.alibaba.druid.stat.TableStat.Column;
import com.alibaba.druid.util.JdbcConstants;

public class SQLServerCreateTableTest_5 extends TestCase {

    public void test_0() throws Exception {
        String sql = "CREATE TABLE dbo.AO_563AEE_ACTOR_ENTITY (" //
                     + "    FULL_NAME VARCHAR(255),"//
                     + "    ID INTEGER IDENTITY(1,1) NOT NULL,"//
                     + "    PROFILE_PAGE_URI VARCHAR(767),"//
                     + "    PROFILE_PICTURE_URI VARCHAR(767),"//
                     + "    USERNAME VARCHAR(255),"//
                     + ")";

        SQLServerStatementParser parser = new SQLServerStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();
        SQLCreateTableStatement stmt = (SQLCreateTableStatement) statementList.get(0);

        Assert.assertEquals(1, statementList.size());

        String output = SQLUtils.toSQLString(stmt, JdbcConstants.SQL_SERVER);
        Assert.assertEquals("CREATE TABLE dbo.AO_563AEE_ACTOR_ENTITY (" //
                            + "\n\tFULL_NAME VARCHAR(255),"//
                            + "\n\tID INTEGER DEFAULT NULL IDENTITY (1, 1),"//
                            + "\n\tPROFILE_PAGE_URI VARCHAR(767),"//
                            + "\n\tPROFILE_PICTURE_URI VARCHAR(767),"//
                            + "\n\tUSERNAME VARCHAR(255)"//
                            + "\n)", output);

        SQLServerSchemaStatVisitor visitor = new SQLServerSchemaStatVisitor();
        stmt.accept(visitor);

//        System.out.println("Tables : " + visitor.getTables());
//        System.out.println("fields : " + visitor.getColumns());
//        System.out.println("coditions : " + visitor.getConditions());
//        System.out.println("orderBy : " + visitor.getOrderByColumns());

        Assert.assertEquals(1, visitor.getTables().size());
        Assert.assertEquals(5, visitor.getColumns().size());
        Assert.assertEquals(0, visitor.getConditions().size());

        Assert.assertTrue(visitor.getTables().containsKey(new TableStat.Name("dbo.AO_563AEE_ACTOR_ENTITY")));

        Assert.assertTrue(visitor.getColumns().contains(new Column("dbo.AO_563AEE_ACTOR_ENTITY", "FULL_NAME")));
        Assert.assertTrue(visitor.getColumns().contains(new Column("dbo.AO_563AEE_ACTOR_ENTITY", "ID")));
        Assert.assertTrue(visitor.getColumns().contains(new Column("dbo.AO_563AEE_ACTOR_ENTITY", "USERNAME")));
    }
}
