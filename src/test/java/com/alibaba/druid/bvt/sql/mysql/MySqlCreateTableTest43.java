/*
 * Copyright 1999-2101 Alibaba Group Holding Ltd.
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

import java.util.List;

import org.junit.Assert;

import com.alibaba.druid.sql.MysqlTest;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.mysql.parser.MySqlStatementParser;
import com.alibaba.druid.sql.dialect.mysql.visitor.MySqlSchemaStatVisitor;
import com.alibaba.druid.stat.TableStat;

public class MySqlCreateTableTest43 extends MysqlTest {

    public void test_0() throws Exception {
        String sql = "CREATE TABLE client_firms (" + //
                     "    id   INT," + //
                     "    name VARCHAR(35)" + //
                     ")" + //
                     "PARTITION BY LIST (id) (" + //
                     "    PARTITION r0 VALUES IN (1, 5, 9, 13, 17, 21)," + //
                     "    PARTITION r1 VALUES IN (2, 6, 10, 14, 18, 22)," + //
                     "    PARTITION r2 VALUES IN (3, 7, 11, 15, 19, 23)," + //
                     "    PARTITION r3 VALUES IN (4, 8, 12, 16, 20, 24)" + //
                     ");"; //

        MySqlStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();
        SQLStatement stmt = statementList.get(0);
        print(statementList);

        Assert.assertEquals(1, statementList.size());

        MySqlSchemaStatVisitor visitor = new MySqlSchemaStatVisitor();
        stmt.accept(visitor);

        System.out.println("Tables : " + visitor.getTables());
        System.out.println("fields : " + visitor.getColumns());
        System.out.println("coditions : " + visitor.getConditions());
        System.out.println("orderBy : " + visitor.getOrderByColumns());

        Assert.assertEquals(1, visitor.getTables().size());
        Assert.assertEquals(2, visitor.getColumns().size());
        Assert.assertEquals(0, visitor.getConditions().size());

        Assert.assertTrue(visitor.getTables().containsKey(new TableStat.Name("client_firms")));

        String output = SQLUtils.toMySqlString(stmt);
        Assert.assertEquals("CREATE TABLE client_firms (" + //
                            "\n\tid INT, " + //
                            "\n\tname VARCHAR(35)" + //
                            "\n) PARTITION BY LIST (id) (" + //
                            "\n\tPARTITION r0 VALUES IN (1, 5, 9, 13, 17, 21), " + //
                            "\n\tPARTITION r1 VALUES IN (2, 6, 10, 14, 18, 22), " + //
                            "\n\tPARTITION r2 VALUES IN (3, 7, 11, 15, 19, 23), " + //
                            "\n\tPARTITION r3 VALUES IN (4, 8, 12, 16, 20, 24)" + //
                            "\n)", output);

    }
}
