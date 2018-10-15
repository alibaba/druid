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
package com.alibaba.druid.bvt.sql.mysql;

import com.alibaba.druid.sql.MysqlTest;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.mysql.parser.MySqlStatementParser;
import com.alibaba.druid.sql.dialect.mysql.visitor.MySqlSchemaStatVisitor;
import com.alibaba.druid.stat.TableStat;
import com.alibaba.druid.stat.TableStat.Column;
import org.junit.Assert;

import java.util.List;

public class MySqlDeleteTest_6 extends MysqlTest {

    public void test_0() throws Exception {
        String sql = "delete a.* from teacher_1 a join teacher_2 b on a.teacher_id = b.teacher_id where a.day < b.day";

        MySqlStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();
        SQLStatement stmt = statementList.get(0);
        assertEquals("DELETE a.*\n" +
                "FROM teacher_1 a\n" +
                "\tJOIN teacher_2 b ON a.teacher_id = b.teacher_id\n" +
                "WHERE a.day < b.day", SQLUtils.toMySqlString(stmt));
        assertEquals("delete a.*\n" +
                "from teacher_1 a\n" +
                "\tjoin teacher_2 b on a.teacher_id = b.teacher_id\n" +
                "where a.day < b.day", SQLUtils.toMySqlString(stmt, SQLUtils.DEFAULT_LCASE_FORMAT_OPTION));

        assertEquals(1, statementList.size());

        MySqlSchemaStatVisitor visitor = new MySqlSchemaStatVisitor();
        stmt.accept(visitor);

        System.out.println(stmt);

        System.out.println("Tables : " + visitor.getTables());
        System.out.println("fields : " + visitor.getColumns());
//        System.out.println("coditions : " + visitor.getConditions());
//        System.out.println("orderBy : " + visitor.getOrderByColumns());

        assertEquals(2, visitor.getTables().size());
        assertEquals(4, visitor.getColumns().size());
        assertEquals(4, visitor.getConditions().size());

        assertTrue(visitor.getTables().containsKey(new TableStat.Name("teacher_1")));
        assertTrue(visitor.getTables().containsKey(new TableStat.Name("teacher_2")));

        assertTrue(visitor.getColumns().contains(new Column("teacher_1", "teacher_id")));
        assertTrue(visitor.getColumns().contains(new Column("teacher_1", "teacher_id")));
        assertTrue(visitor.getColumns().contains(new Column("teacher_2", "day")));
        assertTrue(visitor.getColumns().contains(new Column("teacher_2", "day")));
    }
}
