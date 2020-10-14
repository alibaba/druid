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
package com.alibaba.druid.bvt.sql.oracle.create;

import com.alibaba.druid.sql.OracleTest;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.oracle.parser.OracleStatementParser;
import com.alibaba.druid.sql.dialect.oracle.visitor.OracleSchemaStatVisitor;
import com.alibaba.druid.stat.TableStat;
import com.alibaba.druid.util.JdbcConstants;

import java.util.List;

public class OracleCreateMaterializedViewTest4 extends OracleTest {

    public void test_types() throws Exception {
        String sql = //
        "CREATE MATERIALIZED VIEW sales_mv\n" +
                "   BUILD IMMEDIATE\n" +
                "   REFRESH FAST ON COMMIT\n" +
                "   AS SELECT t.calendar_year, p.prod_id, \n" +
                "      SUM(s.amount_sold) AS sum_sales\n" +
                "      FROM times t, products p, sales s\n" +
                "      WHERE t.time_id = s.time_id AND p.prod_id = s.prod_id\n" +
                "      GROUP BY t.calendar_year, p.prod_id;\n"
               ;

        System.out.println(sql);

        OracleStatementParser parser = new OracleStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();
        SQLStatement stmt = statementList.get(0);
        print(statementList);

        assertEquals(1, statementList.size());

        assertEquals("CREATE MATERIALIZED VIEW sales_mv\n" +
                        "BUILD IMMEDIATE\n" +
                        "REFRESH FAST ON COMMIT\n" +
                        "AS\n" +
                        "SELECT t.calendar_year, p.prod_id, SUM(s.amount_sold) AS sum_sales\n" +
                        "FROM times t, products p, sales s\n" +
                        "WHERE t.time_id = s.time_id\n" +
                        "\tAND p.prod_id = s.prod_id\n" +
                        "GROUP BY t.calendar_year, p.prod_id;",//
                            SQLUtils.toSQLString(stmt, JdbcConstants.ORACLE));

        OracleSchemaStatVisitor visitor = new OracleSchemaStatVisitor();
        stmt.accept(visitor);

        System.out.println("Tables : " + visitor.getTables());
        System.out.println("fields : " + visitor.getColumns());
        System.out.println("coditions : " + visitor.getConditions());
        System.out.println("relationships : " + visitor.getRelationships());
        System.out.println("orderBy : " + visitor.getOrderByColumns());

        assertEquals(3, visitor.getTables().size());

        assertEquals(6, visitor.getColumns().size());

        assertTrue(visitor.containsColumn("times", "calendar_year"));
        assertTrue(visitor.containsColumn("products", "prod_id"));
        assertTrue(visitor.containsColumn("sales", "amount_sold"));
        assertTrue(visitor.containsColumn("times", "time_id"));
        assertTrue(visitor.containsColumn("sales", "time_id"));
        assertTrue(visitor.containsColumn("sales", "prod_id"));
    }
}
