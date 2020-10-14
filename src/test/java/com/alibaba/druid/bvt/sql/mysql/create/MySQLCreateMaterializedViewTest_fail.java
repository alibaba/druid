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

import com.alibaba.druid.sql.OracleTest;
import com.alibaba.druid.sql.SQLUtils;

public class MySQLCreateMaterializedViewTest_fail extends OracleTest {

    public void test1() throws Exception {
        String sql = "CREATE MATERIALIZED VIEW mymv (\n" +
                "  PRIMARY KEY(id)\n" +
                ")\n" +
                "DISTRIBUTED BY HASH (id)\n" +
                "REFRESH COMPLETE ON DEMAND\n" +
                "ENABLE QUERY REWRITE\n" +
                "AS SELECT \n";

        failed(sql);

    }

    public void test2() throws Exception {
        String sql = "CREATE MATERIALIZED VIEW mymv (\n" +
                "  PRIMARY KEY(id)\n" +
                ")\n" +
                "DISTRIBUTED BY HASH (id)\n" +
                "REFRESH COMPLETE ON DEMAND\n" +
                "START WITH now()\n" +
                "NEXT DATE_ADD(now(), INTERVAL 1 MINUTE)\n" +
                "ENABLE \n" +
                "AS SELECT id FROM base;";

        failed(sql);

    }

    public void test3() throws Exception {
        String sql = "CREATE MATERIALIZED VIEW mymv\n" +
                "";

        failed(sql);
    }

    public void test4() throws Exception {
        String sql = "CREATE MATERIALIZED VIEW mymv (\n" +
                "  default_col varcahr,\n" +
                "  PRIMARY KEY(id)\n" +
                ")\n" +
                "DISTRIBUTED BY HASH (id)\n" +
                "REFRESH \n" +
                "DISABLE QUERY REWRITE\n" +
                "AS SELECT id FROM base;";

        failed(sql);
    }

    public void test5() throws Exception {
        String sql = "CREATE MATERIALIZED VIEW mymv\n" +
                "REFRESH \n" +
                "ENABLE QUERY REWRITE\n" +
                "AS SELECT id FROM base;";

        failed(sql);
    }

    public void test6() throws Exception {
        String sql = "CREATE MATERIALIZED VIEW mymv\n" +
                "ENABLE QUERY\n" +
                "AS SELECT id FROM base;";

        failed(sql);
    }

    public void test7() throws Exception {
        String sql = "CREATE MATERIALIZED VIEW mymv (\n" +
                "  default_col varcahr,\n" +
                "  PRIMARY KEY(id)\n" +
                ")\n" +
                "DISTRIBUTED BY HASH (id)\n" +
                " QUERY REWRITE AS SELECT * FROM base;\n";

        failed(sql);
    }

    public void test8() throws Exception {
        String sql = "CREATE MATERIALIZED VIEW mymv\n" +
                "REFRESH ON DEMAND\n" +
                "DISABLE \n" +
                "AS SELECT id FROM base;";

        failed(sql);
    }

    public void failed(String sql) {
        try {
            SQLUtils.parseSingleMysqlStatement(sql);
            fail();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

    }
}
