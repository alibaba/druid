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

public class OracleSelectTest63_with_recursive extends OracleTest {

    public void test_0() throws Exception {
        String sql = //
                "WITH t1(id, parent_id) AS (\n" +
                        "  -- Anchor member.\n" +
                        "  SELECT id,\n" +
                        "         parent_id\n" +
                        "  FROM   tab1\n" +
                        "  WHERE  parent_id IS NULL\n" +
                        "  UNION ALL\n" +
                        "  -- Recursive member.\n" +
                        "  SELECT t2.id,\n" +
                        "         t2.parent_id\n" +
                        "  FROM   tab1 t2, t1\n" +
                        "  WHERE  t2.parent_id = t1.id\n" +
                        ")\n" +
                        "SEARCH BREADTH FIRST BY id SET order1\n" +
                        "SELECT id,\n" +
                        "       parent_id\n" +
                        "FROM   t1\n" +
                        "ORDER BY order1;"; //

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

        Assert.assertEquals(1, visitor.getTables().size());

        Assert.assertEquals(2, visitor.getColumns().size());

        {
            String text = SQLUtils.toOracleString(stmt);

            assertEquals("WITH t1 (id, parent_id) AS (\n" +
                    "\t\t-- Anchor member.\n" +
                    "\t\tSELECT id, parent_id\n" +
                    "\t\tFROM tab1\n" +
                    "\t\tWHERE parent_id IS NULL\n" +
                    "\t\tUNION ALL\n" +
                    "\t\t-- Recursive member.\n" +
                    "\t\tSELECT t2.id, t2.parent_id\n" +
                    "\t\tFROM tab1 t2, t1\n" +
                    "\t\tWHERE t2.parent_id = t1.id\n" +
                    "\t)\n" +
                    "\tSEARCH BREADTH FIRST BY id SET order1\n" +
                    "SELECT id, parent_id\n" +
                    "FROM t1\n" +
                    "ORDER BY order1;", text);
        }

        {
            String text = SQLUtils.toOracleString(stmt, SQLUtils.DEFAULT_LCASE_FORMAT_OPTION);

            Assert.assertEquals("with t1 (id, parent_id) as (\n" +
                    "\t\t-- Anchor member.\n" +
                    "\t\tselect id, parent_id\n" +
                    "\t\tfrom tab1\n" +
                    "\t\twhere parent_id is null\n" +
                    "\t\tunion all\n" +
                    "\t\t-- Recursive member.\n" +
                    "\t\tselect t2.id, t2.parent_id\n" +
                    "\t\tfrom tab1 t2, t1\n" +
                    "\t\twhere t2.parent_id = t1.id\n" +
                    "\t)\n" +
                    "\tsearch BREADTH first by id set order1\n" +
                    "select id, parent_id\n" +
                    "from t1\n" +
                    "order by order1;", text);
        }
        // Assert.assertTrue(visitor.getColumns().contains(new TableStat.Column("acduser.vw_acd_info", "xzqh")));

        // Assert.assertTrue(visitor.getOrderByColumns().contains(new TableStat.Column("employees", "last_name")));
    }
}
