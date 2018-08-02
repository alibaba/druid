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
package com.alibaba.druid.bvt.sql.oracle.create;

import java.util.List;

import org.junit.Assert;

import com.alibaba.druid.sql.OracleTest;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.oracle.parser.OracleStatementParser;
import com.alibaba.druid.sql.dialect.oracle.visitor.OracleSchemaStatVisitor;
import com.alibaba.druid.stat.TableStat;
import com.alibaba.druid.util.JdbcConstants;

public class OracleCreateTableTest30 extends OracleTest {

    public void test_types() throws Exception {
        String sql = //
        "CREATE TABLE order_detail " //
                + "  (CONSTRAINT pk_od PRIMARY KEY (order_id, part_no), " //
                + "   order_id    NUMBER " //
                + "      CONSTRAINT fk_oid " //
                + "         REFERENCES oe.orders(order_id), " //
                + "   part_no     NUMBER " //
                + "      CONSTRAINT fk_pno " //
                + "         REFERENCES oe.product_information(product_id), " //
                + "   quantity    NUMBER " //
                + "      CONSTRAINT nn_qty NOT NULL " //
                + "      CONSTRAINT check_qty CHECK (quantity > 0), " //
                + "   cost        NUMBER " //
                + "      CONSTRAINT check_cost CHECK (cost > 0) ); ";

        OracleStatementParser parser = new OracleStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();
        SQLStatement stmt = statementList.get(0);
        print(statementList);

        Assert.assertEquals(1, statementList.size());

        Assert.assertEquals("CREATE TABLE order_detail (" //
                            + "\n\tCONSTRAINT pk_od PRIMARY KEY (order_id, part_no)," //
                            + "\n\torder_id NUMBER" //
                            + "\n\t\tCONSTRAINT fk_oid REFERENCES oe.orders (order_id)," //
                            + "\n\tpart_no NUMBER" //
                            + "\n\t\tCONSTRAINT fk_pno REFERENCES oe.product_information (product_id)," //
                            + "\n\tquantity NUMBER" //
                            + "\n\t\tCONSTRAINT nn_qty NOT NULL" //
                            + "\n\t\tCONSTRAINT check_qty CHECK (quantity > 0)," //
                            + "\n\tcost NUMBER" //
                            + "\n\t\tCONSTRAINT check_cost CHECK (cost > 0)" //
                            + "\n);",//
                            SQLUtils.toSQLString(stmt, JdbcConstants.ORACLE));

        OracleSchemaStatVisitor visitor = new OracleSchemaStatVisitor();
        stmt.accept(visitor);

        System.out.println("Tables : " + visitor.getTables());
        System.out.println("fields : " + visitor.getColumns());
        System.out.println("coditions : " + visitor.getConditions());
        System.out.println("relationships : " + visitor.getRelationships());
        System.out.println("orderBy : " + visitor.getOrderByColumns());

        Assert.assertEquals(1, visitor.getTables().size());

        Assert.assertEquals(4, visitor.getColumns().size());

        Assert.assertTrue(visitor.getColumns().contains(new TableStat.Column("order_detail", "order_id")));
    }
}
