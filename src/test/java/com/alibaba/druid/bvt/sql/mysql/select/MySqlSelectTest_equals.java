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

import com.alibaba.druid.sql.MysqlTest;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.statement.SQLSelectQuery;
import com.alibaba.druid.sql.ast.statement.SQLSelectStatement;
import com.alibaba.druid.sql.dialect.mysql.parser.MySqlStatementParser;

import java.util.List;

public class MySqlSelectTest_equals extends MysqlTest {

    public void test_0() throws Exception {
        String sql = "select a from t where not a>1 and not b<1";

        String sql2 = "select a from t where not a>1 and not b<2";

        SQLSelectQuery queryBlock = getQueryBlock(sql);
        SQLSelectQuery queryBlock2 = getQueryBlock(sql2);

        assertNotSame(queryBlock, queryBlock2);

    }
    public void test_1() throws Exception {
        String sql = "select a from t where not a>1 and not b<1";

        String sql2 = "select a from t where not a>1 and not b<1";

        SQLSelectQuery queryBlock = getQueryBlock(sql);
        SQLSelectQuery queryBlock2 = getQueryBlock(sql2);

        assertEquals(queryBlock, queryBlock2);

    }

    private SQLSelectQuery getQueryBlock(String sql) {
        MySqlStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();
        SQLStatement stmt = statementList.get(0);

        SQLSelectStatement selectStmt = (SQLSelectStatement) stmt;
        return selectStmt.getSelect().getQuery();
    }
}
