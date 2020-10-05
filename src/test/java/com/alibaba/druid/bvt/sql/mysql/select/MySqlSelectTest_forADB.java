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

import com.alibaba.druid.sql.MysqlTest;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.mysql.parser.MySqlStatementParser;
import com.alibaba.druid.sql.dialect.mysql.visitor.MySqlSchemaStatVisitor;

public class MySqlSelectTest_forADB extends MysqlTest {

    public void test_0() throws Exception {
        parseTrue("select distinct timediff(time \"11:25:00\" , time_test) as col5 from test order by 1 limit 9,6",
                "SELECT DISTINCT timediff(TIME '11:25:00', time_test) AS col5\n" +
                        "FROM test\n" +
                        "ORDER BY 1\n" +
                        "LIMIT 9, 6");

        parseTrue("SELECT\n" +
                        "  1 AS ANY,\n" +
                        "  1 AS AT,\n" +
                        "  1 AS AVG,\n" +
                        "  1 AS BEGIN,\n" +
                        "  1 AS BIT,\n" +
                        "  1 AS BOOLEAN,\n" +
                        "  1 AS CASCADED,\n" +
                        "  1 AS CLOSE,\n" +
                        "  1 AS COALESCE,\n" +
                        "  1 AS COMMIT,\n" +
                        "  1 AS CUBE,\n" +
                        "  1 AS DATETIME,\n" +
                        "  1 AS DEALLOCATE,\n" +
                        "  1 AS DYNAMIC,\n" +
                        "  1 AS END,\n" +
                        "  1 AS ESCAPE,\n" +
                        "  1 AS EVERY,\n" +
                        "  1 AS `FLUSH`,\n" +
                        "  1 AS FUNCTION,\n" +
                        "  1 AS GET_FORMAT,\n" +
                        "  1 AS GLOBAL,\n" +
                        "  1 AS HASH,\n" +
                        "  1 AS IDENTIFIED,\n" +
                        "  1 AS LANGUAGE,\n" +
                        "  1 AS LIST,\n" +
                        "  1 AS LOCAL,\n" +
                        "  1 AS MERGE,\n" +
                        "  1 AS MICROSECOND,\n" +
                        "  1 AS NATIONAL,\n" +
                        "  1 AS NCHAR,\n" +
                        "  1 AS NEW,\n" +
                        "  1 AS NEXT,\n" +
                        "  1 AS NO,\n" +
                        "  1 AS NONE,\n" +
                        "  1 AS OFFSET,\n" +
                        "  1 AS ONLY,\n" +
                        "  1 AS OPEN,\n" +
                        "  1 AS PREPARE,\n" +
                        "  1 AS QUARTER,\n" +
                        "  1 AS RESET,\n" +
                        "  1 AS RETURNS,\n" +
                        "  1 AS ROLLBACK,\n" +
                        "  1 AS ROLLUP,\n" +
                        "  1 AS ROWS,\n" +
                        "  1 AS SAVEPOINT,\n" +
                        "  1 AS SOME,\n" +
                        "  1 AS SUBPARTITION,\n" +
                        "  1 AS SUBPARTITIONS,\n" +
                        "  1 AS VALUE\n" +
                        "FROM test\n" +
                        "LIMIT 2",
                "SELECT 1 AS ANY, 1 AS AT, 1 AS AVG, 1 AS BEGIN, 1 AS BIT\n" +
                        "\t, 1 AS BOOLEAN, 1 AS CASCADED, 1 AS CLOSE, 1 AS COALESCE, 1 AS COMMIT\n" +
                        "\t, 1 AS CUBE, 1 AS DATETIME, 1 AS DEALLOCATE, 1 AS DYNAMIC, 1 AS END\n" +
                        "\t, 1 AS ESCAPE, 1 AS EVERY, 1 AS `FLUSH`, 1 AS FUNCTION, 1 AS GET_FORMAT\n" +
                        "\t, 1 AS GLOBAL, 1 AS HASH, 1 AS IDENTIFIED, 1 AS LANGUAGE, 1 AS LIST\n" +
                        "\t, 1 AS LOCAL, 1 AS MERGE, 1 AS MICROSECOND, 1 AS NATIONAL, 1 AS NCHAR\n" +
                        "\t, 1 AS NEW, 1 AS NEXT, 1 AS NO, 1 AS NONE, 1 AS OFFSET\n" +
                        "\t, 1 AS ONLY, 1 AS OPEN, 1 AS PREPARE, 1 AS QUARTER, 1 AS RESET\n" +
                        "\t, 1 AS RETURNS, 1 AS ROLLBACK, 1 AS ROLLUP, 1 AS ROWS, 1 AS SAVEPOINT\n" +
                        "\t, 1 AS SOME, 1 AS SUBPARTITION, 1 AS SUBPARTITIONS, 1 AS VALUE\n" +
                        "FROM test\n" +
                        "LIMIT 2");
    }

//    public void test_create1() {
//        String sql = "create table table_altertable_02(id bigint , val varchar , val2 double)distribute by hash(id);";
//
//        parseTrue(sql, "");
//    }


}
