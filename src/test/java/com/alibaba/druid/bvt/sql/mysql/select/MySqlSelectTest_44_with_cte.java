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
package com.alibaba.druid.bvt.sql.mysql.select;

import com.alibaba.druid.sql.MysqlTest;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.visitor.SchemaStatVisitor;
import com.alibaba.druid.util.JdbcConstants;

import java.util.List;

public class MySqlSelectTest_44_with_cte extends MysqlTest {

    public void test_0() throws Exception {
        String sql = "WITH RECURSIVE cte AS\n" +
                "(\n" +
                "  SELECT 1 AS n, 1 AS p, -1 AS q\n" +
                "  UNION ALL\n" +
                "  SELECT n + 1, q * 2, p * 2 FROM cte WHERE n < 5\n" +
                ")\n" +
                "SELECT * FROM cte;";


        List<SQLStatement> statementList = SQLUtils.parseStatements(sql, JdbcConstants.MYSQL, true);
        SQLStatement stmt = statementList.get(0);

        assertEquals(1, statementList.size());

        SchemaStatVisitor visitor = SQLUtils.createSchemaStatVisitor(JdbcConstants.MYSQL);
        stmt.accept(visitor);

//        System.out.println("Tables : " + visitor.getTables());
//        System.out.println("fields : " + visitor.getColumns());
//        System.out.println("coditions : " + visitor.getConditions());
//        System.out.println("orderBy : " + visitor.getOrderByColumns());
        
        assertEquals(0, visitor.getTables().size());
        assertEquals(0, visitor.getColumns().size());
//        assertEquals(0, visitor.getConditions().size());
//        assertEquals(0, visitor.getOrderByColumns().size());
        
        {
            String output = SQLUtils.toMySqlString(stmt);
            assertEquals("WITH RECURSIVE cte AS (\n" +
                            "\t\tSELECT 1 AS n, 1 AS p, -1 AS q\n" +
                            "\t\tUNION ALL\n" +
                            "\t\tSELECT n + 1, q * 2\n" +
                            "\t\t\t, p * 2\n" +
                            "\t\tFROM cte\n" +
                            "\t\tWHERE n < 5\n" +
                            "\t)\n" +
                            "SELECT *\n" +
                            "FROM cte;", //
                                output);
        }
        {
            String output = SQLUtils.toMySqlString(stmt, SQLUtils.DEFAULT_LCASE_FORMAT_OPTION);
            assertEquals("with recursive cte as (\n" +
                            "\t\tselect 1 as n, 1 as p, -1 as q\n" +
                            "\t\tunion all\n" +
                            "\t\tselect n + 1, q * 2\n" +
                            "\t\t\t, p * 2\n" +
                            "\t\tfrom cte\n" +
                            "\t\twhere n < 5\n" +
                            "\t)\n" +
                            "select *\n" +
                            "from cte;", //
                                output);
        }

        {
            String output = SQLUtils.toMySqlString(stmt, new SQLUtils.FormatOption(true, true, true));
            assertEquals("WITH RECURSIVE cte AS (\n" +
                            "\t\tSELECT ? AS n, ? AS p, ? AS q\n" +
                            "\t\tUNION ALL\n" +
                            "\t\tSELECT n + ?, q * ?\n" +
                            "\t\t\t, p * ?\n" +
                            "\t\tFROM cte\n" +
                            "\t\tWHERE n < ?\n" +
                            "\t)\n" +
                            "SELECT *\n" +
                            "FROM cte;", //
                    output);
        }
    }
}
