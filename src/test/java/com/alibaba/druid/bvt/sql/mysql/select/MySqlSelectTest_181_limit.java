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

public class MySqlSelectTest_181_limit extends MysqlTest {

    public void test_0() throws Exception {
        String sql = "(select 1) limit 1;";

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

        assertEquals(0, visitor.getTables().size());
        assertEquals(0, visitor.getColumns().size());
        assertEquals(0, visitor.getConditions().size());
        assertEquals(0, visitor.getOrderByColumns().size());

//        assertTrue(visitor.getTables().containsKey(new TableStat.Name("ub_userdiscuss")));

        String output = SQLUtils.toMySqlString(stmt);
        assertEquals("(SELECT 1\n" +
                        "LIMIT 1);", //
                            output);
    }

    public void test_1() throws Exception {
        String sql = "(select A.int_test,A.string_test from (select int_test,string_test from ss_dev.test_sub_rt where id < 1000) A order by A.int_test,A.string_test) limit 1000";

        MySqlStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();
        SQLStatement stmt = statementList.get(0);

        SQLSelectStatement selectStmt = (SQLSelectStatement) stmt;

        SQLSelect select = selectStmt.getSelect();
        assertNotNull(select.getQuery());
        MySqlSelectQueryBlock queryBlock = (MySqlSelectQueryBlock) select.getQuery();
        assertNotNull(queryBlock.getOrderBy());

//        print(statementList);

        assertEquals(1, statementList.size());

        MySqlSchemaStatVisitor visitor = new MySqlSchemaStatVisitor();
        stmt.accept(visitor);

        assertEquals(1, visitor.getTables().size());
        assertEquals(3, visitor.getColumns().size());
        assertEquals(1, visitor.getConditions().size());
        assertEquals(0, visitor.getOrderByColumns().size());

//        assertTrue(visitor.getTables().containsKey(new TableStat.Name("ub_userdiscuss")));

        String output = SQLUtils.toMySqlString(stmt);
        assertEquals("(SELECT A.int_test, A.string_test\n" +
                        "FROM (\n" +
                        "\tSELECT int_test, string_test\n" +
                        "\tFROM ss_dev.test_sub_rt\n" +
                        "\tWHERE id < 1000\n" +
                        ") A\n" +
                        "ORDER BY A.int_test, A.string_test)\n" +
                        "LIMIT 1000", //
                            output);
    }
}
