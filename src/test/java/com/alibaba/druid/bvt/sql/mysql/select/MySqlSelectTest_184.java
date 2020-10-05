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
import com.alibaba.druid.sql.ast.statement.SQLSelect;
import com.alibaba.druid.sql.ast.statement.SQLSelectStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlSelectQueryBlock;
import com.alibaba.druid.sql.dialect.mysql.parser.MySqlStatementParser;
import com.alibaba.druid.sql.dialect.mysql.visitor.MySqlSchemaStatVisitor;
import com.alibaba.druid.stat.TableStat;

import java.util.List;

public class MySqlSelectTest_184 extends MysqlTest {

    public void test_0() throws Exception {
        String sql = "SELECT \"B\".\"col_new\" FROM \"wenyu_meta_test\".\"WENBO6_CONFIG_TEST\" AS \"a\", \"wenyu_meta_test\".\"WENYU_CONFIG_TEST\" AS \"b\" WHERE \"A\".\"cid\" = \"B\".\"cid\" AND \"A\".\"cname\" = \"B\".\"cname\" LIMIT 4";

        MySqlStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();
        SQLStatement stmt = statementList.get(0);

        SQLSelectStatement selectStmt = (SQLSelectStatement) stmt;

        SQLSelect select = selectStmt.getSelect();
        assertNotNull(select.getQuery());
        MySqlSelectQueryBlock queryBlock = (MySqlSelectQueryBlock) select.getQuery();
        assertNull(queryBlock.getOrderBy());

//        print(statementList);

        assertEquals(1, statementList.size());

        MySqlSchemaStatVisitor visitor = new MySqlSchemaStatVisitor();
        stmt.accept(visitor);

        System.out.println("Tables : " + visitor.getTables());
        System.out.println("fields : " + visitor.getColumns());
//        System.out.println("coditions : " + visitor.getConditions());
//        System.out.println("orderBy : " + visitor.getOrderByColumns());

        assertEquals(2, visitor.getTables().size());
        assertEquals(5, visitor.getColumns().size());
        assertEquals(4, visitor.getConditions().size());
        assertEquals(0, visitor.getOrderByColumns().size());

        assertTrue(visitor.containsTable("wenyu_meta_test.WENBO6_CONFIG_TEST"));
        assertTrue(visitor.containsTable("wenyu_meta_test.WENYU_CONFIG_TEST"));
        assertTrue(visitor.containsColumn("wenyu_meta_test.WENYU_CONFIG_TEST", "cid"));

        String output = SQLUtils.toMySqlString(stmt);
        assertEquals("SELECT \"B\".\"col_new\"\n" +
                        "FROM \"wenyu_meta_test\".\"WENBO6_CONFIG_TEST\" \"a\", wenyu_meta_test.\"WENYU_CONFIG_TEST\" \"b\"\n" +
                        "WHERE A.\"cid\" = B.\"cid\"\n" +
                        "\tAND A.\"cname\" = B.\"cname\"\n" +
                        "LIMIT 4", //
                            output);
    }

    public void test_1() throws Exception {
        String sql = "SELECT * FROM \"wenyu_meta_test\".\"WENYU_META_TEST_02\" LIMIT 4";

        MySqlStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();
        SQLStatement stmt = statementList.get(0);

        SQLSelectStatement selectStmt = (SQLSelectStatement) stmt;

        SQLSelect select = selectStmt.getSelect();
        assertNotNull(select.getQuery());
        MySqlSelectQueryBlock queryBlock = (MySqlSelectQueryBlock) select.getQuery();
        assertNull(queryBlock.getOrderBy());

//        print(statementList);

        assertEquals(1, statementList.size());

        MySqlSchemaStatVisitor visitor = new MySqlSchemaStatVisitor();
        stmt.accept(visitor);

        System.out.println("Tables : " + visitor.getTables());
        System.out.println("fields : " + visitor.getColumns());
//        System.out.println("coditions : " + visitor.getConditions());
//        System.out.println("orderBy : " + visitor.getOrderByColumns());

        assertEquals(1, visitor.getTables().size());
        assertEquals(1, visitor.getColumns().size());
        assertEquals(0, visitor.getConditions().size());
        assertEquals(0, visitor.getOrderByColumns().size());

        assertTrue(visitor.containsTable("wenyu_meta_test.WENYU_META_TEST_02"));
        assertTrue(visitor.containsColumn("wenyu_meta_test.WENYU_META_TEST_02", "*"));

        String output = SQLUtils.toMySqlString(stmt);
        assertEquals("SELECT *\n" +
                        "FROM \"wenyu_meta_test\".\"WENYU_META_TEST_02\"\n" +
                        "LIMIT 4", //
                output);

        assertEquals("wenyu_meta_test.WENYU_META_TEST_02", visitor.getTables().keySet().iterator().next().getName());
        assertEquals("wenyu_meta_test.WENYU_META_TEST_02", visitor.getColumns().iterator().next().getTable());
    }
}
