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

public class MySqlCreateViewTest4 extends MysqlTest {

    public void test_0() throws Exception {
        String sql = "create or replace definer = 'ivan'@'%' view my_view3 as select count(*) from t3;";

        MySqlStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();
        SQLCreateViewStatement stmt = (SQLCreateViewStatement) statementList.get(0);
//        print(statementList);

        Assert.assertEquals(1, statementList.size());

        MySqlSchemaStatVisitor visitor = new MySqlSchemaStatVisitor();
        stmt.accept(visitor);

        assertEquals("CREATE OR REPLACE DEFINER = 'ivan'@'%'\n" +
                        "\tVIEW my_view3\n" +
                        "AS\n" +
                        "SELECT count(*)\n" +
                        "FROM t3;", //
                SQLUtils.toMySqlString(stmt));

        Assert.assertEquals("create or replace definer = 'ivan'@'%'\n" +
                        "\tview my_view3\n" +
                        "as\n" +
                        "select count(*)\n" +
                        "from t3;", //
                SQLUtils.toMySqlString(stmt, SQLUtils.DEFAULT_LCASE_FORMAT_OPTION));

//        System.out.println("Tables : " + visitor.getTables());
        System.out.println("fields : " + visitor.getColumns());
//        System.out.println("coditions : " + visitor.getConditions());
//        System.out.println("orderBy : " + visitor.getOrderByColumns());

        Assert.assertEquals(1, visitor.getTables().size());
        Assert.assertEquals(1, visitor.getColumns().size());
        Assert.assertEquals(0, visitor.getConditions().size());

        Assert.assertTrue(visitor.getTables().containsKey(new TableStat.Name("t3")));

        Assert.assertTrue(visitor.getColumns().contains(new Column("t3", "*")));
//        Assert.assertTrue(visitor.getColumns().contains(new Column("t2", "l_suppkey")));
    }
}
