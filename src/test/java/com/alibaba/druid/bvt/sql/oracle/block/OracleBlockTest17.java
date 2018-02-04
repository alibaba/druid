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
import com.alibaba.druid.util.JdbcConstants;

import java.util.List;

public class OracleBlockTest17 extends OracleTest {

    public void test_0() throws Exception {
        String sql = "DECLARE\n" +
                "  x NUMBER := 0;\n" +
                "BEGIN\n" +
                "  LOOP -- After CONTINUE statement, control resumes here\n" +
                "    DBMS_OUTPUT.PUT_LINE ('Inside loop:  x = ' || TO_CHAR(x));\n" +
                "    x := x + 1;\n" +
                "    IF x < 3 THEN\n" +
                "      CONTINUE;\n" +
                "    END IF;\n" +
                "    DBMS_OUTPUT.PUT_LINE\n" +
                "      ('Inside loop, after CONTINUE:  x = ' || TO_CHAR(x));\n" +
                "    EXIT WHEN x = 5;\n" +
                "  END LOOP;\n" +
                " \n" +
                "  DBMS_OUTPUT.PUT_LINE (' After loop:  x = ' || TO_CHAR(x));\n" +
                "END;"; //

        List<SQLStatement> stmtList = SQLUtils.parseStatements(sql, JdbcConstants.ORACLE);
        assertEquals(1, stmtList.size());

        String result = SQLUtils.toOracleString(stmtList.get(0));
        System.out.println(result);
        assertEquals("DECLARE\n" +
                "\tx NUMBER := 0;\n" +
                "BEGIN\n" +
                "\tLOOP\n" +
                "\t\tDBMS_OUTPUT.PUT_LINE('Inside loop:  x = ' || TO_CHAR(x));\n" +
                "\t\tx := x + 1;\n" +
                "\t\tIF x < 3 THEN\n" +
                "\t\t\tCONTINUE;\n" +
                "\t\tEND IF;\n" +
                "\t\tDBMS_OUTPUT.PUT_LINE('Inside loop, after CONTINUE:  x = ' || TO_CHAR(x));\n" +
                "\t\tEXIT WHEN x = 5;\n" +
                "\tEND LOOP;\n" +
                "\tDBMS_OUTPUT.PUT_LINE(' After loop:  x = ' || TO_CHAR(x));\n" +
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
