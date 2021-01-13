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
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.visitor.SchemaStatVisitor;
import junit.framework.TestCase;

import java.util.List;

public class MySqlCreateSequenceTest3 extends TestCase {

    public void test_0() throws Exception {
        String sql = //
        "CREATE GROUP SEQUENCE seq1 START WITH 123 UNIT COUNT 1 INDEX 0";
        System.out.println(sql);
        List<SQLStatement> statementList = SQLUtils.parseStatements(sql, DbType.mysql);
        SQLStatement stmt = statementList.get(0);

        assertEquals(1, statementList.size());

        SchemaStatVisitor visitor = SQLUtils.createSchemaStatVisitor(DbType.mysql);
        stmt.accept(visitor);
        System.out.println(stmt.toString());
        stmt = SQLUtils.parseStatements(stmt.toString(), DbType.mysql).get(0);
        System.out.println("Tables : " + visitor.getTables());
        System.out.println("fields : " + visitor.getColumns());
        System.out.println("coditions : " + visitor.getConditions());
        System.out.println("relationships : " + visitor.getRelationships());
        System.out.println("orderBy : " + visitor.getOrderByColumns());

        assertEquals(0, visitor.getTables().size());

        // assertTrue(visitor.getTables().containsKey(new TableStat.Name("cdc.en_complaint_ipr_stat_fdt0")));

        assertEquals(0, visitor.getColumns().size());

        assertEquals("CREATE GROUP SEQUENCE seq1 START WITH 123 UNIT COUNT 1 INDEX 0", stmt.toString());
        assertEquals("create group sequence seq1 start with 123 unit count 1 index 0", stmt.toLowerCaseString());

        // assertTrue(visitor.getColumns().contains(new TableStat.Column("pivot_table", "*")));
        // assertTrue(visitor.getColumns().contains(new TableStat.Column("pivot_table", "YEAR")));
        // assertTrue(visitor.getColumns().contains(new TableStat.Column("pivot_table", "order_mode")));
    }

    public void test_3() throws Exception {
        String sql = //
                "CREATE SIMPLE WITH CACHE SEQUENCE seq1 START WITH 123";
        System.out.println(sql);
        List<SQLStatement> statementList = SQLUtils.parseStatements(sql, DbType.mysql);
        SQLStatement stmt = statementList.get(0);

        assertEquals(1, statementList.size());

        SchemaStatVisitor visitor = SQLUtils.createSchemaStatVisitor(DbType.mysql);
        stmt.accept(visitor);
        System.out.println(stmt.toString());
        stmt = SQLUtils.parseStatements(stmt.toString(), DbType.mysql).get(0);
        System.out.println("Tables : " + visitor.getTables());
        System.out.println("fields : " + visitor.getColumns());
        System.out.println("coditions : " + visitor.getConditions());
        System.out.println("relationships : " + visitor.getRelationships());
        System.out.println("orderBy : " + visitor.getOrderByColumns());

        assertEquals(0, visitor.getTables().size());

        // assertTrue(visitor.getTables().containsKey(new TableStat.Name("cdc.en_complaint_ipr_stat_fdt0")));

        assertEquals(0, visitor.getColumns().size());

        assertEquals("CREATE SIMPLE WITH CACHE SEQUENCE seq1 START WITH 123", stmt.toString());

        // assertTrue(visitor.getColumns().contains(new TableStat.Column("pivot_table", "*")));
        // assertTrue(visitor.getColumns().contains(new TableStat.Column("pivot_table", "YEAR")));
        // assertTrue(visitor.getColumns().contains(new TableStat.Column("pivot_table", "order_mode")));
    }

    public void test_1() throws Exception {
        String sql = //
                "RENAME SEQUENCE seq3 to seq4";

        List<SQLStatement> statementList = SQLUtils.parseStatements(sql, DbType.mysql);
        SQLStatement stmt = statementList.get(0);
        System.out.println(stmt.toString());

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

        assertEquals("RENAME SEQUENCE seq3 TO seq4", stmt.toString());
        assertEquals("rename sequence seq3 to seq4", stmt.toLowerCaseString());

        // assertTrue(visitor.getColumns().contains(new TableStat.Column("pivot_table", "*")));
        // assertTrue(visitor.getColumns().contains(new TableStat.Column("pivot_table", "YEAR")));
        // assertTrue(visitor.getColumns().contains(new TableStat.Column("pivot_table", "order_mode")));
    }

    public void test_2() throws Exception {
        String sql = //
                "CREATE GROUP SEQUENCE seq1 START WITH 123 unit count 10 index 1 step 10";

        List<SQLStatement> statementList = SQLUtils.parseStatements(sql, DbType.mysql);
        SQLStatement stmt = statementList.get(0);

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

        assertEquals("CREATE GROUP SEQUENCE seq1 START WITH 123 UNIT COUNT 10 INDEX 1 STEP 10", stmt.toString());
    }

    public void test_4() throws Exception {
        String sql = //
                "CREATE TIME SEQUENCE seq1";

        List<SQLStatement> statementList = SQLUtils.parseStatements(sql, DbType.mysql);
        SQLStatement stmt = statementList.get(0);

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

        assertEquals("CREATE TIME SEQUENCE seq1", stmt.toString());
        assertEquals("create time sequence seq1", stmt.toLowerCaseString());

        // assertTrue(visitor.getColumns().contains(new TableStat.Column("pivot_table", "*")));
        // assertTrue(visitor.getColumns().contains(new TableStat.Column("pivot_table", "YEAR")));
        // assertTrue(visitor.getColumns().contains(new TableStat.Column("pivot_table", "order_mode")));
    }

    public static void main(String[] args) {
        System.out.println(buildTableNameForXDriver("a.b"));
    }

    private static String buildTableNameForXDriver(String t) {
        try {
            if (t.contains("\\.")) {
                final String[] split = t.split("\\.");
                return new String("`" + split[0] + "`.`" + t + "`");
            } else {
                return new String("`" + t + "`");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
