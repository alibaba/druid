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

public class SQLServerCreateTableTest_6 extends TestCase {

    public void test_0() throws Exception {
        String sql = "CREATE TABLE dbo.AO_E8B6CC_ISSUE_MAPPING_V2 (" //
                     + "    AUTHOR VARCHAR(255),"//
                     + "    BRANCH VARCHAR(255),"//
                     + "    \"DATE\" DATETIME,"//
                     + "    FILES_DATA NTEXT,"//
                     + "    ID INTEGER IDENTITY(1,1) NOT NULL,"//
                     + "    ISSUE_ID VARCHAR(255),"//
                     + "    MESSAGE NTEXT,"//
                     + "    NODE VARCHAR(255),"//
                     + "    PARENTS_DATA VARCHAR(255),"//
                     + "    RAW_AUTHOR VARCHAR(255),"//
                     + "    RAW_NODE VARCHAR(255),"//
                     + "    REPOSITORY_ID INTEGER CONSTRAINT df_AO_E8B6CC_ISSUE_MAPPING_V2_REPOSITORY_ID DEFAULT 0,"//
                     + "    VERSION INTEGER,"//
                     + "CONSTRAINT pk_AO_E8B6CC_ISSUE_MAPPING_V2_ID PRIMARY KEY(ID)"//
                     + ")";

        SQLServerStatementParser parser = new SQLServerStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();
        SQLCreateTableStatement stmt = (SQLCreateTableStatement) statementList.get(0);

        Assert.assertEquals(1, statementList.size());

        String output = SQLUtils.toSQLString(stmt, JdbcConstants.SQL_SERVER);
        Assert.assertEquals("CREATE TABLE dbo.AO_E8B6CC_ISSUE_MAPPING_V2 (" //
                            + "\n\tAUTHOR VARCHAR(255),"//
                            + "\n\tBRANCH VARCHAR(255),"//
                            + "\n\t\"DATE\" DATETIME,"//
                            + "\n\tFILES_DATA NTEXT,"//
                            + "\n\tID INTEGER DEFAULT NULL IDENTITY (1, 1),"//
                            + "\n\tISSUE_ID VARCHAR(255),"//
                            + "\n\tMESSAGE NTEXT,"//
                            + "\n\tNODE VARCHAR(255),"//
                            + "\n\tPARENTS_DATA VARCHAR(255),"//
                            + "\n\tRAW_AUTHOR VARCHAR(255),"//
                            + "\n\tRAW_NODE VARCHAR(255),"//
                            + "\n\tREPOSITORY_ID INTEGER DEFAULT 0,"//
                            + "\n\tVERSION INTEGER,"//
                            + "\n\tCONSTRAINT pk_AO_E8B6CC_ISSUE_MAPPING_V2_ID PRIMARY KEY (ID)"//
                            + "\n)", output);

        SQLServerSchemaStatVisitor visitor = new SQLServerSchemaStatVisitor();
        stmt.accept(visitor);

//        System.out.println("Tables : " + visitor.getTables());
//        System.out.println("fields : " + visitor.getColumns());
//        System.out.println("coditions : " + visitor.getConditions());
//        System.out.println("orderBy : " + visitor.getOrderByColumns());

        Assert.assertEquals(1, visitor.getTables().size());
        Assert.assertEquals(14, visitor.getColumns().size());
        Assert.assertEquals(0, visitor.getConditions().size());

        Assert.assertTrue(visitor.getTables().containsKey(new TableStat.Name("dbo.AO_E8B6CC_ISSUE_MAPPING_V2")));

        Assert.assertTrue(visitor.getColumns().contains(new Column("dbo.AO_E8B6CC_ISSUE_MAPPING_V2", "AUTHOR")));
        Assert.assertTrue(visitor.getColumns().contains(new Column("dbo.AO_E8B6CC_ISSUE_MAPPING_V2", "BRANCH")));
        Assert.assertTrue(visitor.getColumns().contains(new Column("dbo.AO_E8B6CC_ISSUE_MAPPING_V2", "VERSION")));
    }
}
