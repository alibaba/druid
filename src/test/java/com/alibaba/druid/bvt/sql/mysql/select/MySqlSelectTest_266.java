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
import com.alibaba.druid.sql.ast.statement.SQLSelectStatement;
import com.alibaba.druid.sql.parser.SQLParserFeature;


public class MySqlSelectTest_266 extends MysqlTest {

    public void test_0() throws Exception {
        String sql = "EXPLAIN (FORMAT DETAIL)\n" +
                "SELECT\n" +
                "  `customer`.`name`\n" +
                ", `max`(`customer`.`custkey`) \"MAXKEY\"\n" +
                "FROM\n" +
                "  \"CUSTOMER\"\n" +
                ", \"ORDERS\"\n" +
                "WHERE (NOT (`CUSTOMER`.`custkey` IN (SELECT `CUSTKEY`\n" +
                "FROM\n" +
                "  \"ORDERS\"\n" +
                "WHERE (`CUSTKEY` > 100)\n" +
                ")))\n" +
                "GROUP BY `NAME`\n" +
                "ORDER BY `MAXKEY` ASC\n" +
                "LIMIT 1\n";

        SQLStatement stmt = SQLUtils
                .parseSingleStatement(sql, DbType.mysql
                        , SQLParserFeature.KeepSourceLocation
                        , SQLParserFeature.EnableSQLBinaryOpExprGroup
                );

        assertEquals("EXPLAIN (FORMAT DETAIL) SELECT `customer`.`name`, `max`(`customer`.`custkey`) AS \"MAXKEY\"\n" +
                "FROM \"CUSTOMER\", ORDERS\n" +
                "WHERE NOT `CUSTOMER`.`custkey` IN (\n" +
                "\tSELECT `CUSTKEY`\n" +
                "\tFROM \"ORDERS\"\n" +
                "\tWHERE `CUSTKEY` > 100\n" +
                ")\n" +
                "GROUP BY `NAME`\n" +
                "ORDER BY `MAXKEY` ASC\n" +
                "LIMIT 1", stmt.toString());
    }


}