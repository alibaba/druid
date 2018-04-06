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
package com.alibaba.druid.bvt.sql.mysql.select;

import com.alibaba.druid.sql.MysqlTest;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.visitor.SchemaStatVisitor;
import com.alibaba.druid.util.JdbcConstants;
import org.junit.Assert;

import java.util.List;

public class MySqlSelectTest_42_with_cte extends MysqlTest {

    public void test_0() throws Exception {
        String sql = "WITH\n" +
                "  cte1 AS (SELECT a, b FROM table1),\n" +
                "  cte2 AS (SELECT c, d FROM table2)\n" +
                "SELECT b, d FROM cte1 JOIN cte2\n" +
                "WHERE cte1.a = cte2.c;";


        List<SQLStatement> statementList = SQLUtils.parseStatements(sql, JdbcConstants.MYSQL, true);
        SQLStatement stmt = statementList.get(0);

        Assert.assertEquals(1, statementList.size());

        SchemaStatVisitor visitor = SQLUtils.createSchemaStatVisitor(JdbcConstants.MYSQL);
        stmt.accept(visitor);

        System.out.println(stmt);

        System.out.println("Tables : " + visitor.getTables());
        System.out.println("fields : " + visitor.getColumns());
        System.out.println("coditions : " + visitor.getConditions());
        System.out.println("orderBy : " + visitor.getOrderByColumns());
        
        Assert.assertEquals(2, visitor.getTables().size());
        Assert.assertEquals(4, visitor.getColumns().size());
        Assert.assertEquals(2, visitor.getConditions().size());
        Assert.assertEquals(0, visitor.getOrderByColumns().size());

        assertTrue(visitor.containsTable("table1"));
        assertTrue(visitor.containsTable("table2"));
        assertTrue(visitor.containsColumn("table1", "a"));
        assertTrue(visitor.containsColumn("table1", "b"));
        assertTrue(visitor.containsColumn("table2", "c"));
        assertTrue(visitor.containsColumn("table2", "d"));

        {
            String output = SQLUtils.toMySqlString(stmt);
            Assert.assertEquals("WITH cte1 AS (\n" +
                            "\t\tSELECT a, b\n" +
                            "\t\tFROM table1\n" +
                            "\t), \n" +
                            "\tcte2 AS (\n" +
                            "\t\tSELECT c, d\n" +
                            "\t\tFROM table2\n" +
                            "\t)\n" +
                            "SELECT b, d\n" +
                            "FROM cte1\n" +
                            "\tJOIN cte2\n" +
                            "WHERE cte1.a = cte2.c;", //
                                output);
        }
        {
            String output = SQLUtils.toMySqlString(stmt, SQLUtils.DEFAULT_LCASE_FORMAT_OPTION);
            Assert.assertEquals("with cte1 as (\n" +
                            "\t\tselect a, b\n" +
                            "\t\tfrom table1\n" +
                            "\t), \n" +
                            "\tcte2 as (\n" +
                            "\t\tselect c, d\n" +
                            "\t\tfrom table2\n" +
                            "\t)\n" +
                            "select b, d\n" +
                            "from cte1\n" +
                            "\tjoin cte2\n" +
                            "where cte1.a = cte2.c;", //
                                output);
        }

        {
            String output = SQLUtils.toMySqlString(stmt, new SQLUtils.FormatOption(true, true, true));
            Assert.assertEquals("WITH cte1 AS (\n" +
                            "\t\tSELECT a, b\n" +
                            "\t\tFROM table1\n" +
                            "\t), \n" +
                            "\tcte2 AS (\n" +
                            "\t\tSELECT c, d\n" +
                            "\t\tFROM table2\n" +
                            "\t)\n" +
                            "SELECT b, d\n" +
                            "FROM cte1\n" +
                            "\tJOIN cte2\n" +
                            "WHERE cte1.a = cte2.c;", //
                    output);
        }
    }
}
