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
package com.alibaba.druid.bvt.sql.mysql.create;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.OracleTest;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.visitor.SchemaStatVisitor;

import java.util.List;

public class MySqlCreateSequenceTest1 extends OracleTest {

    public void test_0() throws Exception {
        String sql = //
        "CREATE SEQUENCE customers_seq" + //
                " START WITH     1000" + //
                " INCREMENT BY   1" + //
                " NOCACHE" + //
                " NOCYCLE;";

        List<SQLStatement> statementList = SQLUtils.parseStatements(sql, DbType.mysql);
        SQLStatement stmt = statementList.get(0);
        print(statementList);

        assertEquals(1, statementList.size());

        SchemaStatVisitor visitor = SQLUtils.createSchemaStatVisitor(DbType.mysql);
        stmt.accept(visitor);

        System.out.println("Tables : " + visitor.getTables());
        System.out.println("fields : " + visitor.getColumns());
        System.out.println("coditions : " + visitor.getConditions());
        System.out.println("relationships : " + visitor.getRelationships());
        System.out.println("orderBy : " + visitor.getOrderByColumns());

        assertEquals(0, visitor.getTables().size());

        // assertTrue(visitor.getTables().containsKey(new TableStat.Name("cdc.en_complaint_ipr_stat_fdt0")));

        assertEquals(0, visitor.getColumns().size());

        assertEquals("CREATE SEQUENCE customers_seq START WITH 1000 INCREMENT BY 1 NOCYCLE NOCACHE;", stmt.toString());

        // assertTrue(visitor.getColumns().contains(new TableStat.Column("pivot_table", "*")));
        // assertTrue(visitor.getColumns().contains(new TableStat.Column("pivot_table", "YEAR")));
        // assertTrue(visitor.getColumns().contains(new TableStat.Column("pivot_table", "order_mode")));
    }

    public void test_1() throws Exception {
        String sql = //
                "CREATE SIMPLE SEQUENCE customers_seq" + //
                        " WITH CACHE;";

        List<SQLStatement> statementList = SQLUtils.parseStatements(sql, DbType.mysql);
        SQLStatement stmt = statementList.get(0);
        print(statementList);

        assertEquals(1, statementList.size());

        SchemaStatVisitor visitor = SQLUtils.createSchemaStatVisitor(DbType.mysql);
        stmt.accept(visitor);

        System.out.println("Tables : " + visitor.getTables());
        System.out.println("fields : " + visitor.getColumns());
        System.out.println("coditions : " + visitor.getConditions());
        System.out.println("relationships : " + visitor.getRelationships());
        System.out.println("orderBy : " + visitor.getOrderByColumns());

        assertEquals(0, visitor.getTables().size());

        // assertTrue(visitor.getTables().containsKey(new TableStat.Name("cdc.en_complaint_ipr_stat_fdt0")));

        assertEquals(0, visitor.getColumns().size());

        assertEquals("CREATE SIMPLE SEQUENCE customers_seq CACHE;", stmt.toString());

        // assertTrue(visitor.getColumns().contains(new TableStat.Column("pivot_table", "*")));
        // assertTrue(visitor.getColumns().contains(new TableStat.Column("pivot_table", "YEAR")));
        // assertTrue(visitor.getColumns().contains(new TableStat.Column("pivot_table", "order_mode")));
    }
}
