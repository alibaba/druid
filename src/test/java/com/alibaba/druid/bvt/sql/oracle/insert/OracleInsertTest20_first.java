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
package com.alibaba.druid.bvt.sql.oracle.insert;

import com.alibaba.druid.sql.OracleTest;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.oracle.parser.OracleStatementParser;
import com.alibaba.druid.sql.dialect.oracle.visitor.OracleSchemaStatVisitor;
import com.alibaba.druid.stat.TableStat;
import com.alibaba.druid.util.JdbcConstants;
import org.junit.Assert;

import java.util.List;

public class OracleInsertTest20_first extends OracleTest {

    public void test_0() throws Exception {
        String sql = "INSERT FIRST\n" +
                "   WHEN ottl < 100000 THEN\n" +
                "      INTO small_orders\n" +
                "         VALUES(oid, ottl, sid, cid)\n" +
                "   WHEN ottl > 100000 and ottl < 200000 THEN\n" +
                "      INTO medium_orders\n" +
                "         VALUES(oid, ottl, sid, cid)\n" +
                "   WHEN ottl > 290000 THEN\n" +
                "      INTO special_orders\n" +
                "   WHEN ottl > 200000 THEN\n" +
                "      INTO large_orders\n" +
                "         VALUES(oid, ottl, sid, cid)\n" +
                "   SELECT o.order_id oid, o.customer_id cid, o.order_total ottl,\n" +
                "      o.sales_rep_id sid, c.credit_limit cl, c.cust_email cem\n" +
                "      FROM orders o, customers c\n" +
                "      WHERE o.customer_id = c.customer_id;";

        OracleStatementParser parser = new OracleStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();
        SQLStatement stmt = statementList.get(0);
        print(statementList);

        assertEquals(1, statementList.size());

        assertEquals("INSERT FIRST \n" +
                        "\tWHEN ottl < 100000 THEN\n" +
                        "\t\tINTO small_orders\n" +
                        "\t\tVALUES (oid, ottl, sid, cid)\n" +
                        "\tWHEN ottl > 100000\n" +
                        "\tAND ottl < 200000 THEN\n" +
                        "\t\tINTO medium_orders\n" +
                        "\t\tVALUES (oid, ottl, sid, cid)\n" +
                        "\tWHEN ottl > 290000 THEN\n" +
                        "\t\tINTO special_orders\n" +
                        "\tWHEN ottl > 200000 THEN\n" +
                        "\t\tINTO large_orders\n" +
                        "\t\tVALUES (oid, ottl, sid, cid)\n" +
                        "SELECT o.order_id AS oid, o.customer_id AS cid, o.order_total AS ottl, o.sales_rep_id AS sid, c.credit_limit AS cl\n" +
                        "\t, c.cust_email AS cem\n" +
                        "FROM orders o, customers c\n" +
                        "WHERE o.customer_id = c.customer_id;",//
                            SQLUtils.toSQLString(stmt, JdbcConstants.ORACLE));

        OracleSchemaStatVisitor visitor = new OracleSchemaStatVisitor();
        stmt.accept(visitor);

        System.out.println("Tables : " + visitor.getTables());
        System.out.println("fields : " + visitor.getColumns());
//        System.out.println("coditions : " + visitor.getConditions());
//        System.out.println("relationships : " + visitor.getRelationships());

        assertEquals(6, visitor.getTables().size());
        assertEquals(7, visitor.getColumns().size());

        assertTrue(visitor.getTables().containsKey(new TableStat.Name("orders")));

         assertTrue(visitor.getColumns().contains(new TableStat.Column("orders", "customer_id")));
    }

}
