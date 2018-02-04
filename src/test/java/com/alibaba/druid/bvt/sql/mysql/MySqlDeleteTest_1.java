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

import java.util.List;

import org.junit.Assert;

import com.alibaba.druid.sql.MysqlTest;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.mysql.parser.MySqlStatementParser;
import com.alibaba.druid.sql.dialect.mysql.visitor.MySqlSchemaStatVisitor;
import com.alibaba.druid.stat.TableStat;
import com.alibaba.druid.stat.TableStat.Column;

public class MySqlDeleteTest_1 extends MysqlTest {

    public void test_0() throws Exception {
        String sql = "DELETE FROM a1, a2 USING t1 AS a1 INNER JOIN t2 AS a2 WHERE a1.id=a2.id;";

        MySqlStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();
        SQLStatement stmt = statementList.get(0);
        
        Assert.assertEquals("DELETE FROM a1, a2\n" +
                "USING t1 a1\n" +
                "\tINNER JOIN t2 a2\n" +
                "WHERE a1.id = a2.id;", SQLUtils.toMySqlString(stmt));
        Assert.assertEquals("delete from a1, a2\n" +
                "using t1 a1\n" +
                "\tinner join t2 a2\n" +
                "where a1.id = a2.id;", SQLUtils.toMySqlString(stmt, SQLUtils.DEFAULT_LCASE_FORMAT_OPTION));

        Assert.assertEquals(1, statementList.size());

        System.out.println(stmt.toString());

        MySqlSchemaStatVisitor visitor = new MySqlSchemaStatVisitor();
        stmt.accept(visitor);

        System.out.println("Tables : " + visitor.getTables());
        System.out.println("fields : " + visitor.getColumns());
//        System.out.println("coditions : " + visitor.getConditions());
//        System.out.println("orderBy : " + visitor.getOrderByColumns());
        
        Assert.assertEquals(2, visitor.getTables().size());
        Assert.assertEquals(2, visitor.getColumns().size());
        Assert.assertEquals(2, visitor.getConditions().size());

        Assert.assertTrue(visitor.getTables().containsKey(new TableStat.Name("t1")));
        Assert.assertTrue(visitor.getTables().containsKey(new TableStat.Name("t2")));
        
        Assert.assertTrue(visitor.getColumns().contains(new Column("t1", "id")));
        Assert.assertTrue(visitor.getColumns().contains(new Column("t2", "id")));
    }
}
