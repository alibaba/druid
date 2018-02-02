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
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.mysql.parser.MySqlStatementParser;
import com.alibaba.druid.sql.parser.ParserException;

import java.util.List;

public class MySqlSelectTest_94_error extends MysqlTest {

    public void test_0() throws Exception {
        String sql = "select * from ttt where exist (select max(id) from ttt);";

        Exception error = null;
        try {
            MySqlStatementParser parser = new MySqlStatementParser(sql);
            List<SQLStatement> statementList = parser.parseStatementList();
        } catch (ParserException ex) {
            error = ex;
        }
        assertNotNull(error);
    }
}
