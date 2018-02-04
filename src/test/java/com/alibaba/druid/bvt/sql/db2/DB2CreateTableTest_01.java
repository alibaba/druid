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
import com.alibaba.druid.stat.TableStat;
import com.alibaba.druid.util.JdbcConstants;
import org.junit.Assert;

import java.util.List;

public class DB2CreateTableTest_01 extends DB2Test {

    public void test_0() throws Exception {
        String sql = "CREATE TABLE MK.KPI_AREA_SORT_FACT_LATN_ID_MID\n" +
                "(\n" +
                "LATN_ID INTEGER,\n" +
                "BUREAU_KEY INTEGER,\n" +
                "ADD_SUM BIGINT,\n" +
                "USER_ACCT BIGINT,\n" +
                "USER_ACCT_LY BIGINT,\n" +
                "TYPE_ID INTEGER\n" +
                ")\n" +
                "DATA CAPTURE NONE\n" +
                "IN WH_APP_TEMP\n" +
                "PARTITIONING KEY\n" +
                "(LATN_ID,BUREAU_KEY,ADD_SUM\n" +
                ") USING HASHING;";

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
        Assert.assertEquals(6, visitor.getColumns().size());
        Assert.assertEquals(0, visitor.getConditions().size());

        Assert.assertTrue(visitor.getTables().containsKey(new TableStat.Name("MK.KPI_AREA_SORT_FACT_LATN_ID_MID")));

//         Assert.assertTrue(visitor.getColumns().contains(new Column("DSN8B10.EMP", "WORKDEPT")));
        // Assert.assertTrue(visitor.getColumns().contains(new Column("mytable", "first_name")));
        // Assert.assertTrue(visitor.getColumns().contains(new Column("mytable", "full_name")));

        Assert.assertEquals("CREATE TABLE MK.KPI_AREA_SORT_FACT_LATN_ID_MID (\n" +
                        "\tLATN_ID INTEGER,\n" +
                        "\tBUREAU_KEY INTEGER,\n" +
                        "\tADD_SUM BIGINT,\n" +
                        "\tUSER_ACCT BIGINT,\n" +
                        "\tUSER_ACCT_LY BIGINT,\n" +
                        "\tTYPE_ID INTEGER\n" +
                        ")\n" +
                        "DATA CAPTURE NONE\n" +
                        "IN WH_APP_TEMP\n" +
                        "PARTITION BY HASH (LATN_ID, BUREAU_KEY, ADD_SUM);", //
                            SQLUtils.toSQLString(stmt, JdbcConstants.DB2));
        
        Assert.assertEquals("create table MK.KPI_AREA_SORT_FACT_LATN_ID_MID (\n" +
                        "\tLATN_ID INTEGER,\n" +
                        "\tBUREAU_KEY INTEGER,\n" +
                        "\tADD_SUM BIGINT,\n" +
                        "\tUSER_ACCT BIGINT,\n" +
                        "\tUSER_ACCT_LY BIGINT,\n" +
                        "\tTYPE_ID INTEGER\n" +
                        ")\n" +
                        "DATA CAPTURE NONE\n" +
                        "IN WH_APP_TEMP\n" +
                        "partition by hash (LATN_ID, BUREAU_KEY, ADD_SUM);", //
                            SQLUtils.toSQLString(stmt, JdbcConstants.DB2, SQLUtils.DEFAULT_LCASE_FORMAT_OPTION));
    }
}
