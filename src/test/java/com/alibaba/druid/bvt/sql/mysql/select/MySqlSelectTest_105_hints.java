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


public class MySqlSelectTest_105_hints extends MysqlTest {
    public void test_0() throws Exception {
        String sql = "\n" +
                "select * from t FORCE INDEX(name_1),abc FORCE INDEX(name_2) where id = 1";

        MySqlStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();

        assertEquals(1, statementList.size());

        SQLSelectStatement stmt = (SQLSelectStatement) statementList.get(0);

        assertEquals("SELECT *\n" +
                "FROM t FORCE INDEX (name_1), abc FORCE INDEX (name_2)\n" +
                "WHERE id = 1", stmt.toString());
    }

}