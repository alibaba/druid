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
package com.alibaba.druid.bvt.sql.mysql.update;

import com.alibaba.druid.sql.MysqlTest;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.mysql.parser.MySqlStatementParser;
import com.alibaba.druid.sql.dialect.mysql.visitor.MySqlSchemaStatVisitor;
import com.alibaba.druid.stat.TableStat.Column;
import org.junit.Assert;

import java.util.List;

public class MySqlUpdateTest_9 extends MysqlTest {

    public void test_0() throws Exception {
        String sql = "update tb1 a , tb2 b set a.name='abc' where a.id=b.id";

        MySqlStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();
        SQLStatement stmt = statementList.get(0);
        print(statementList);

        Assert.assertEquals(1, statementList.size());

        MySqlSchemaStatVisitor visitor = new MySqlSchemaStatVisitor();
        stmt.accept(visitor);

//        System.out.println("Tables : " + visitor.getTables());
//        System.out.println("fields : " + visitor.getColumns());
//        System.out.println("coditions : " + visitor.getConditions());
//        System.out.println("orderBy : " + visitor.getOrderByColumns());

        Assert.assertEquals(2, visitor.getTables().size());
        Assert.assertEquals(3, visitor.getColumns().size());
        // Assert.assertEquals(2, visitor.getConditions().size());

        Assert.assertTrue(visitor.containsTable("tb1"));
        Assert.assertTrue(visitor.containsTable("tb2"));

        Assert.assertTrue(visitor.getColumns().contains(new Column("tb1", "id")));
        Assert.assertTrue(visitor.getColumns().contains(new Column("tb1", "name")));
        Assert.assertTrue(visitor.getColumns().contains(new Column("tb2", "id")));

        {
            String output = SQLUtils.toMySqlString(stmt);
            Assert.assertEquals("UPDATE tb1 a, tb2 b\n" +
                            "SET a.name = 'abc'\n" +
                            "WHERE a.id = b.id", //
                                output);
        }
        {
            String output = SQLUtils.toMySqlString(stmt, SQLUtils.DEFAULT_LCASE_FORMAT_OPTION);
            Assert.assertEquals("update tb1 a, tb2 b\n" +
                            "set a.name = 'abc'\n" +
                            "where a.id = b.id", //
                                output);
        }
    }
}
