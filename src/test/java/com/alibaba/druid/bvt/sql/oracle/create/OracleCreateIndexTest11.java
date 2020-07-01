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

public class OracleCreateIndexTest11 extends OracleTest {

    public void test_0() throws Exception {
        String sql = //
        "CREATE INDEX sales_ix ON composite_sales(time_id, prod_id)\n" +
                "   STORAGE (INITIAL 1M MAXEXTENTS UNLIMITED)\n" +
                "   LOCAL\n" +
                "   (PARTITION q1_1998,\n" +
                "    PARTITION q2_1998,\n" +
                "    PARTITION q3_1998,\n" +
                "    PARTITION q4_1998,\n" +
                "    PARTITION q1_1999,\n" +
                "    PARTITION q2_1999,\n" +
                "    PARTITION q3_1999,\n" +
                "    PARTITION q4_1999,\n" +
                "    PARTITION q1_2000,\n" +
                "    PARTITION q2_2000\n" +
                "      (SUBPARTITION pq2001, SUBPARTITION pq2002, \n" +
                "       SUBPARTITION pq2003, SUBPARTITION pq2004,\n" +
                "       SUBPARTITION pq2005, SUBPARTITION pq2006, \n" +
                "       SUBPARTITION pq2007, SUBPARTITION pq2008),\n" +
                "    PARTITION q3_2000\n" +
                "      (SUBPARTITION c1 TABLESPACE tbs_02, \n" +
                "       SUBPARTITION c2 TABLESPACE tbs_02, \n" +
                "       SUBPARTITION c3 TABLESPACE tbs_02,\n" +
                "       SUBPARTITION c4 TABLESPACE tbs_02,\n" +
                "       SUBPARTITION c5 TABLESPACE tbs_02),\n" +
                "    PARTITION q4_2000\n" +
                "      (SUBPARTITION pq4001 TABLESPACE tbs_03, \n" +
                "       SUBPARTITION pq4002 TABLESPACE tbs_03,\n" +
                "       SUBPARTITION pq4003 TABLESPACE tbs_03,\n" +
                "       SUBPARTITION pq4004 TABLESPACE tbs_03)\n" +
                ");";

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

        assertEquals("CREATE INDEX sales_ix ON composite_sales(time_id, prod_id)\n" +
                        "STORAGE (\n" +
                        "\tINITIAL 1M\n" +
                        "\tMAXEXTENTS UNLIMITED\n" +
                        ")\n" +
                        "LOCAL (\n" +
                        "\tPARTITION q1_1998,\n" +
                        "\tPARTITION q2_1998,\n" +
                        "\tPARTITION q3_1998,\n" +
                        "\tPARTITION q4_1998,\n" +
                        "\tPARTITION q1_1999,\n" +
                        "\tPARTITION q2_1999,\n" +
                        "\tPARTITION q3_1999,\n" +
                        "\tPARTITION q4_1999,\n" +
                        "\tPARTITION q1_2000,\n" +
                        "\tPARTITION q2_2000 (\n" +
                        "\t\tSUBPARTITION pq2001,\n" +
                        "\t\tSUBPARTITION pq2002,\n" +
                        "\t\tSUBPARTITION pq2003,\n" +
                        "\t\tSUBPARTITION pq2004,\n" +
                        "\t\tSUBPARTITION pq2005,\n" +
                        "\t\tSUBPARTITION pq2006,\n" +
                        "\t\tSUBPARTITION pq2007,\n" +
                        "\t\tSUBPARTITION pq2008\n" +
                        "\t),\n" +
                        "\tPARTITION q3_2000 (\n" +
                        "\t\tSUBPARTITION c1 TABLESPACE tbs_02,\n" +
                        "\t\tSUBPARTITION c2 TABLESPACE tbs_02,\n" +
                        "\t\tSUBPARTITION c3 TABLESPACE tbs_02,\n" +
                        "\t\tSUBPARTITION c4 TABLESPACE tbs_02,\n" +
                        "\t\tSUBPARTITION c5 TABLESPACE tbs_02\n" +
                        "\t),\n" +
                        "\tPARTITION q4_2000 (\n" +
                        "\t\tSUBPARTITION pq4001 TABLESPACE tbs_03,\n" +
                        "\t\tSUBPARTITION pq4002 TABLESPACE tbs_03,\n" +
                        "\t\tSUBPARTITION pq4003 TABLESPACE tbs_03,\n" +
                        "\t\tSUBPARTITION pq4004 TABLESPACE tbs_03\n" +
                        "\t)\n" +
                        ");"
                , SQLUtils.toSQLString(stmt, JdbcConstants.ORACLE));

        assertEquals(1, visitor.getTables().size());

        assertTrue(visitor.getTables().containsKey(new TableStat.Name("composite_sales")));

        assertEquals(2, visitor.getColumns().size());

//         assertTrue(visitor.getColumns().contains(new TableStat.Column("xwarehouses", "sales_rep_id")));
        // assertTrue(visitor.getColumns().contains(new TableStat.Column("pivot_table", "YEAR")));
        // assertTrue(visitor.getColumns().contains(new TableStat.Column("pivot_table", "order_mode")));
    }
}
