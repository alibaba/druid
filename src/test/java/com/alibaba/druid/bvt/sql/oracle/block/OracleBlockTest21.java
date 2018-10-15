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

public class OracleBlockTest21 extends OracleTest {

    public void test_0() throws Exception {
        String sql = "BEGIN\n" +
                "  FOR someone IN (\n" +
                "    SELECT * FROM employees\n" +
                "    WHERE employee_id < 120\n" +
                "    ORDER BY employee_id\n" +
                "  )\n" +
                "  LOOP\n" +
                "    DBMS_OUTPUT.PUT_LINE('First name = ' || someone.first_name ||\n" +
                "                         ', Last name = ' || someone.last_name);\n" +
                "  END LOOP;\n" +
                "END;"; //

        List<SQLStatement> stmtList = SQLUtils.parseStatements(sql, JdbcConstants.ORACLE);
        assertEquals(1, stmtList.size());

        String result = SQLUtils.toOracleString(stmtList.get(0));
        System.out.println(result);
        assertEquals("BEGIN\n" +
                "\tFOR someone IN (\n" +
                "\t\tSELECT *\n" +
                "\t\tFROM employees\n" +
                "\t\tWHERE employee_id < 120\n" +
                "\t\tORDER BY employee_id\n" +
                "\t)\n" +
                "\tLOOP\n" +
                "\t\tDBMS_OUTPUT.PUT_LINE('First name = ' || someone.first_name || ', Last name = ' || someone.last_name);\n" +
                "\tEND LOOP;\n" +
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

        assertEquals(1, visitor.getTables().size());

        assertTrue(visitor.getTables().containsKey(new TableStat.Name("employees")));

        assertEquals(2, visitor.getColumns().size());
        assertEquals(1, visitor.getConditions().size());
        assertEquals(0, visitor.getRelationships().size());

         assertTrue(visitor.getColumns().contains(new TableStat.Column("employees", "employee_id")));
    }
}
