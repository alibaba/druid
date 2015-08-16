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
package com.alibaba.druid.bvt.sql.db2;

import java.util.List;

import org.junit.Assert;

import com.alibaba.druid.sql.DB2Test;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.db2.parser.DB2StatementParser;
import com.alibaba.druid.sql.dialect.db2.visitor.DB2SchemaStatVisitor;
import com.alibaba.druid.stat.TableStat;
import com.alibaba.druid.stat.TableStat.Column;
import com.alibaba.druid.util.JdbcConstants;

public class DB2SelectTest_5 extends DB2Test {

    public void test_0() throws Exception {
        String sql = "SELECT * FROM (SELECT TEMP_TAB.*,ROWNUMBER() OVER() AS IDX FROM (SELECT" //
                + "            DISTINCT  ( OH.ORDER_ID )" //
                + "        FROM" //
                + "            ORDER_HEADER OH," //
                + "            ORDER_ITEM OI," //
                + "            ORDER_PAYMENT_PERFERENCE OPP," //
                + "            ORDER_SHIPMENT_PERFERENCE OSP" //
                + "        WHERE" //
                + "            OH.ORDER_ID = OI.ORDER_ID" //
                + "        AND OH.ORDER_ID = OPP.ORDER_ID" //
                + "        AND OH.ORDER_ID = OSP.ORDER_ID" //
                + "             " //
                + "                AND OH.ORDER_ID = ? ) AS TEMP_TAB ) AS TEMP_TAB_WITH_IDX "//
                + " WHERE TEMP_TAB_WITH_IDX.IDX > 0 AND TEMP_TAB_WITH_IDX.IDX <= 20" //
                + "";

        DB2StatementParser parser = new DB2StatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();
        SQLStatement stmt = statementList.get(0);
        print(statementList);

        Assert.assertEquals(1, statementList.size());

        DB2SchemaStatVisitor visitor = new DB2SchemaStatVisitor();
        stmt.accept(visitor);

        System.out.println("Tables : " + visitor.getTables());
        System.out.println("fields : " + visitor.getColumns());
        System.out.println("coditions : " + visitor.getConditions());
        System.out.println("orderBy : " + visitor.getOrderByColumns());

        Assert.assertEquals(4, visitor.getTables().size());
        Assert.assertEquals(4, visitor.getColumns().size());
        Assert.assertEquals(4, visitor.getConditions().size());

        Assert.assertTrue(visitor.getTables().containsKey(new TableStat.Name("ORDER_HEADER")));

        Assert.assertTrue(visitor.getColumns().contains(new Column("ORDER_HEADER", "ORDER_ID")));
        // Assert.assertTrue(visitor.getColumns().contains(new Column("mytable", "first_name")));
        // Assert.assertTrue(visitor.getColumns().contains(new Column("mytable", "full_name")));

        String output = SQLUtils.toSQLString(stmt, JdbcConstants.DB2);
        Assert.assertEquals("SELECT *"//
                + "\nFROM (SELECT TEMP_TAB.*, ROWNUMBER() OVER () AS IDX"//
                + "\n\tFROM (SELECT DISTINCT OH.ORDER_ID"//
                + "\n\t\tFROM ORDER_HEADER OH, ORDER_ITEM OI, ORDER_PAYMENT_PERFERENCE OPP, ORDER_SHIPMENT_PERFERENCE OSP"//
                + "\n\t\tWHERE OH.ORDER_ID = OI.ORDER_ID"//
                + "\n\t\t\tAND OH.ORDER_ID = OPP.ORDER_ID"//
                + "\n\t\t\tAND OH.ORDER_ID = OSP.ORDER_ID"//
                + "\n\t\t\tAND OH.ORDER_ID = ?"//
                + "\n\t\t) TEMP_TAB"//
                + "\n\t) TEMP_TAB_WITH_IDX"//
                + "\nWHERE TEMP_TAB_WITH_IDX.IDX > 0"//
                + "\n\tAND TEMP_TAB_WITH_IDX.IDX <= 20", //
                            output);
    }
}
