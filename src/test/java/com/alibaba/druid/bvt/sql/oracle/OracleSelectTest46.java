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
import com.alibaba.druid.sql.test.TestUtils;

public class OracleSelectTest46 extends OracleTest {

    public void test_0() throws Exception {
        String sql = //
        "select *" //
                + " FROM MT_PRODUCT_ORDER T1,MT_ORDER T2 WHERE " //
                + " T1.MT_ORDER_ID = T2.MT_ORDER_ID" //
                + " and SELLER_SSOID = 1" //
                + " AND T1.MT_ORDER_ID = '1'" //
                + " AND T1.MT_BATCH_ORDER_ID IN '1'"; //

        OracleStatementParser parser = new OracleStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();
        SQLStatement stmt = statementList.get(0);
        print(statementList);

        Assert.assertEquals(1, statementList.size());

        OracleSchemaStatVisitor visitor = new OracleSchemaStatVisitor();
        stmt.accept(visitor);

        System.out.println("Tables : " + visitor.getTables());
        System.out.println("fields : " + visitor.getColumns());
        System.out.println("coditions : " + visitor.getConditions());
        System.out.println("relationships : " + visitor.getRelationships());
        System.out.println("orderBy : " + visitor.getOrderByColumns());

        Assert.assertEquals(2, visitor.getTables().size());

        Assert.assertEquals(4, visitor.getColumns().size());

        String text = TestUtils.outputOracle(stmt);

        Assert.assertEquals("SELECT *" //
                            + "\nFROM MT_PRODUCT_ORDER T1, MT_ORDER T2" //
                            + "\nWHERE T1.MT_ORDER_ID = T2.MT_ORDER_ID" //
                            + "\n\tAND SELLER_SSOID = 1" //
                            + "\n\tAND T1.MT_ORDER_ID = '1'" //
                            + "\n\tAND T1.MT_BATCH_ORDER_ID IN ('1');"//
                            + "\n", text);

        // Assert.assertTrue(visitor.getColumns().contains(new TableStat.Column("acduser.vw_acd_info", "xzqh")));

        // Assert.assertTrue(visitor.getOrderByColumns().contains(new TableStat.Column("employees", "last_name")));
    }
}
