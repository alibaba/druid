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

public class OracleBlockTest18 extends OracleTest {

    public void test_0() throws Exception {
        String sql = "CREATE PROCEDURE p (\n" +
                "    sales  NUMBER,\n" +
                "    quota  NUMBER,\n" +
                "    emp_id NUMBER\n" +
                "  )\n" +
                "  IS\n" +
                "    bonus  NUMBER := 0;\n" +
                "  BEGIN\n" +
                "    IF sales > (quota + 200) THEN\n" +
                "      bonus := (sales - quota)/4;\n" +
                "    ELSE\n" +
                "      bonus := 50;\n" +
                "    END IF;\n" +
                " \n" +
                "    DBMS_OUTPUT.PUT_LINE('bonus = ' || bonus);\n" +
                " \n" +
                "    UPDATE employees\n" +
                "    SET salary = salary + bonus \n" +
                "    WHERE employee_id = emp_id;\n" +
                "  END p;"; //

        List<SQLStatement> stmtList = SQLUtils.parseStatements(sql, JdbcConstants.ORACLE);
        assertEquals(1, stmtList.size());

        String result = SQLUtils.toOracleString(stmtList.get(0));
        System.out.println(result);
        assertEquals("CREATE PROCEDURE p (\n" +
                "\tsales NUMBER, \n" +
                "\tquota NUMBER, \n" +
                "\temp_id NUMBER\n" +
                ")\n" +
                "AS\n" +
                "\tbonus NUMBER := 0;\n" +
                "BEGIN\n" +
                "\tIF sales > quota + 200 THEN\n" +
                "\t\tbonus := (sales - quota) / 4;\n" +
                "\tELSE\n" +
                "\t\tbonus := 50;\n" +
                "\tEND IF;\n" +
                "\tDBMS_OUTPUT.PUT_LINE('bonus = ' || bonus);\n" +
                "\tUPDATE employees\n" +
                "\tSET salary = salary + bonus\n" +
                "\tWHERE employee_id = emp_id;\n" +
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

        // Assert.assertTrue(visitor.getColumns().contains(new TableStat.Column("employees", "salary")));
    }
}
