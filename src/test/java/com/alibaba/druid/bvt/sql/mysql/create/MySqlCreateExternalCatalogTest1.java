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
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.mysql.parser.MySqlStatementParser;

import java.util.List;

public class MySqlCreateExternalCatalogTest1 extends MysqlTest {

    public void test_0() throws Exception {
        String sql = "CREATE EXTERNAL CATALOG IF NOT EXISTS kafka_1 PROPERTIES ("
                    + "'connector.name'='kafka' " + "'kafka.table-names'='table1,table2' "
                    + "'kafka.nodes'='1.1.1.1:10000,1.1.1.2:10000') COMMENT 'this is a kafka connector test.'";

        MySqlStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> stmtList = parser.parseStatementList();

        assertEquals(1, stmtList.size());

        SQLStatement stmt = stmtList.get(0);

        assertEquals("CREATE EXTERNAL CATALOG IF NOT EXISTS kafka_1 PROPERTIES (\n"
                     + "'connector.name'='kafka'\n"
                     + "'kafka.nodes'='1.1.1.1:10000,1.1.1.2:10000'\n"
                     + "'kafka.table-names'='table1,table2')\n"
                     + "COMMENT 'this is a kafka connector test.'", stmt.toString());
    }
    public void test_1() throws Exception {
         String sql = "CREATE EXTERNAL CATALOG user_db.mysql_1 PROPERTIES ("
                      + "'connector.name'='mysql' "
                    + "'connection-url'='jdbc:mysql://1.1.1.1:3306' "
                      + "'connection-user'=\"x'!xx\" "
                    + "'connection-password'=\"x'xx\")";

        MySqlStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> stmtList = parser.parseStatementList();

        assertEquals(1, stmtList.size());

        SQLStatement stmt = stmtList.get(0);

        assertEquals("CREATE EXTERNAL CATALOG user_db.mysql_1 PROPERTIES (\n"
                     + "'connector.name'='mysql'\n"
                     + "'connection-url'='jdbc:mysql://1.1.1.1:3306'\n"
                     + "'connection-user'=\"x'!xx\"\n"
                     + "'connection-password'=\"x'xx\")", stmt.toString());
    }


}
