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

public class SQLServerCreateTableTest_3 extends TestCase {

    public void test_0() throws Exception {
        String sql = "CREATE TABLE dbo.cwd_directory (" //
                + "ID NUMERIC NOT NULL, directory_name NVARCHAR(255), "//
                + "lower_directory_name NVARCHAR(255), created_date DATETIME, " //
                + "updated_date DATETIME, active int, description NVARCHAR(255), " //
                + "impl_class NVARCHAR(255), lower_impl_class NVARCHAR(255), " //
                + "directory_type NVARCHAR(60), directory_position NUMERIC, "//
                + "CONSTRAINT PK_cwd_directory PRIMARY KEY (ID))";

        SQLServerStatementParser parser = new SQLServerStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();
        SQLCreateTableStatement stmt = (SQLCreateTableStatement) statementList.get(0);

        Assert.assertEquals(1, statementList.size());

        Assert.assertEquals("CREATE TABLE dbo.cwd_directory (" //
                + "\n\tID NUMERIC NOT NULL,"//
                + "\n\tdirectory_name NVARCHAR(255),"//
                + "\n\tlower_directory_name NVARCHAR(255),"//
                + "\n\tcreated_date DATETIME,"//
                + "\n\tupdated_date DATETIME,"//
                + "\n\tactive int,"//
                + "\n\tdescription NVARCHAR(255),"//
                + "\n\timpl_class NVARCHAR(255),"//
                + "\n\tlower_impl_class NVARCHAR(255),"//
                + "\n\tdirectory_type NVARCHAR(60),"//
                + "\n\tdirectory_position NUMERIC,"//
                + "\n\tCONSTRAINT PK_cwd_directory PRIMARY KEY (ID)"//
                + "\n)", SQLUtils.toSQLString(stmt, JdbcConstants.SQL_SERVER));
        
        
        Assert.assertEquals("create table dbo.cwd_directory (" //
                            + "\n\tID NUMERIC not null,"//
                            + "\n\tdirectory_name NVARCHAR(255),"//
                            + "\n\tlower_directory_name NVARCHAR(255),"//
                            + "\n\tcreated_date DATETIME,"//
                            + "\n\tupdated_date DATETIME,"//
                            + "\n\tactive int,"//
                            + "\n\tdescription NVARCHAR(255),"//
                            + "\n\timpl_class NVARCHAR(255),"//
                            + "\n\tlower_impl_class NVARCHAR(255),"//
                            + "\n\tdirectory_type NVARCHAR(60),"//
                            + "\n\tdirectory_position NUMERIC,"//
                            + "\n\tconstraint PK_cwd_directory primary key (ID)"//
                            + "\n)", SQLUtils.toSQLString(stmt, JdbcConstants.SQL_SERVER, SQLUtils.DEFAULT_LCASE_FORMAT_OPTION));

        SQLServerSchemaStatVisitor visitor = new SQLServerSchemaStatVisitor();
        stmt.accept(visitor);

//        System.out.println("Tables : " + visitor.getTables());
//        System.out.println("fields : " + visitor.getColumns());
//        System.out.println("coditions : " + visitor.getConditions());
//        System.out.println("orderBy : " + visitor.getOrderByColumns());

        Assert.assertEquals(1, visitor.getTables().size());
        Assert.assertEquals(12, visitor.getColumns().size());
        Assert.assertEquals(0, visitor.getConditions().size());

        Assert.assertTrue(visitor.getTables().containsKey(new TableStat.Name("dbo.cwd_directory")));

        Assert.assertTrue(visitor.getColumns().contains(new Column("dbo.cwd_directory", "ID")));
        Assert.assertTrue(visitor.getColumns().contains(new Column("dbo.cwd_directory", "directory_name")));
        Assert.assertTrue(visitor.getColumns().contains(new Column("dbo.cwd_directory", "lower_directory_name")));
    }
}
