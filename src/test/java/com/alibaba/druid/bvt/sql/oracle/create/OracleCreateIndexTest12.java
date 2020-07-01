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

public class OracleCreateIndexTest12 extends OracleTest {

    public void test_0() throws Exception {
        String sql = //
        "CREATE BITMAP INDEX product_bm_ix \n" +
                "   ON hash_products(list_price)\n" +
                "   TABLESPACE tbs_1\n" +
                "   LOCAL(PARTITION ix_p1 TABLESPACE tbs_02,\n" +
                "         PARTITION ix_p2,\n" +
                "         PARTITION ix_p3 TABLESPACE tbs_03,\n" +
                "         PARTITION ix_p4,\n" +
                "         PARTITION ix_p5 TABLESPACE tbs_04 );\n";

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

        assertEquals("CREATE BITMAP INDEX product_bm_ix ON hash_products(list_price)\n" +
                        "TABLESPACE tbs_1\n" +
                        "LOCAL (\n" +
                        "\tPARTITION ix_p1\n" +
                        "\t\tTABLESPACE tbs_02,\n" +
                        "\tPARTITION ix_p2,\n" +
                        "\tPARTITION ix_p3\n" +
                        "\t\tTABLESPACE tbs_03,\n" +
                        "\tPARTITION ix_p4,\n" +
                        "\tPARTITION ix_p5\n" +
                        "\t\tTABLESPACE tbs_04\n" +
                        ");"
                , SQLUtils.toSQLString(stmt, JdbcConstants.ORACLE));

        assertEquals(1, visitor.getTables().size());

        assertTrue(visitor.getTables().containsKey(new TableStat.Name("hash_products")));

        assertEquals(1, visitor.getColumns().size());

//         assertTrue(visitor.getColumns().contains(new TableStat.Column("xwarehouses", "sales_rep_id")));
        // assertTrue(visitor.getColumns().contains(new TableStat.Column("pivot_table", "YEAR")));
        // assertTrue(visitor.getColumns().contains(new TableStat.Column("pivot_table", "order_mode")));
    }
}
