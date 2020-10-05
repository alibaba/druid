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

public class MySqlCreateViewTest2 extends MysqlTest {

    public void test_0() throws Exception {
        String sql = "create view revenue0 as \n" +
                "select l_suppkey as supplier_no, sum(l_extendedprice * (1 - l_discount)) as total_revenue \n" +
                "from lineitem \n" +
                "where l_shipdate >= date '1993-01-01' and l_shipdate < date '1993-01-01' + interval '3' month \n" +
                "group by l_suppkey";

        MySqlStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();
        SQLCreateViewStatement stmt = (SQLCreateViewStatement) statementList.get(0);
//        print(statementList);

        Assert.assertEquals(1, statementList.size());

        MySqlSchemaStatVisitor visitor = new MySqlSchemaStatVisitor();
        stmt.accept(visitor);

        assertEquals("CREATE VIEW revenue0\n" +
                        "AS\n" +
                        "SELECT l_suppkey AS supplier_no, sum(l_extendedprice * (1 - l_discount)) AS total_revenue\n" +
                        "FROM lineitem\n" +
                        "WHERE l_shipdate >= DATE '1993-01-01'\n" +
                        "\tAND l_shipdate < DATE '1993-01-01' + INTERVAL '3' MONTH\n" +
                        "GROUP BY l_suppkey", //
                SQLUtils.toMySqlString(stmt));

        Assert.assertEquals("create view revenue0\n" +
                        "as\n" +
                        "select l_suppkey as supplier_no, sum(l_extendedprice * (1 - l_discount)) as total_revenue\n" +
                        "from lineitem\n" +
                        "where l_shipdate >= date '1993-01-01'\n" +
                        "\tand l_shipdate < date '1993-01-01' + interval '3' month\n" +
                        "group by l_suppkey", //
                SQLUtils.toMySqlString(stmt, SQLUtils.DEFAULT_LCASE_FORMAT_OPTION));

//        System.out.println("Tables : " + visitor.getTables());
//        System.out.println("fields : " + visitor.getColumns());
//        System.out.println("coditions : " + visitor.getConditions());
//        System.out.println("orderBy : " + visitor.getOrderByColumns());

        Assert.assertEquals(1, visitor.getTables().size());
        Assert.assertEquals(4, visitor.getColumns().size());
        Assert.assertEquals(2, visitor.getConditions().size());

        Assert.assertTrue(visitor.getTables().containsKey(new TableStat.Name("lineitem")));

        Assert.assertTrue(visitor.getColumns().contains(new Column("lineitem", "l_shipdate")));
        Assert.assertTrue(visitor.getColumns().contains(new Column("lineitem", "l_suppkey")));
    }
}
