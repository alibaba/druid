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
package com.alibaba.druid.bvt.sql.hive;

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.statement.SQLSelectOrderByItem;
import com.alibaba.druid.sql.ast.statement.SQLSelectStatement;
import com.alibaba.druid.sql.visitor.SchemaStatVisitor;
import com.alibaba.druid.util.JdbcConstants;
import junit.framework.TestCase;

import java.util.List;

public class HiveSelectTest_13_cluster_by
        extends TestCase {

    public void test_0() throws Exception
    {
        String sql = "select buyer_id,seller_id ,order_id,div_pay_amt,t1.dim_seller.member_id,t1.dim_buyer.member_id from LD_aly.fct_pay_ord_cn_di t1 cluster by buyer_id,seller_id \n";

        List<SQLStatement> statementList = SQLUtils.parseStatements(sql, JdbcConstants.HIVE);
        SQLSelectStatement stmt = (SQLSelectStatement) statementList.get(0);

        assertEquals("SELECT buyer_id, seller_id, order_id, div_pay_amt, t1.dim_seller.member_id\n" +
                "\t, t1.dim_buyer.member_id\n" +
                "FROM LD_aly.fct_pay_ord_cn_di t1\n" +
                "CLUSTER BY buyer_id, seller_id", stmt.toString());

        assertEquals("SELECT buyer_id, seller_id, order_id, div_pay_amt, t1.dim_seller.member_id\n" +
                "\t, t1.dim_buyer.member_id\n" +
                "FROM LD_aly.fct_pay_ord_cn_di t1\n" +
                "CLUSTER BY buyer_id, seller_id", SQLUtils.toSQLString(stmt));

        assertEquals(1, statementList.size());

        for (SQLSelectOrderByItem item : stmt.getSelect().getQueryBlock().getClusterBy()) {
            assertFalse(item.isSortBy());
            assertFalse(item.isDistributeBy());
            assertTrue(item.isClusterBy());
        }

        SchemaStatVisitor visitor = SQLUtils.createSchemaStatVisitor(JdbcConstants.HIVE);
        stmt.accept(visitor);

//        System.out.println("Tables : " + visitor.getTables());
        System.out.println("fields : " + visitor.getColumns());
        System.out.println("coditions : " + visitor.getConditions());
        System.out.println("orderBy : " + visitor.getOrderByColumns());
        System.out.println("groupBy : " + visitor.getGroupByColumns());

        assertEquals(1, visitor.getTables().size());
        assertEquals(5, visitor.getColumns().size());
        assertEquals(0, visitor.getConditions().size());
        assertEquals(0, visitor.getGroupByColumns().size());

        assertTrue(visitor.containsColumn("LD_aly.fct_pay_ord_cn_di", "buyer_id"));
    }
}
