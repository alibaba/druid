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
package com.alibaba.druid.bvt.sql.mysql.update;

import com.alibaba.druid.sql.MysqlTest;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.statement.SQLUpdateStatement;
import com.alibaba.druid.sql.dialect.mysql.parser.MySqlStatementParser;
import com.alibaba.druid.sql.dialect.mysql.visitor.MySqlSchemaStatVisitor;
import com.alibaba.druid.stat.TableStat.Column;
import com.alibaba.druid.wall.WallUtils;

import java.util.List;

public class MySqlUpdateTest_15 extends MysqlTest {

    public void test_0() throws Exception {
        String sql = "update students set name='test' where id in (select stu_id from score where s <100)";

        MySqlStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();
        SQLStatement stmt = statementList.get(0);
        print(statementList);

        assertEquals(1, statementList.size());

        MySqlSchemaStatVisitor visitor = new MySqlSchemaStatVisitor();
        stmt.accept(visitor);

//        System.out.println("Tables : " + visitor.getTables());
//        System.out.println("fields : " + visitor.getColumns());
//        System.out.println("coditions : " + visitor.getConditions());
//        System.out.println("orderBy : " + visitor.getOrderByColumns());

        assertEquals(2, visitor.getTables().size());
        assertEquals(4, visitor.getColumns().size());
        // assertEquals(2, visitor.getConditions().size());

        assertTrue(visitor.containsTable("students"));
        assertTrue(visitor.containsTable("score"));

        assertTrue(visitor.getColumns().contains(new Column("students", "name")));
        assertTrue(visitor.getColumns().contains(new Column("score", "stu_id")));

        {
            String output = SQLUtils.toMySqlString(stmt);
            assertEquals("UPDATE students\n" +
                            "SET name = 'test'\n" +
                            "WHERE id IN (\n" +
                            "\t\tSELECT stu_id\n" +
                            "\t\tFROM score\n" +
                            "\t\tWHERE s < 100\n" +
                            "\t)", //
                                output);
        }
        {
            String output = SQLUtils.toMySqlString(stmt, SQLUtils.DEFAULT_LCASE_FORMAT_OPTION);
            assertEquals("update students\n" +
                            "set name = 'test'\n" +
                            "where id in (\n" +
                            "\t\tselect stu_id\n" +
                            "\t\tfrom score\n" +
                            "\t\twhere s < 100\n" +
                            "\t)", //
                                output);
        }

        assertTrue(WallUtils.isValidateMySql(sql));

        {
            SQLUpdateStatement update = (SQLUpdateStatement) stmt;
            SQLExpr where = update.getWhere();
            assertEquals("id IN (\n" +
                    "\tSELECT stu_id\n" +
                    "\tFROM score\n" +
                    "\tWHERE s < 100\n" +
                    ")", where.toString());
        }

    }
}
