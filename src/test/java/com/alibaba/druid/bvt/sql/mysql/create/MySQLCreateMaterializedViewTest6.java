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

import com.alibaba.druid.sql.MysqlTest;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.visitor.VisitorFeature;

public class MySQLCreateMaterializedViewTest6 extends MysqlTest {

    public void test1() throws Exception {
        String sql = "CREATE MATERIALIZED VIEW mymv (\n" +
                "  PRIMARY KEY(id)\n" +
                ")\n" +
                "DISTRIBUTED BY HASH (id)\n" +
                "REFRESH COMPLETE ON DEMAND\n" +
                "ENABLE QUERY REWRITE\n" +
                "AS SELECT id FROM base;\n";

        ok(sql, "CREATE MATERIALIZED VIEW mymv (\n" +
                "\tPRIMARY KEY (id)\n" +
                ")\n" +
                "DISTRIBUTED BY HASH(id)\n" +
                "REFRESH COMPLETE ON DEMAND\n" +
                "ENABLE QUERY REWRITE\n" +
                "AS\n" +
                "SELECT id\n" +
                "FROM base;");

    }

    public void test2() throws Exception {
        String sql = "CREATE MATERIALIZED VIEW mymv (\n" +
                "  PRIMARY KEY(id)\n" +
                ")\n" +
                "DISTRIBUTED BY HASH (id)\n" +
                "REFRESH COMPLETE ON DEMAND\n" +
                "START WITH now()\n" +
                "NEXT DATE_ADD(now(), INTERVAL 1 MINUTE)\n" +
                "ENABLE QUERY REWRITE\n" +
                "AS SELECT id FROM base;";

        ok(sql, "CREATE MATERIALIZED VIEW mymv (\n" +
                "\tPRIMARY KEY (id)\n" +
                ")\n" +
                "DISTRIBUTED BY HASH(id)\n" +
                "REFRESH COMPLETE ON DEMAND\n" +
                "START WITH now() NEXT DATE_ADD(now(), INTERVAL 1 MINUTE)\n" +
                "ENABLE QUERY REWRITE\n" +
                "AS\n" +
                "SELECT id\n" +
                "FROM base;");

    }

    public void test3() throws Exception {
        String sql = "CREATE MATERIALIZED VIEW mymv\n" +
                "AS SELECT id FROM base;";

        ok(sql, "CREATE MATERIALIZED VIEW mymv\n" +
                "AS\n" +
                "SELECT id\n" +
                "FROM base;");
    }

    public void test4() throws Exception {
        String sql = "CREATE MATERIALIZED VIEW mymv (\n" +
                "  default_col varcahr,\n" +
                "  PRIMARY KEY(id)\n" +
                ")\n" +
                "DISTRIBUTED BY HASH (id)\n" +
                "REFRESH FAST ON DEMAND\n" +
                "DISABLE QUERY REWRITE\n" +
                "AS SELECT id FROM base;";

        ok(sql, "CREATE MATERIALIZED VIEW mymv (\n" +
                "\tdefault_col varcahr,\n" +
                "\tPRIMARY KEY (id)\n" +
                ")\n" +
                "DISTRIBUTED BY HASH(id)\n" +
                "REFRESH FAST ON DEMAND\n" +
                "DISABLE QUERY REWRITE\n" +
                "AS\n" +
                "SELECT id\n" +
                "FROM base;");
    }

    public void test5() throws Exception {
        String sql = "CREATE MATERIALIZED VIEW mymv\n" +
                "REFRESH COMPLETE\n" +
                "ENABLE QUERY REWRITE\n" +
                "AS SELECT id FROM base;";

        ok(sql, "CREATE MATERIALIZED VIEW mymv\n" +
                "REFRESH COMPLETE\n" +
                "ENABLE QUERY REWRITE\n" +
                "AS\n" +
                "SELECT id\n" +
                "FROM base;");
    }

    public void test6() throws Exception {
        String sql = "CREATE MATERIALIZED VIEW mymv\n" +
                "ENABLE QUERY REWRITE\n" +
                "AS SELECT id FROM base;";

        ok(sql, "CREATE MATERIALIZED VIEW mymv\n" +
                "ENABLE QUERY REWRITE\n" +
                "AS\n" +
                "SELECT id\n" +
                "FROM base;");
    }

    public void test7() throws Exception {
        String sql = "CREATE MATERIALIZED VIEW mymv (\n" +
                "  default_col varcahr,\n" +
                "  PRIMARY KEY(id)\n" +
                ")\n" +
                "DISTRIBUTED BY HASH (id)\n" +
                "ENABLE QUERY REWRITE AS SELECT * FROM base;\n";

        ok(sql, "CREATE MATERIALIZED VIEW mymv (\n" +
                "\tdefault_col varcahr,\n" +
                "\tPRIMARY KEY (id)\n" +
                ")\n" +
                "DISTRIBUTED BY HASH(id)\n" +
                "ENABLE QUERY REWRITE\n" +
                "AS\n" +
                "SELECT *\n" +
                "FROM base;");
    }

    public void test8() throws Exception {
        String sql = "CREATE MATERIALIZED VIEW mymv\n" +
                "REFRESH ON DEMAND\n" +
                "DISABLE QUERY REWRITE\n" +
                "AS SELECT id FROM base;";

        ok(sql, "CREATE MATERIALIZED VIEW mymv\n" +
                "REFRESH ON DEMAND\n" +
                "DISABLE QUERY REWRITE\n" +
                "AS\n" +
                "SELECT id\n" +
                "FROM base;");
    }

    public void test9() throws Exception {
        String sql = "CREATE MATERIALIZED VIEW myview " +
                " REFRESH NEXT now() + interval 1 day " +
                " AS SELECT now() as t";

        ok(sql, "CREATE MATERIALIZED VIEW myview\n" +
                "REFRESH NEXT now() + INTERVAL 1 DAY\n" +
                "AS\n" +
                "SELECT now() AS t");
    }

    public void test10() throws Exception {
        String sql = "ALTER MATERIALIZED VIEW myview " +
                " REFRESH NEXT now() + interval 1 day ";

        ok(sql, "ALTER MATERIALIZED VIEW myview\n" +
                "REFRESH NEXT now() + INTERVAL 1 DAY");
    }

    public void test11() throws Exception {
        String sql = "CREATE MATERIALIZED VIEW myview " +
                " REFRESH FAST ON COMMIT " +
                " DISABLE QUERY REWRITE " +
                " AS SELECT * FROM base";

        ok(sql, "CREATE MATERIALIZED VIEW myview\n" +
                "REFRESH FAST ON COMMIT\n" +
                "DISABLE QUERY REWRITE\n" +
                "AS\n" +
                "SELECT *\n" +
                "FROM base");
    }

    public void test12() throws Exception {
        String sql = "CREATE MATERIALIZED VIEW myview " +
                " REFRESH COMPLETE ON OVERWRITE " +
                " ENABLE QUERY REWRITE " +
                " AS SELECT * FROM base";

        ok(sql, "CREATE MATERIALIZED VIEW myview\n" +
                "REFRESH COMPLETE ON OVERWRITE\n" +
                "ENABLE QUERY REWRITE\n" +
                "AS\n" +
                "SELECT *\n" +
                "FROM base");
    }

    public void ok(String sql, String expectedSql) {
        SQLStatement stmt = SQLUtils.parseSingleMysqlStatement(sql);

        assertEquals(expectedSql, stmt.toString(VisitorFeature.OutputDistributedLiteralInCreateTableStmt));
    }
}
