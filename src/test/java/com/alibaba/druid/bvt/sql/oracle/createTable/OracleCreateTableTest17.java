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
import com.alibaba.druid.sql.dialect.oracle.parser.OracleStatementParser;
import com.alibaba.druid.sql.dialect.oracle.visitor.OracleSchemaStatVisitor;
import com.alibaba.druid.stat.TableStat;
import com.alibaba.druid.util.JdbcConstants;
import org.junit.Assert;

import java.util.List;

public class OracleCreateTableTest17 extends OracleTest {

    public void test_types() throws Exception {
        String sql = //
        "create table ACT_HI_PROCINST ( "//
                + "ID_ NVARCHAR2(64) not null, "//
                + "PROC_INST_ID_ NVARCHAR2(64) not null, "//
                + "BUSINESS_KEY_ NVARCHAR2(255), "//
                + "PROC_DEF_ID_ NVARCHAR2(64) not null, "//
                + "START_TIME_ TIMESTAMP(6) not null, "//
                + "END_TIME_ TIMESTAMP(6), "//
                + "DURATION_ NUMBER(19,0), "//
                + "START_USER_ID_ NVARCHAR2(255), "//
                + "START_ACT_ID_ NVARCHAR2(255), "//
                + "END_ACT_ID_ NVARCHAR2(255), "//
                + "SUPER_PROCESS_INSTANCE_ID_ NVARCHAR2(64), "//
                + "DELETE_REASON_ NVARCHAR2(2000), "//
                + "primary key (ID_), "//
                + "unique (PROC_INST_ID_) "//
                + ")";

        OracleStatementParser parser = new OracleStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();
        SQLStatement statement = statementList.get(0);
        print(statementList);

        Assert.assertEquals(1, statementList.size());

        Assert.assertEquals("CREATE TABLE ACT_HI_PROCINST ("
                + "\n\tID_ NVARCHAR2(64) NOT NULL,"
                + "\n\tPROC_INST_ID_ NVARCHAR2(64) NOT NULL,"
                + "\n\tBUSINESS_KEY_ NVARCHAR2(255),"
                + "\n\tPROC_DEF_ID_ NVARCHAR2(64) NOT NULL,"
                + "\n\tSTART_TIME_ TIMESTAMP(6) NOT NULL,"
                + "\n\tEND_TIME_ TIMESTAMP(6),"
                + "\n\tDURATION_ NUMBER(19, 0),"
                + "\n\tSTART_USER_ID_ NVARCHAR2(255),"
                + "\n\tSTART_ACT_ID_ NVARCHAR2(255),"
                + "\n\tEND_ACT_ID_ NVARCHAR2(255),"
                + "\n\tSUPER_PROCESS_INSTANCE_ID_ NVARCHAR2(64),"
                + "\n\tDELETE_REASON_ NVARCHAR2(2000),"
                + "\n\tPRIMARY KEY (ID_),"
                + "\n\tUNIQUE (PROC_INST_ID_)"
                + "\n)", SQLUtils.toSQLString(statement, JdbcConstants.ORACLE));

        OracleSchemaStatVisitor visitor = new OracleSchemaStatVisitor();
        statement.accept(visitor);

        System.out.println("Tables : " + visitor.getTables());
        System.out.println("fields : " + visitor.getColumns());
        System.out.println("coditions : " + visitor.getConditions());
        System.out.println("relationships : " + visitor.getRelationships());
        System.out.println("orderBy : " + visitor.getOrderByColumns());

        Assert.assertEquals(1, visitor.getTables().size());

        Assert.assertEquals(12, visitor.getColumns().size());

        Assert.assertTrue(visitor.getColumns().contains(new TableStat.Column("ACT_HI_PROCINST", "ID_")));
    }
}
