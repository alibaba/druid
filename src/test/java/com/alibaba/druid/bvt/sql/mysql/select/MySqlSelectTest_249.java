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
package com.alibaba.druid.bvt.sql.mysql.select;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.MysqlTest;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.statement.SQLSelectStatement;


public class MySqlSelectTest_249 extends MysqlTest {

    public void test_0() throws Exception {
        String sql = "select CAST(t.a AS ROW(v1 varchar, v2 varchar)) from ( select t.a from (     select cast(json_extract('{\"x\":[{\"a\":1,\"b\":2},{\"a\":3,\"b\":4}]}', '$.x') as array<JSON>) as package_array ) CROSS JOIN unnest(package_array) AS t(a) )";


        SQLSelectStatement stmt = (SQLSelectStatement) SQLUtils.parseSingleStatement(sql, DbType.mysql);

        assertEquals("SELECT CAST(t.a AS ROW(v1 varchar,v2 varchar))\n" +
                "FROM (\n" +
                "\tSELECT t.a\n" +
                "\tFROM (\n" +
                "\t\tSELECT CAST(json_extract('{\"x\":[{\"a\":1,\"b\":2},{\"a\":3,\"b\":4}]}', '$.x') AS ARRAY<JSON>) AS package_array\n" +
                "\t)\n" +
                "\t\tCROSS JOIN UNNEST(package_array) AS t (a)\n" +
                ")", stmt.toString());
    }



}