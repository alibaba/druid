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

public class DB2CreateTableTest_05 extends DB2Test {

    public void test_0() throws Exception {
        String sql = "CREATE TABLE MK.M_DM_BASE_CHARGE_TMP1\n" +
                "(PROD_ID BIGINT,\n" +
                "ITEM_ID SMALLINT,\n" +
                "CHARGE BIGINT,\n" +
                "PAGES BIGINT,\n" +
                "DURATION BIGINT,\n" +
                "BILLING_DURATION BIGINT\n" +
                ") DATA CAPTURE NONE\n" +
                "IN MK_APP_TEMP\n" +
                "INDEX IN WH_INDEX1\n" +
                "PARTITIONING KEY\n" +
                "(PROD_ID\n" +
                ") USING HASHING";

        DB2StatementParser parser = new DB2StatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();
        SQLStatement stmt = statementList.get(0);
        System.out.println(SQLUtils.toDB2String(stmt));

        assertEquals(1, statementList.size());

        DB2SchemaStatVisitor visitor = new DB2SchemaStatVisitor();
        stmt.accept(visitor);

//        System.out.println("Tables : " + visitor.getTables());
//        System.out.println("fields : " + visitor.getColumns());
//        System.out.println("coditions : " + visitor.getConditions());
//        System.out.println("orderBy : " + visitor.getOrderByColumns());

        assertEquals(1, visitor.getTables().size());
        assertEquals(6, visitor.getColumns().size());
        assertEquals(0, visitor.getConditions().size());

        assertTrue(visitor.containsTable("MK.M_DM_BASE_CHARGE_TMP1"));

//         assertTrue(visitor.getColumns().contains(new Column("DSN8B10.EMP", "WORKDEPT")));
        // assertTrue(visitor.getColumns().contains(new Column("mytable", "first_name")));
        // assertTrue(visitor.getColumns().contains(new Column("mytable", "full_name")));

        assertEquals("CREATE TABLE MK.M_DM_BASE_CHARGE_TMP1 (\n" +
                        "\tPROD_ID BIGINT,\n" +
                        "\tITEM_ID SMALLINT,\n" +
                        "\tCHARGE BIGINT,\n" +
                        "\tPAGES BIGINT,\n" +
                        "\tDURATION BIGINT,\n" +
                        "\tBILLING_DURATION BIGINT\n" +
                        ")\n" +
                        "DATA CAPTURE NONE\n" +
                        "IN MK_APP_TEMP\n" +
                        "INDEX IN WH_INDEX1\n" +
                        "PARTITION BY HASH (PROD_ID)", //
                            SQLUtils.toSQLString(stmt, JdbcConstants.DB2));
        
        assertEquals("create table MK.M_DM_BASE_CHARGE_TMP1 (\n" +
                        "\tPROD_ID BIGINT,\n" +
                        "\tITEM_ID SMALLINT,\n" +
                        "\tCHARGE BIGINT,\n" +
                        "\tPAGES BIGINT,\n" +
                        "\tDURATION BIGINT,\n" +
                        "\tBILLING_DURATION BIGINT\n" +
                        ")\n" +
                        "DATA CAPTURE NONE\n" +
                        "IN MK_APP_TEMP\n" +
                        "INDEX IN WH_INDEX1\n" +
                        "partition by hash (PROD_ID)", //
                            SQLUtils.toSQLString(stmt, JdbcConstants.DB2, SQLUtils.DEFAULT_LCASE_FORMAT_OPTION));
    }
}
