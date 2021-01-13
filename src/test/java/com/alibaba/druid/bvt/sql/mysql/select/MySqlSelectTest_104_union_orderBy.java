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
import com.alibaba.druid.sql.ast.statement.SQLSelectStatement;
import com.alibaba.druid.sql.ast.statement.SQLUnionQuery;
import com.alibaba.druid.sql.dialect.mysql.parser.MySqlStatementParser;

import java.util.List;


public class MySqlSelectTest_104_union_orderBy extends MysqlTest {
    public void test_0() throws Exception {
        String sql = "\n" +
                "select a from a1 union select a from a2 union select a from a3 union select a from a4 union select a from a5 order by a desc";

        MySqlStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();

        assertEquals(1, statementList.size());

        SQLSelectStatement stmt = (SQLSelectStatement) statementList.get(0);
        System.out.println(stmt);
        SQLUnionQuery union = (SQLUnionQuery) stmt.getSelect().getQuery();
        assertNotNull(union.getOrderBy());
        assertTrue(union.getLeft() instanceof SQLUnionQuery);
        assertFalse(union.getRight() instanceof SQLUnionQuery);
        assertTrue(((SQLUnionQuery) union.getLeft()).getLeft() instanceof SQLUnionQuery);
        assertFalse(((SQLUnionQuery) union.getLeft()).getRight() instanceof SQLUnionQuery);

        assertEquals("SELECT a\n" +
                "FROM a1\n" +
                "UNION\n" +
                "SELECT a\n" +
                "FROM a2\n" +
                "UNION\n" +
                "SELECT a\n" +
                "FROM a3\n" +
                "UNION\n" +
                "SELECT a\n" +
                "FROM a4\n" +
                "UNION\n" +
                "SELECT a\n" +
                "FROM a5\n" +
                "ORDER BY a DESC", stmt.toString());
    }

}