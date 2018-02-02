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
package com.alibaba.druid.bvt.sql.oracle.create;

import com.alibaba.druid.sql.OracleTest;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.oracle.parser.OracleStatementParser;
import com.alibaba.druid.sql.dialect.oracle.visitor.OracleSchemaStatVisitor;
import com.alibaba.druid.stat.TableStat;
import com.alibaba.druid.util.JdbcConstants;

import java.util.List;

public class OracleCreateFunctionTest_0 extends OracleTest {

    public void test_types() throws Exception {
        String sql = //
        "CREATE FUNCTION get_bal(acc_no IN NUMBER) \n" +
                "   RETURN NUMBER \n" +
                "   IS acc_bal NUMBER(11,2);\n" +
                "   BEGIN \n" +
                "      SELECT order_total \n" +
                "      INTO acc_bal \n" +
                "      FROM orders \n" +
                "      WHERE customer_id = acc_no; \n" +
                "      RETURN(acc_bal); \n" +
                "    END;";

        OracleStatementParser parser = new OracleStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();
        SQLStatement stmt = statementList.get(0);
        print(statementList);

        assertEquals(1, statementList.size());

        assertEquals("CREATE FUNCTION get_bal (\n" +
                        "\tacc_no IN NUMBER\n" +
                        ")\n" +
                        "RETURN NUMBER\n" +
                        "AS\n" +
                        "acc_bal NUMBER(11, 2);\n" +
                        "BEGIN\n" +
                        "\tSELECT order_total\n" +
                        "\tINTO acc_bal\n" +
                        "\tFROM orders\n" +
                        "\tWHERE customer_id = acc_no;\n" +
                        "\tRETURN acc_bal;\n" +
                        "END;",//
                            SQLUtils.toSQLString(stmt, JdbcConstants.ORACLE));

        OracleSchemaStatVisitor visitor = new OracleSchemaStatVisitor();
        stmt.accept(visitor);

        System.out.println("Tables : " + visitor.getTables());
        System.out.println("fields : " + visitor.getColumns());
        System.out.println("coditions : " + visitor.getConditions());
        System.out.println("relationships : " + visitor.getRelationships());
        System.out.println("orderBy : " + visitor.getOrderByColumns());

        assertEquals(1, visitor.getTables().size());

        assertEquals(3, visitor.getColumns().size());

        assertTrue(visitor.getColumns().contains(new TableStat.Column("orders", "order_total")));
    }
}
