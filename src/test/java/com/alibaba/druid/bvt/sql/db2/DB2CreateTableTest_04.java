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
package com.alibaba.druid.bvt.sql.db2;

import com.alibaba.druid.sql.DB2Test;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.db2.parser.DB2StatementParser;
import com.alibaba.druid.sql.dialect.db2.visitor.DB2SchemaStatVisitor;
import com.alibaba.druid.util.JdbcConstants;
import org.junit.Assert;

import java.util.List;

public class DB2CreateTableTest_04 extends DB2Test {

    public void test_0() throws Exception {
        String sql = "CREATE TABLE CUSTOMER\n" +
                "          (CUSTOMERNUM      INTEGER,\n" +
                "           CUSTOMERNAME     VARCHAR(80),\n" +
                "           ADDRESS          VARCHAR(200),\n" +
                "           CITY             VARCHAR(50),\n" +
                "           COUNTRY          VARCHAR(50),\n" +
                "           CODE             VARCHAR(15),\n" +
                "           CUSTOMERNUMDIM   INTEGER)\n" +
                "           COMPRESS YES;";

        DB2StatementParser parser = new DB2StatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();
        SQLStatement stmt = statementList.get(0);
        System.out.println(SQLUtils.toDB2String(stmt));

        Assert.assertEquals(1, statementList.size());

        DB2SchemaStatVisitor visitor = new DB2SchemaStatVisitor();
        stmt.accept(visitor);

//        System.out.println("Tables : " + visitor.getTables());
//        System.out.println("fields : " + visitor.getColumns());
//        System.out.println("coditions : " + visitor.getConditions());
//        System.out.println("orderBy : " + visitor.getOrderByColumns());

        Assert.assertEquals(1, visitor.getTables().size());
        Assert.assertEquals(7, visitor.getColumns().size());
        Assert.assertEquals(0, visitor.getConditions().size());

        Assert.assertTrue(visitor.containsTable("CUSTOMER"));

//         Assert.assertTrue(visitor.getColumns().contains(new Column("DSN8B10.EMP", "WORKDEPT")));
        // Assert.assertTrue(visitor.getColumns().contains(new Column("mytable", "first_name")));
        // Assert.assertTrue(visitor.getColumns().contains(new Column("mytable", "full_name")));

        Assert.assertEquals("CREATE TABLE CUSTOMER (\n" +
                        "\tCUSTOMERNUM INTEGER,\n" +
                        "\tCUSTOMERNAME VARCHAR(80),\n" +
                        "\tADDRESS VARCHAR(200),\n" +
                        "\tCITY VARCHAR(50),\n" +
                        "\tCOUNTRY VARCHAR(50),\n" +
                        "\tCODE VARCHAR(15),\n" +
                        "\tCUSTOMERNUMDIM INTEGER\n" +
                        ")\n" +
                        "COMPRESS YES;", //
                            SQLUtils.toSQLString(stmt, JdbcConstants.DB2));
        
        Assert.assertEquals("create table CUSTOMER (\n" +
                        "\tCUSTOMERNUM INTEGER,\n" +
                        "\tCUSTOMERNAME VARCHAR(80),\n" +
                        "\tADDRESS VARCHAR(200),\n" +
                        "\tCITY VARCHAR(50),\n" +
                        "\tCOUNTRY VARCHAR(50),\n" +
                        "\tCODE VARCHAR(15),\n" +
                        "\tCUSTOMERNUMDIM INTEGER\n" +
                        ")\n" +
                        "compress yes;", //
                            SQLUtils.toSQLString(stmt, JdbcConstants.DB2, SQLUtils.DEFAULT_LCASE_FORMAT_OPTION));
    }
}
