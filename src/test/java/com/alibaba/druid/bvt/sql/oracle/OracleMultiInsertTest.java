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

public class OracleMultiInsertTest extends OracleTest {

    public void test_0() throws Exception {
        String sql = "INSERT ALL" + //
                     "      INTO sales (prod_id, cust_id, time_id, amount)" + //
                     "      VALUES (product_id, customer_id, weekly_start_date, sales_sun)" + //
                     "      INTO sales (prod_id, cust_id, time_id, amount)" + //
                     "      VALUES (product_id, customer_id, weekly_start_date+1, sales_mon)" + //
                     "      INTO sales (prod_id, cust_id, time_id, amount)" + //
                     "      VALUES (product_id, customer_id, weekly_start_date+2, sales_tue)" + //
                     "      INTO sales (prod_id, cust_id, time_id, amount)" + //
                     "      VALUES (product_id, customer_id, weekly_start_date+3, sales_wed)" + //
                     "      INTO sales (prod_id, cust_id, time_id, amount)" + //
                     "      VALUES (product_id, customer_id, weekly_start_date+4, sales_thu)" + //
                     "      INTO sales (prod_id, cust_id, time_id, amount)" + //
                     "      VALUES (product_id, customer_id, weekly_start_date+5, sales_fri)" + //
                     "      INTO sales (prod_id, cust_id, time_id, amount)" + //
                     "      VALUES (product_id, customer_id, weekly_start_date+6, sales_sat)" + //
                     "   SELECT product_id, customer_id, weekly_start_date, sales_sun," + //
                     "      sales_mon, sales_tue, sales_wed, sales_thu, sales_fri, sales_sat" + //
                     "      FROM sales_input_table;"; //

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

        Assert.assertTrue(visitor.getTables().containsKey(new TableStat.Name("sales")));
        Assert.assertTrue(visitor.getTables().containsKey(new TableStat.Name("sales_input_table")));

        Assert.assertEquals(2, visitor.getTables().size());
        Assert.assertEquals(14, visitor.getColumns().size());

        Assert.assertTrue(visitor.getColumns().contains(new TableStat.Column("sales", "prod_id")));
        Assert.assertTrue(visitor.getColumns().contains(new TableStat.Column("sales", "cust_id")));
        Assert.assertTrue(visitor.getColumns().contains(new TableStat.Column("sales", "time_id")));
        Assert.assertTrue(visitor.getColumns().contains(new TableStat.Column("sales", "amount")));
        Assert.assertTrue(visitor.getColumns().contains(new TableStat.Column("sales_input_table", "product_id")));
        Assert.assertTrue(visitor.getColumns().contains(new TableStat.Column("sales_input_table", "customer_id")));
        Assert.assertTrue(visitor.getColumns().contains(new TableStat.Column("sales_input_table", "weekly_start_date")));
        Assert.assertTrue(visitor.getColumns().contains(new TableStat.Column("sales_input_table", "sales_sun")));
        Assert.assertTrue(visitor.getColumns().contains(new TableStat.Column("sales_input_table", "sales_mon")));
        Assert.assertTrue(visitor.getColumns().contains(new TableStat.Column("sales_input_table", "sales_tue")));
        Assert.assertTrue(visitor.getColumns().contains(new TableStat.Column("sales_input_table", "sales_wed")));
        Assert.assertTrue(visitor.getColumns().contains(new TableStat.Column("sales_input_table", "sales_thu")));
        Assert.assertTrue(visitor.getColumns().contains(new TableStat.Column("sales_input_table", "sales_fri")));
        Assert.assertTrue(visitor.getColumns().contains(new TableStat.Column("sales_input_table", "sales_sat")));

    }

}
