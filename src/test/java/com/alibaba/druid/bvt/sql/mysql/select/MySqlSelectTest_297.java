/*
 * Copyright 1999-2017 Alibaba Group Holding Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.alibaba.druid.bvt.sql.mysql.select;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.MysqlTest;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;

public class MySqlSelectTest_297
        extends MysqlTest {

    public void test_0() throws Exception {
        String sql = "select sn,properties->'$.zoneId',properties->'$.regionId',ip,owner,gmt_create   \n" +
                "from resource_instance where type=16  and  (properties->'$.idkp'='1647796581073291')";

        SQLStatement stmt = SQLUtils
                .parseSingleStatement(sql, DbType.mysql);

        assertEquals("SELECT sn, properties -> '$.zoneId', properties -> '$.regionId'\n" +
                "\t, ip, owner, gmt_create\n" +
                "FROM resource_instance\n" +
                "WHERE type = 16\n" +
                "\tAND properties -> '$.idkp' = '1647796581073291'", stmt.toString());
    }


    public void test_1() throws Exception {
        String sql = "select `current_date`, 1 + `current_date`";

        SQLStatement stmt = SQLUtils
                .parseSingleStatement(sql, DbType.mysql);

        assertEquals("SELECT `current_date`, 1 + `current_date`", stmt.toString());
    }

    public void test_2() throws Exception {
        String sql = "select `current_timestamp`, 1 + `current_timestamp`";

        SQLStatement stmt = SQLUtils
                .parseSingleStatement(sql, DbType.mysql);

        assertEquals("SELECT `current_timestamp`, 1 + `current_timestamp`", stmt.toString());
    }

    public void test_3() throws Exception {
        String sql = "select `current_time`, 1 + `current_time`";

        SQLStatement stmt = SQLUtils
                .parseSingleStatement(sql, DbType.mysql);

        assertEquals("SELECT `current_time`, 1 + `current_time`", stmt.toString());
    }

    public void test_4() throws Exception {
        String sql = "select `curdate`, 1 + `curdate`";

        SQLStatement stmt = SQLUtils
                .parseSingleStatement(sql, DbType.mysql);

        assertEquals("SELECT `curdate`, 1 + `curdate`", stmt.toString());
    }

    public void test_5() throws Exception {
        String sql = "SELECT `current_date`, 1 + `current_date`";

        SQLStatement stmt = SQLUtils
                .parseSingleStatement(sql, DbType.mysql);

        assertEquals("SELECT `current_date`, 1 + `current_date`", stmt.toString());
    }

    public void test_6() throws Exception {
        String sql = "SELECT `time`, a, `date`, b, `timestamp` from t";

        SQLStatement stmt = SQLUtils
                .parseSingleStatement(sql, DbType.mysql);

        assertEquals("SELECT `time`, a, `date`, b, `timestamp`\n" +
                "FROM t", stmt.toString());
    }

    public void test_7() throws Exception {
        String sql = "SELECT `current_date`, a, `current_time`, b, `current_timestamp` from t";

        SQLStatement stmt = SQLUtils
                .parseSingleStatement(sql, DbType.mysql);

        assertEquals("SELECT `current_date`, a, `current_time`, b, `current_timestamp`\n" +
                "FROM t", stmt.toString());
    }

    public void test_8() throws Exception {
        String sql = "SELECT `current_user`, a, `localtime`, b, `localtimestamp` from t";

        SQLStatement stmt = SQLUtils
                .parseSingleStatement(sql, DbType.mysql);

        assertEquals("SELECT `current_user`, a, `localtime`, b, `localtimestamp`\n" +
                "FROM t", stmt.toString());
    }
}