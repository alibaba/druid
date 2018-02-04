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
package com.alibaba.druid.bvt.sql.oceanbase;

import java.util.List;

import org.junit.Assert;

import com.alibaba.druid.sql.MysqlTest;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.mysql.parser.MySqlStatementParser;
import com.alibaba.druid.sql.dialect.mysql.visitor.MySqlSchemaStatVisitor;

public class OceanbaseCreateTableTest_rangePartition5 extends MysqlTest {

    public void test_0() throws Exception {
        String sql = "CREATE TABLE tnrange (id INT, name VARCHAR(5) )"
                + " PARTITION BY RANGE(id) ( "
                + "     PARTITION p1 VALUES LESS THAN (1), "
                + "     PARTITION p2 VALUES LESS THAN MAXVALUE"
                + ");"; //

        MySqlStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> stmtList = parser.parseStatementList();
        SQLStatement stmt = stmtList.get(0);

        {
            String result = SQLUtils.toMySqlString(stmt);
            Assert.assertEquals("CREATE TABLE tnrange ("
                    + "\n\tid INT,"
                    + "\n\tname VARCHAR(5)"
                    + "\n)"
                    + "\nPARTITION BY RANGE (id) ("
                    + "\n\tPARTITION p1 VALUES LESS THAN (1),"
                    + "\n\tPARTITION p2 VALUES LESS THAN MAXVALUE"
                    + "\n);",
                                result);
        }
        {
            String result = SQLUtils.toMySqlString(stmt, SQLUtils.DEFAULT_LCASE_FORMAT_OPTION);
            Assert.assertEquals("create table tnrange ("
                    + "\n\tid INT,"
                    + "\n\tname VARCHAR(5)"
                    + "\n)"
                    + "\npartition by range (id) ("
                    + "\n\tpartition p1 values less than (1),"
                    + "\n\tpartition p2 values less than maxvalue"
                    + "\n);",
                                result);
        }

        Assert.assertEquals(1, stmtList.size());

        MySqlSchemaStatVisitor visitor = new MySqlSchemaStatVisitor();
        stmt.accept(visitor);

        System.out.println("Tables : " + visitor.getTables());
        System.out.println("fields : " + visitor.getColumns());
        System.out.println("coditions : " + visitor.getConditions());
        System.out.println("orderBy : " + visitor.getOrderByColumns());

        Assert.assertEquals(1, visitor.getTables().size());
        Assert.assertEquals(2, visitor.getColumns().size());
        Assert.assertEquals(0, visitor.getConditions().size());

        // Assert.assertTrue(visitor.getTables().containsKey(new TableStat.Name("t_basic_store")));

    }
}
