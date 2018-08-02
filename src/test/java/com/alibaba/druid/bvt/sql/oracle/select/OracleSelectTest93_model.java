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
package com.alibaba.druid.bvt.sql.oracle.select;

import com.alibaba.druid.sql.OracleTest;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.statement.SQLSelectStatement;
import com.alibaba.druid.sql.dialect.oracle.parser.OracleStatementParser;
import com.alibaba.druid.sql.dialect.oracle.visitor.OracleSchemaStatVisitor;
import org.junit.Assert;

import java.util.List;

public class OracleSelectTest93_model extends OracleTest {

    public void test_0() throws Exception {
        String sql = //
                "select country,prod,year,s\n" +
                        "from sales_view_ref\n" +
                        "model\n" +
                        "partition by (country)\n" +
                        "dimension by (prod, year)\n" +
                        "measures (sale s)\n" +
                        "ignore nav\n" +
                        "-- cell_reference_options\n" +
                        "unique dimension\n" +
                        "-- here starts model_rules_clause\n" +
                        "rules upsert sequential order\n" +
                        "(\n" +
                        "s[prod='mouse pad', year=2001] = s['mouse pad', 1999] + s['mouse pad', 2000],\n" +
                        "s['standard mouse', 2002] = s['standard mouse', 2001]\n" +
                        ")\n" +
                        "order by country, prod, year"; //

        System.out.println(sql);

        OracleStatementParser parser = new OracleStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();
        SQLSelectStatement stmt = (SQLSelectStatement) statementList.get(0);
        System.out.println(stmt.toString());

        Assert.assertEquals(1, statementList.size());

        OracleSchemaStatVisitor visitor = new OracleSchemaStatVisitor();
        stmt.accept(visitor);

        {
            String text = SQLUtils.toOracleString(stmt);

            assertEquals("SELECT country, prod, year, s\n" +
                    "FROM sales_view_ref\n" +
                    "MODEL\n" +
                    "\tPARTITION BY (country)\n" +
                    "\tDIMENSION BY (prod, year)\n" +
                    "\tMEASURES (sale s)\n" +
                    "\tIGNORE NAV\n" +
                    "\tUNIQUE DIMENSION\n" +
                    "\tRULES UPSERT SEQUENTIAL ORDER (s[prod = 'mouse pad', year = 2001] = s['mouse pad', 1999] + s['mouse pad', 2000], s['standard mouse', 2002] = s['standard mouse', 2001])\n" +
                    "ORDER BY country, prod, year", text);
        }

        System.out.println("Tables : " + visitor.getTables());
        System.out.println("fields : " + visitor.getColumns());
        System.out.println("coditions : " + visitor.getConditions());
        System.out.println("relationships : " + visitor.getRelationships());
        System.out.println("orderBy : " + visitor.getOrderByColumns());

        assertEquals(1, visitor.getTables().size());
        assertEquals(4, visitor.getColumns().size());
        assertEquals(0, visitor.getConditions().size());
        assertEquals(0, visitor.getRelationships().size());
        assertEquals(3, visitor.getOrderByColumns().size());
    }


}
