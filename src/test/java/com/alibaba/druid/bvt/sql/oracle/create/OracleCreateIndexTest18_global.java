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
package com.alibaba.druid.bvt.sql.oracle.create;

import com.alibaba.druid.sql.OracleTest;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.visitor.SchemaStatVisitor;
import com.alibaba.druid.stat.TableStat;
import com.alibaba.druid.util.JdbcConstants;

import java.util.List;

public class OracleCreateIndexTest18_global extends OracleTest {

    public void test_0() throws Exception {
        String sql = //
        "CREATE INDEX cost_ix ON sales (amount_sold)\n" +
                "   GLOBAL PARTITION BY RANGE (amount_sold)\n" +
                "      (PARTITION p1 VALUES LESS THAN (1000),\n" +
                "       PARTITION p2 VALUES LESS THAN (2500),\n" +
                "       PARTITION p3 VALUES LESS THAN (MAXVALUE));";

        List<SQLStatement> statementList = SQLUtils.parseStatements(sql, JdbcConstants.ORACLE);
        SQLStatement stmt = statementList.get(0);
        print(statementList);

        assertEquals(1, statementList.size());

        SchemaStatVisitor visitor = SQLUtils.createSchemaStatVisitor(JdbcConstants.ORACLE);
        stmt.accept(visitor);

        System.out.println("Tables : " + visitor.getTables());
        System.out.println("fields : " + visitor.getColumns());
        System.out.println("coditions : " + visitor.getConditions());
        System.out.println("relationships : " + visitor.getRelationships());
        System.out.println("orderBy : " + visitor.getOrderByColumns());

        assertEquals("CREATE INDEX cost_ix ON sales(amount_sold)\n" +
                        "GLOBAL PARTITION BY RANGE (amount_sold) (\n" +
                        "\tPARTITION p1 VALUES LESS THAN (1000),\n" +
                        "\tPARTITION p2 VALUES LESS THAN (2500),\n" +
                        "\tPARTITION p3 VALUES LESS THAN (MAXVALUE)\n" +
                        ");"
                , SQLUtils.toSQLString(stmt, JdbcConstants.ORACLE));

        assertEquals(1, visitor.getTables().size());

        assertTrue(visitor.getTables().containsKey(new TableStat.Name("sales")));

        assertEquals(1, visitor.getColumns().size());

//         assertTrue(visitor.getColumns().contains(new TableStat.Column("xwarehouses", "sales_rep_id")));
        // assertTrue(visitor.getColumns().contains(new TableStat.Column("pivot_table", "YEAR")));
        // assertTrue(visitor.getColumns().contains(new TableStat.Column("pivot_table", "order_mode")));
    }
}
