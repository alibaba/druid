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
package com.alibaba.druid.bvt.sql.postgresql;

import com.alibaba.druid.sql.PGTest;
import com.alibaba.druid.sql.ast.statement.SQLSetStatement;

public class PGSetTest3 extends PGTest {
    public void testSet() throws Exception {
        Class<?> type = SQLSetStatement.class;

        String sql = "SET TIME ZONE 'Europe/Rome';";
        String expectedSql = "SET TIME ZONE 'Europe/Rome';";
        String expectedPattern = "SET TIME ZONE ?;";
        testParseSql(sql, expectedSql, expectedPattern, type);

        sql = "SET configuration_parameter TO DEFAULT;";
        expectedSql = "SET configuration_parameter TO DEFAULT;";
        testParseSql(sql, expectedSql, expectedSql, type);

        sql = "SET search_path TO my_schema, public;";
        expectedSql = "SET search_path TO my_schema, public;";
        testParseSql(sql, expectedSql, expectedSql, type);

        sql = "SET search_path =  my_schema, public;";
        expectedSql = "SET search_path TO my_schema, public;";
        testParseSql(sql, expectedSql, expectedSql, type);

        sql = "SET a=1";
        expectedSql = "SET a TO 1";
        testParseSql(sql, expectedSql, expectedSql, type);

        sql = "SET a=1,2";
        expectedSql = "SET a TO 1, 2";
        testParseSql(sql, expectedSql, expectedSql, type);
    }
}
