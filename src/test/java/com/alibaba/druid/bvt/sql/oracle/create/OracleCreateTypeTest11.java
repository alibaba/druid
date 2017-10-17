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
import com.alibaba.druid.util.JdbcConstants;

import java.util.List;

public class OracleCreateTypeTest11 extends OracleTest {

    public void test_types() throws Exception {
        String sql = "CREATE TYPE BODY rational\n" +
                "  IS\n" +
                "   MAP MEMBER FUNCTION rat_to_real RETURN REAL IS\n" +
                "      BEGIN\n" +
                "         RETURN numerator/denominator;\n" +
                "      END;\n" +
                "\n" +
                "   MEMBER PROCEDURE normalize IS\n" +
                "      gcd NUMBER := integer_operations.greatest_common_divisor\n" +
                "                     (numerator, denominator);\n" +
                "      BEGIN\n" +
                "         numerator := numerator/gcd;\n" +
                "         denominator := denominator/gcd;\n" +
                "      END;\n" +
                "\n" +
                "   MEMBER FUNCTION plus(x rational) RETURN rational IS\n" +
                "      r rational := rational_operations.make_rational\n" +
                "                      (numerator*x.denominator +\n" +
                "                       x.numerator*denominator,\n" +
                "                       denominator*x.denominator);\n" +
                "      BEGIN\n" +
                "         RETURN r;\n" +
                "      END;\n" +
                "\n" +
                "   END;";

        System.out.println(sql);


        OracleStatementParser parser = new OracleStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();
        SQLStatement stmt = statementList.get(0);
        print(statementList);

        assertEquals(1, statementList.size());

        assertEquals("CREATE TYPE BODY rational IS\n" +
                        "\tMAP MEMBER FUNCTION rat_to_real () RETURN REAL\n" +
                        "\tIS\n" +
                        "\tBEGIN\n" +
                        "\t\tRETURN numerator / denominator;\n" +
                        "\tEND;\n" +
                        "\tMEMBER PROCEDURE normalize\n" +
                        "\tIS\n" +
                        "\tgcd NUMBER := integer_operations.greatest_common_divisor(numerator, denominator);\n" +
                        "\tBEGIN\n" +
                        "\t\tnumerator := numerator / gcd;\n" +
                        "\t\tdenominator := denominator / gcd;\n" +
                        "\tEND;\n" +
                        "\tMEMBER FUNCTION plus (x rational) RETURN rational\n" +
                        "\tIS\n" +
                        "\tr rational := rational_operations.make_rational(numerator * x.denominator + x.numerator * denominator, denominator * x.denominator);\n" +
                        "\tBEGIN\n" +
                        "\t\tRETURN r;\n" +
                        "\tEND;\n" +
                        "END;",//
                            SQLUtils.toSQLString(stmt, JdbcConstants.ORACLE));

        OracleSchemaStatVisitor visitor = new OracleSchemaStatVisitor();
        stmt.accept(visitor);

        System.out.println("Tables : " + visitor.getTables());
        System.out.println("fields : " + visitor.getColumns());
        System.out.println("coditions : " + visitor.getConditions());
        System.out.println("relationships : " + visitor.getRelationships());
        System.out.println("orderBy : " + visitor.getOrderByColumns());

        assertEquals(0, visitor.getTables().size());

        assertEquals(0, visitor.getColumns().size());

        SQLUtils.toPGString(stmt);

//        assertTrue(visitor.getColumns().contains(new TableStat.Column("orders", "order_total")));
    }
}
