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
import com.alibaba.druid.sql.transform.SQLUnifiedUtils;
import com.alibaba.druid.sql.visitor.VisitorFeature;

public class MySQLCreateMaterializedViewTest7 extends MysqlTest {

    public void test1() throws Exception {
        String sql = "create materialized view `a` (\n" +
                "    `__adb_auto_id__` bigint AUTO_INCREMENT,\n" +
                "    `id` int comment 'id',\n" +
                "    `name` varchar(10),\n" +
                "    `value` double,\n" +
                "    index index_id(`name` ASC, `value` ASC),\n" +
                "    primary key (`__adb_auto_id__`)\n" +
                ") DISTRIBUTED BY HASH(`__adb_auto_id__`)\n" +
                "comment 'materialized view a'\n" +
                "as select * from base;";

        ok(sql, "CREATE MATERIALIZED VIEW `a` (\n" +
                "\t`__adb_auto_id__` bigint AUTO_INCREMENT,\n" +
                "\t`id` int COMMENT 'id',\n" +
                "\t`name` varchar(10),\n" +
                "\t`value` double,\n" +
                "\tINDEX index_id(`name` ASC, `value` ASC),\n" +
                "\tPRIMARY KEY (`__adb_auto_id__`)\n" +
                ")\n" +
                "DISTRIBUTED BY HASH(`__adb_auto_id__`)\n" +
                "COMMENT 'materialized view a'\n" +
                "AS\n" +
                "SELECT *\n" +
                "FROM base;");

    }

    public void test2() throws Exception {
        String sql = "create materialized view `b` (\n" +
                "    `id` int comment 'id',\n" +
                "    `name` varchar(10) primary key\n" +
                ") \n" +
                "DISTRIBUTED BY BROADCAST\n" +
                "INDEX_ALL = 'Y'\n" +
                "comment 'materialized view b'\n" +
                "as select * from base;";

        ok(sql, "CREATE MATERIALIZED VIEW `b` (\n" +
                "\t`id` int COMMENT 'id',\n" +
                "\t`name` varchar(10) PRIMARY KEY\n" +
                ")\n" +
                "DISTRIBUTED BY BROADCAST  INDEX_ALL = 'Y'\n" +
                "COMMENT 'materialized view b'\n" +
                "AS\n" +
                "SELECT *\n" +
                "FROM base;");

    }

    public void test3() throws Exception {
        String sql = "create materialized view c (\n" +
                "    key index_id(id) comment 'id',\n" +
                "    name varchar(10),\n" +
                "    value double,\n" +
                "    clustered key idex(name, value),\n" +
                "    primary key(id)\n" +
                ") \n" +
                "DISTRIBUTED by hash(id)\n" +
                "partition by value(date_format(dat, \"%Y%m%d\")) LIFECYCLE 30\n" +
                "comment 'materialized view c'\n" +
                "as select * from base;";

        ok(sql, "CREATE MATERIALIZED VIEW c (\n" +
                "\tKEY index_id (id) COMMENT 'id',\n" +
                "\tname varchar(10),\n" +
                "\tvalue double,\n" +
                "\tCLUSTERED KEY idex (name, value),\n" +
                "\tPRIMARY KEY (id)\n" +
                ")\n" +
                "DISTRIBUTED BY HASH(id)\n" +
                "COMMENT 'materialized view c'\n" +
                "PARTITION BY VALUE (date_format(dat, '%Y%m%d')) LIFECYCLE 30\n" +
                "AS\n" +
                "SELECT *\n" +
                "FROM base;");
    }

    public void test4() throws Exception {
        String sql = "create materialized view d (\n" +
                "    id bigint(20) not null comment 'id',\n" +
                "    name varchar(10)\n" +
                ") \n" +
                "ENGINE = 'CSTORE'\n" +
                "INDEX_ALL = 'Y'\n" +
                "DISTRIBUTED BY HASH(`ID`)\n" +
                "partition by value(name) LIFECYCLE 15\n" +
                "COMMENT 'materialized view d'\n" +
                "as select * from base;";

        ok(sql, "CREATE MATERIALIZED VIEW d (\n" +
                "\tid bigint(20) NOT NULL COMMENT 'id',\n" +
                "\tname varchar(10)\n" +
                ")\n" +
                "DISTRIBUTED BY HASH(`ID`) ENGINE = 'CSTORE' INDEX_ALL = 'Y'\n" +
                "COMMENT 'materialized view d'\n" +
                "PARTITION BY VALUE (name) LIFECYCLE 15\n" +
                "AS\n" +
                "SELECT *\n" +
                "FROM base;");
    }

    public void test5() throws Exception {
        String sql = "Create Materialized View `mview_0` (\n" +
                " `a` int,\n" +
                " `b` double,\n" +
                " `c` float,\n" +
                " `d` int,\n" +
                " `e` double,\n" +
                " `f` float,\n" +
                " primary key (`a`)\n" +
                ") DISTRIBUTED BY HASH(`a`) INDEX_ALL='Y'\n" +
                " REFRESH COMPLETE ON DEMAND\n" +
                " START WITH '2020-09-02 16:06:05'\n" +
                " NEXT adddatedatetime(now(), INTERVAL  '10' MINUTE)\n" +
                " DISABLE QUERY REWRITE\n" +
                "AS SELECT *\n" +
                "FROM\n" +
                "  `base0`\n" +
                ", `base1`\n" +
                "WHERE ((`a` = `d`) AND (`c` <> `F`))";

        ok(sql, "CREATE MATERIALIZED VIEW `mview_0` (\n" +
                "\t`a` int,\n" +
                "\t`b` double,\n" +
                "\t`c` float,\n" +
                "\t`d` int,\n" +
                "\t`e` double,\n" +
                "\t`f` float,\n" +
                "\tPRIMARY KEY (`a`)\n" +
                ")\n" +
                "DISTRIBUTED BY HASH(`a`) INDEX_ALL = 'Y'\n" +
                "REFRESH COMPLETE ON DEMAND\n" +
                "START WITH '2020-09-02 16:06:05' NEXT adddatedatetime(now(), INTERVAL '10' MINUTE)\n" +
                "DISABLE QUERY REWRITE\n" +
                "AS\n" +
                "SELECT *\n" +
                "FROM `base0`, `base1`\n" +
                "WHERE `a` = `d`\n" +
                "\tAND `c` <> `F`");
    }

    public void test6() throws Exception {
//        SQLUtils.parseSingleMysqlStatement("Create Materialized View `mview_0` (\n" +
//                " `a` int,\n" +
//                " `b` double,\n" +
//                " `c` float,\n" +
//                " `d` int,\n" +
//                " `e` double,\n" +
//                " `f` float,\n" +
//                " primary key (`a`)\n" +
//                ") DISTRIBUTED BY HASH(`a`) INDEX_ALL='Y'\n" +
//                " REFRESH COMPLETE ON DEMAND\n" +
//                " START WITH '2020-09-02 16:06:05'\n" +
//                " NEXT adddatedatetime(now(), INTERVAL  '10' MINUTE)\n" +
//                " DISABLE QUERY REWRITE\n" +
//                "AS SELECT *\n" +
//                "FROM\n" +
//                "  `base0`\n" +
//                ", `base1`\n" +
//                "WHERE ((`a` = `d`) AND (`c` <> `F`))");


        String sql = "Create Materialized View `mview_0` (\n" +
                " `a` int,\n" +
                " `b` double,\n" +
                " `c` float,\n" +
                " `d` int,\n" +
                " `e` double,\n" +
                " `f` float,\n" +
                " primary key (`a`)\n" +
                ") DISTRIBUTED BY HASH(`a`) INDEX_ALL='Y'\n" +
                " REFRESH COMPLETE ON DEMAND\n" +
                " START WITH '2020-09-02 16:06:05'\n" +
                " NEXT adddatedatetime(now(), INTERVAL  '10' MINUTE)\n" +
                " DISABLE QUERY REWRITE\n" +
                "AS SELECT *\n" +
                "FROM\n" +
                "  `base0`\n" +
                ", `base1`\n" +
                "WHERE ((`a` = `d`) AND (`c` <> `F`))";
        String unifySQL = SQLUnifiedUtils.unifySQL(sql, DbType.mysql);

        assertEquals("CREATE MATERIALIZED VIEW `mview_0` (\n" +
                "\t`a` int,\n" +
                "\t`b` double,\n" +
                "\t`c` float,\n" +
                "\t`d` int,\n" +
                "\t`e` double,\n" +
                "\t`f` float,\n" +
                "\tPRIMARY KEY (`a`)\n" +
                ")\n" +
                "DISTRIBUTE BY HASH(`a`) INDEX_ALL = ?\n" +
                "REFRESH COMPLETE ON DEMAND\n" +
                "START WITH ? NEXT adddatedatetime(now(), INTERVAL ? MINUTE)\n" +
                "DISABLE QUERY REWRITE\n" +
                "AS\n" +
                "SELECT *\n" +
                "FROM `base0`, `base1`\n" +
                "WHERE `a` = `d`\n" +
                "\tAND `c` <> `F`", unifySQL);
    }

    public void ok(String sql, String expectedSql) {
        SQLStatement stmt = SQLUtils.parseSingleMysqlStatement(sql);

        assertEquals(expectedSql, stmt.toString(VisitorFeature.OutputDistributedLiteralInCreateTableStmt));
    }
}
