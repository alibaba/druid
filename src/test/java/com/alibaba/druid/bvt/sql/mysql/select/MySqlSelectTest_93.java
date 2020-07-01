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
package com.alibaba.druid.bvt.sql.mysql.select;

import com.alibaba.druid.sql.MysqlTest;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.statement.SQLSelectStatement;
import com.alibaba.druid.sql.dialect.mysql.parser.MySqlStatementParser;

import java.util.List;

public class MySqlSelectTest_93 extends MysqlTest {

    public void test_0() throws Exception {
        String sql = "select\n" +
                "         \n" +
                "         \n" +
                "\n" +
                "      id,whiteList_type, origin_address,gmt_create,gmt_modified, updateby, createby, (whitelist->'$.latitude') AS latitude,\n" +
                "      (whitelist->'$.longitude') AS longitude, whiteList_status, remark\n" +
                "\n" +
                "         \n" +
                "     \n" +
                "        from geo_whitelist\n" +
                "        where 1=1\n" +
                "         \n" +
                "         \n" +
                "         \n" +
                "         \n" +
                "         \n" +
                "            and whiteList_status = ?";

        MySqlStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();

        assertEquals(1, statementList.size());

        SQLSelectStatement stmt = (SQLSelectStatement) statementList.get(0);
        assertEquals("SELECT id, whiteList_type, origin_address, gmt_create, gmt_modified\n" +
                "\t, updateby, createby, whitelist -> '$.latitude' AS latitude\n" +
                "\t, whitelist -> '$.longitude' AS longitude, whiteList_status, remark\n" +
                "FROM geo_whitelist\n" +
                "WHERE 1 = 1\n" +
                "\tAND whiteList_status = ?", stmt.toString());
    }

    public void test_1() throws Exception {
        String sql = "SELECT * FROM user WHERE lastlogininfo ->'$.time' > '2015-10-02';";

        MySqlStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();

        assertEquals(1, statementList.size());

        SQLSelectStatement stmt = (SQLSelectStatement) statementList.get(0);
        assertEquals("SELECT *\n" +
                "FROM user\n" +
                "WHERE lastlogininfo -> '$.time' > '2015-10-02';", stmt.toString());
    }

}
