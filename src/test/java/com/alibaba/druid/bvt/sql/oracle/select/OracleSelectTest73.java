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
package com.alibaba.druid.bvt.sql.oracle.select;

import com.alibaba.druid.sql.OracleTest;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.oracle.parser.OracleStatementParser;
import com.alibaba.druid.sql.dialect.oracle.visitor.OracleSchemaStatVisitor;
import org.junit.Assert;

import java.util.List;

public class OracleSelectTest73 extends OracleTest {

    public void test_0() throws Exception {
        String sql = //
                "SELECT fact_1_id,\n" +
                        "       fact_2_id,\n" +
                        "       SUM(sales_value) AS sales_value\n" +
                        "FROM   dimension_tab\n" +
                        "GROUP BY CUBE (fact_1_id, fact_2_id)\n" +
                        "ORDER BY fact_1_id, fact_2_id;"; //

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

        assertEquals(1, visitor.getTables().size());

        assertEquals(3, visitor.getColumns().size());

        {
            String text = SQLUtils.toOracleString(stmt);

            assertEquals("SELECT fact_1_id, fact_2_id, SUM(sales_value) AS sales_value\n" +
                    "FROM dimension_tab\n" +
                    "GROUP BY CUBE (fact_1_id, fact_2_id\n" +
                    "ORDER BY fact_1_id, fact_2_id;", text);
        }
        // Assert.assertTrue(visitor.getColumns().contains(new TableStat.Column("acduser.vw_acd_info", "xzqh")));

        // Assert.assertTrue(visitor.getOrderByColumns().contains(new TableStat.Column("employees", "last_name")));
    }
}
