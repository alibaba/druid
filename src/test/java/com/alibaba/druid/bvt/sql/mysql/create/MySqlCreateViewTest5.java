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
package com.alibaba.druid.bvt.sql.mysql.create;

import com.alibaba.druid.sql.MysqlTest;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.statement.SQLCreateViewStatement;
import com.alibaba.druid.sql.dialect.mysql.parser.MySqlStatementParser;
import com.alibaba.druid.sql.dialect.mysql.visitor.MySqlSchemaStatVisitor;
import com.alibaba.druid.stat.TableStat;
import com.alibaba.druid.stat.TableStat.Column;
import org.junit.Assert;

import java.util.List;

public class MySqlCreateViewTest5 extends MysqlTest {

    public void test_0() throws Exception {
        String sql = "create or replace definer = current_user sql security invoker view my_view4(c1, 1c, _, c1_2) \n" +
                "\tas select * from  (t1 as tt1, t2 as tt2) inner join t1 on t1.col1 = tt1.col1;";

        MySqlStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();
        SQLCreateViewStatement stmt = (SQLCreateViewStatement) statementList.get(0);
//        print(statementList);

        assertEquals(1, statementList.size());

        MySqlSchemaStatVisitor visitor = new MySqlSchemaStatVisitor();
        stmt.accept(visitor);

        assertEquals("CREATE OR REPLACE DEFINER = current_user\n" +
                        "\tSQL SECURITY = invoker\n" +
                        "\tVIEW my_view4 (\n" +
                        "\tc1, \n" +
                        "\t1c, \n" +
                        "\t_, \n" +
                        "\tc1_2\n" +
                        ")\n" +
                        "AS\n" +
                        "SELECT *\n" +
                        "FROM (t1 tt1, t2 tt2)\n" +
                        "\tINNER JOIN t1 ON t1.col1 = tt1.col1;", //
                SQLUtils.toMySqlString(stmt));

        assertEquals("create or replace definer = current_user\n" +
                        "\tsql security = invoker\n" +
                        "\tview my_view4 (\n" +
                        "\tc1, \n" +
                        "\t1c, \n" +
                        "\t_, \n" +
                        "\tc1_2\n" +
                        ")\n" +
                        "as\n" +
                        "select *\n" +
                        "from (t1 tt1, t2 tt2)\n" +
                        "\tinner join t1 on t1.col1 = tt1.col1;", //
                SQLUtils.toMySqlString(stmt, SQLUtils.DEFAULT_LCASE_FORMAT_OPTION));

//        System.out.println("Tables : " + visitor.getTables());
        System.out.println("fields : " + visitor.getColumns());
//        System.out.println("coditions : " + visitor.getConditions());
//        System.out.println("orderBy : " + visitor.getOrderByColumns());

        assertEquals(2, visitor.getTables().size());
        assertEquals(3, visitor.getColumns().size());
        assertEquals(1, visitor.getConditions().size());

        assertTrue(visitor.getTables().containsKey(new TableStat.Name("t1")));

        assertTrue(visitor.getColumns().contains(new Column("t1", "col1")));
//        assertTrue(visitor.getColumns().contains(new Column("t2", "l_suppkey")));
    }
}
