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
package com.alibaba.druid.bvt.sql.oracle.block;

import com.alibaba.druid.sql.OracleTest;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.oracle.visitor.OracleSchemaStatVisitor;
import com.alibaba.druid.stat.TableStat;
import com.alibaba.druid.util.JdbcConstants;

import java.util.List;

public class OracleBlockTest16 extends OracleTest {

    public void test_0() throws Exception {
        String sql = "DECLARE\n" +
                "  s  PLS_INTEGER := 0;\n" +
                "  i  PLS_INTEGER := 0;\n" +
                "  j  PLS_INTEGER;\n" +
                "BEGIN\n" +
                "  <<outer_loop>>\n" +
                "  LOOP\n" +
                "    i := i + 1;\n" +
                "    j := 0;\n" +
                "    <<inner_loop>>\n" +
                "    LOOP\n" +
                "      j := j + 1;\n" +
                "      s := s + i * j; -- Sum several products\n" +
                "      EXIT inner_loop WHEN (j > 5);\n" +
                "      EXIT outer_loop WHEN ((i * j) > 15);\n" +
                "    END LOOP inner_loop;\n" +
                "  END LOOP outer_loop;\n" +
                "  DBMS_OUTPUT.PUT_LINE\n" +
                "    ('The sum of products equals: ' || TO_CHAR(s));\n" +
                "END;"; //

        List<SQLStatement> stmtList = SQLUtils.parseStatements(sql, JdbcConstants.ORACLE);
        assertEquals(1, stmtList.size());

        String result = SQLUtils.toOracleString(stmtList.get(0));
        System.out.println(result);
        assertEquals("DECLARE\n" +
                "\ts PLS_INTEGER := 0;\n" +
                "\ti PLS_INTEGER := 0;\n" +
                "\tj PLS_INTEGER;\n" +
                "BEGIN\n" +
                "\t<<outer_loop>>\n" +
                "\tLOOP\n" +
                "\t\ti := i + 1;\n" +
                "\t\tj := 0;\n" +
                "\t\t<<inner_loop>>\n" +
                "\t\tLOOP\n" +
                "\t\t\tj := j + 1;\n" +
                "\t\t\ts := s + i * j;\n" +
                "\t\t\tEXIT inner_loop WHEN j > 5;\n" +
                "\t\t\tEXIT outer_loop WHEN i * j > 15;\n" +
                "\t\tEND LOOP inner_loop;\n" +
                "\tEND LOOP outer_loop;\n" +
                "\tDBMS_OUTPUT.PUT_LINE('The sum of products equals: ' || TO_CHAR(s));\n" +
                "END;", result);

        assertEquals(1, stmtList.size());

        OracleSchemaStatVisitor visitor = new OracleSchemaStatVisitor();
        for (SQLStatement statement : stmtList) {
            statement.accept(visitor);
        }

        System.out.println("Tables : " + visitor.getTables());
        System.out.println("fields : " + visitor.getColumns());
        System.out.println("coditions : " + visitor.getConditions());
        System.out.println("relationships : " + visitor.getRelationships());
        System.out.println("orderBy : " + visitor.getOrderByColumns());

        assertEquals(0, visitor.getTables().size());

//        Assert.assertTrue(visitor.getTables().containsKey(new TableStat.Name("employees")));

        assertEquals(0, visitor.getColumns().size());
        assertEquals(0, visitor.getConditions().size());
        assertEquals(0, visitor.getRelationships().size());

        // Assert.assertTrue(visitor.getColumns().contains(new TableStat.Column("employees", "salary")));
    }
}
