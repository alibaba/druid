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

public class OracleSelectTest28 extends OracleTest {

    public void test_0() throws Exception {
        String sql = //
        "SELECT /*+ ORDERED USE_NL ( \"A1 \") USE_NL ( \"A2 \") USE_NL ( \"A3 \") */  \"A3 \". \"AP_PAY_TIME \", \"A2 \". \"ORDER_ID \", \"A3 \". \"AP_PAY_AMT \", \"A1 \". \"COUNTRY \" FROM  \"ESCROW \". \"TRADE_PAY \"  \"A3 \", \"ESCROW \". \"ESCROW_TRADE \"  \"A2 \", \"ESCROW \". \"BUSINESS_ORDER \"  \"A1 \" WHERE  \"A3 \". \"TRADE_ID \"= \"A2 \". \"ID \" AND  \"A1 \". \"ID \"(+)=TO_NUMBER( \"A2 \". \"OUT_ORDER_ID \") AND  \"A2 \". \"ORDER_FROM \"='wholesale_order' AND  \"A3 \". \"AP_PAY_TIME \">=:1-.003819444444444444444444444444444444444444 AND  \"A3 \". \"AP_PAY_TIME \">=TRUNC(:2)";

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
        System.out.println("orderBy : " + visitor.getOrderByColumns());

        Assert.assertEquals(3, visitor.getTables().size());

        Assert.assertTrue(visitor.getTables().containsKey(new TableStat.Name("ESCROW.TRADE_PAY")));
        Assert.assertTrue(visitor.getTables().containsKey(new TableStat.Name("ESCROW.ESCROW_TRADE")));
        Assert.assertTrue(visitor.getTables().containsKey(new TableStat.Name("ESCROW.BUSINESS_ORDER")));

        Assert.assertEquals(9, visitor.getColumns().size());

        // Assert.assertTrue(visitor.getColumns().contains(new TableStat.Column("pivot_table", "*")));
        // Assert.assertTrue(visitor.getColumns().contains(new TableStat.Column("pivot_table", "YEAR")));
        // Assert.assertTrue(visitor.getColumns().contains(new TableStat.Column("pivot_table", "order_mode")));
    }
}
