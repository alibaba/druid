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
package com.alibaba.druid.bvt.sql.oracle;

import java.util.List;

import org.junit.Assert;

import com.alibaba.druid.sql.OracleTest;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.oracle.parser.OracleStatementParser;
import com.alibaba.druid.sql.dialect.oracle.visitor.OracleSchemaStatVisitor;
import com.alibaba.druid.stat.TableStat;

public class OracleMultiInsertTest2 extends OracleTest {

    public void test_0() throws Exception {
        String sql = "INSERT ALL" + //
                     "   WHEN order_total < 100000 THEN" + //
                     "      INTO small_orders" + //
                     "   WHEN order_total > 100000 AND order_total < 200000 THEN" + //
                     "      INTO medium_orders" + //
                     "   ELSE" + //
                     "      INTO large_orders" + //
                     "   SELECT order_id, order_total, sales_rep_id, customer_id" + //
                     "      FROM orders;"; //

        OracleStatementParser parser = new OracleStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();
        SQLStatement statemen = statementList.get(0);
        print(statementList);

        Assert.assertEquals(1, statementList.size());

        OracleSchemaStatVisitor visitor = new OracleSchemaStatVisitor();
        statemen.accept(visitor);

        System.out.println("Tables : " + visitor.getTables());
        System.out.println("fields : " + visitor.getColumns());
        System.out.println("coditions : " + visitor.getConditions());
        System.out.println("relationships : " + visitor.getRelationships());

        Assert.assertTrue(visitor.getTables().containsKey(new TableStat.Name("orders")));
        Assert.assertTrue(visitor.getTables().containsKey(new TableStat.Name("small_orders")));
        Assert.assertTrue(visitor.getTables().containsKey(new TableStat.Name("medium_orders")));
        Assert.assertTrue(visitor.getTables().containsKey(new TableStat.Name("large_orders")));

        Assert.assertEquals(4, visitor.getTables().size());
        Assert.assertEquals(4, visitor.getColumns().size());

        Assert.assertTrue(visitor.getColumns().contains(new TableStat.Column("orders", "order_id")));
        Assert.assertTrue(visitor.getColumns().contains(new TableStat.Column("orders", "order_total")));
        Assert.assertTrue(visitor.getColumns().contains(new TableStat.Column("orders", "sales_rep_id")));
        Assert.assertTrue(visitor.getColumns().contains(new TableStat.Column("orders", "customer_id")));

    }

}
