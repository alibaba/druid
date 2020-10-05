/*
 * Copyright 1999-2011 Alibaba Group Holding Ltd.
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
package com.alibaba.druid.bvt.sql.mysql.select;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.MysqlTest;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLLimit;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.statement.SQLSelectQuery;
import com.alibaba.druid.sql.ast.statement.SQLSelectStatement;
import com.alibaba.druid.sql.dialect.mysql.parser.MySqlStatementParser;
import com.alibaba.druid.sql.parser.SQLParserFeature;

public class MySqlSelectTest_clearLimit extends MysqlTest {

    public void test_0() throws Exception {
        String sql = "select a from t limit 1,2";

        Object[] result = SQLUtils.clearLimit(sql, DbType.mysql);

        assertEquals("SELECT a\n"
                     + "FROM t", result[0]);
        assertEquals("LIMIT 1, 2", result[1].toString());
    }

    public void test_3() throws Exception {
        String sql = "SELECT id from test1"
                     + " where id BETWEEN 1 and 100"
                     + " intersect"
                     + " select sid from grade1"
                     + " where sid in(SELECT sid from grade1 where sid is null or sid between 10 and 100 ) limit 1,10";

        Object[] result = SQLUtils.clearLimit(sql, DbType.mysql);

        assertEquals("SELECT id\n" + "FROM test1\n" + "WHERE id BETWEEN 1 AND 100\n" + "INTERSECT\n" + "SELECT sid\n"
                     + "FROM grade1\n" + "WHERE sid IN (\n" + "\tSELECT sid\n" + "\tFROM grade1\n"
                     + "\tWHERE sid IS NULL\n" + "\t\tOR sid BETWEEN 10 AND 100\n" + ")\n" + "LIMIT 1, 10", result[0]);
        assertNull(result[1]);
    }
    public void test_4() throws Exception {
        String sql = "SELECT id from test1 where id BETWEEN 1 and 100 intersect  select sid from grade1 where sid in(SELECT sid from grade1 where sid  is null or sid between 10 and 100 )";

        Object[] result = SQLUtils.clearLimit(sql, DbType.mysql);

        assertEquals("SELECT id\n"
                     + "FROM test1\n"
                     + "WHERE id BETWEEN 1 AND 100\n"
                     + "INTERSECT\n"
                     + "SELECT sid\n"
                     + "FROM grade1\n"
                     + "WHERE sid IN (\n"
                     + "\tSELECT sid\n"
                     + "\tFROM grade1\n"
                     + "\tWHERE sid IS NULL\n"
                     + "\t\tOR sid BETWEEN 10 AND 100\n"
                     + ")", result[0]);
        assertNull(result[1]);
    }

    public void test_1() throws Exception {
        String sql = "select a from t limit 1,2";
        SQLLimit limit = SQLUtils.getLimit(sql, DbType.mysql);

        assertEquals("LIMIT 1, 2", limit.toString());
    }

    public void test_2() {
        String sql =
                " /*+ dump-oss-accesskey-id=xx,dump-oss-accesskey-secret=xx*/ \n"
                + "    DUMP DATA OVERWRITE INTO 'oss://oss-cn-hangzhou.aliyuncs.com/barca-platform/c'\n"
                + "  SELECT item_id,item_title FROM tyx.r_hjc_yun_item_info limit 1";
        Object[] objects = SQLUtils.clearLimit(sql, DbType.mysql);

        assertEquals("LIMIT 1", objects[1].toString());
    }

    public void test_clear_5() {
        String sql =
                " insert into t1 select * from t2 limit 10,10";
        Object[] objects = SQLUtils.clearLimit(sql, DbType.mysql);

        assertEquals("LIMIT 10, 10", objects[1].toString());


    }

    public void test_getLimit() {
        SQLLimit limit = SQLUtils.getLimit("insert into t1 select * from t2 limit 10,10", DbType.mysql);
        assertEquals("LIMIT 10, 10", limit.toString());

        limit = SQLUtils.getLimit("select a from t limit 1,2", DbType.mysql);
        assertEquals("LIMIT 1, 2", limit.toString());

        limit = SQLUtils.getLimit("DUMP DATA OVERWRITE INTO 'oss://oss-cn-hangzhou.aliyuncs.com/barca-platform/c' "
                                  + "   SELECT item_id,item_title FROM tyx.r_hjc_yun_item_info limit 1", DbType.mysql);

        assertEquals("LIMIT 1", limit.toString());


        limit = SQLUtils.getLimit("select * from 1", DbType.mysql);

        assertNull(limit);
    }

    public void test_getLimit2() {
        SQLLimit limit = SQLUtils.getLimit("(SELECT 13 ) INTERSECT (SELECT 13 limit 10)", DbType.mysql);

        assertNull(limit);
    }
}
