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
import com.alibaba.druid.sql.ast.SQLCommentHint;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.statement.SQLSelectStatement;
import com.alibaba.druid.sql.ast.statement.SQLUnionQuery;
import com.alibaba.druid.sql.dialect.mysql.parser.MySqlStatementParser;

import java.util.List;

public class MySqlSelectTest_99_union_order extends MysqlTest {

    public void test_0() throws Exception {
        String sql = "select id as id1 from ll union select id as id1 from rr order by id desc;";

        MySqlStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();

        assertEquals(1, statementList.size());

        SQLSelectStatement stmt = (SQLSelectStatement) statementList.get(0);
        assertEquals("SELECT id AS id1\n" +
                "FROM ll\n" +
                "UNION\n" +
                "SELECT id AS id1\n" +
                "FROM rr\n" +
                "ORDER BY id DESC;", stmt.toString());

        SQLUnionQuery union = (SQLUnionQuery) stmt.getSelect().getQuery();
        assertNotNull(union.getOrderBy());
//        SQLCommentHint hint = (SQLCommentHint) stmt.getSelect().getHints().get(0);
//        assertEquals("+ xxx ", hint.getText());
    }
}
