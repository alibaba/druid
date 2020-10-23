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
import com.alibaba.druid.sql.MysqlTest;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.oracle.visitor.OracleSchemaStatVisitor;
import com.alibaba.druid.sql.visitor.VisitorFeature;
import com.alibaba.druid.stat.TableStat;

public class MySQLCreateMaterializedViewTest0 extends MysqlTest {

    public void test_types() throws Exception {
        String sql = //
        "CREATE MATERIALIZED VIEW mymv (\n" +
                "  default_col varchar,\n" +
                "  PRIMARY KEY(id)\n" +
                ")\n" +
                "DISTRIBUTED BY HASH (id)\n" +
                "REFRESH FAST ON DEMAND\n" +
                "ENABLE QUERY REWRITE\n" +
                "AS SELECT id FROM base;";

        SQLStatement stmt = SQLUtils.parseSingleMysqlStatement(sql);

        assertEquals("CREATE MATERIALIZED VIEW mymv (\n" +
                        "\tdefault_col varchar,\n" +
                        "\tPRIMARY KEY (id)\n" +
                        ")\n" +
                        "DISTRIBUTED BY HASH(id)\n" +
                        "REFRESH FAST ON DEMAND\n" +
                        "ENABLE QUERY REWRITE\n" +
                        "AS\n" +
                        "SELECT id\n" +
                        "FROM base;",//
                            SQLUtils.toSQLString(stmt, DbType.mysql, null, VisitorFeature.OutputDistributedLiteralInCreateTableStmt));

        OracleSchemaStatVisitor visitor = new OracleSchemaStatVisitor();
        stmt.accept(visitor);

        System.out.println("Tables : " + visitor.getTables());
        System.out.println("fields : " + visitor.getColumns());
        System.out.println("coditions : " + visitor.getConditions());
        System.out.println("relationships : " + visitor.getRelationships());
        System.out.println("orderBy : " + visitor.getOrderByColumns());

        assertEquals(1, visitor.getTables().size());

        assertEquals(1, visitor.getColumns().size());

        assertTrue(visitor.getColumns().contains(new TableStat.Column("base", "id")));
    }
}
