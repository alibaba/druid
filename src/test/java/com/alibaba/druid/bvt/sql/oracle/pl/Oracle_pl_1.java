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
package com.alibaba.druid.bvt.sql.oracle.pl;

import com.alibaba.druid.sql.OracleTest;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.visitor.SchemaStatVisitor;
import com.alibaba.druid.util.JdbcConstants;

import java.util.List;

public class Oracle_pl_1 extends OracleTest {

    public void test_0() throws Exception {
        String sql = "DROP TABLE employees_temp;\n" +
				"CREATE TABLE employees_temp AS\n" +
				"  SELECT employee_id, first_name, last_name \n" +
				"  FROM employees;\n" +
				" \n" +
				"DECLARE\n" +
				"  emp_id          employees_temp.employee_id%TYPE := 299;\n" +
				"  emp_first_name  employees_temp.first_name%TYPE  := 'Bob';\n" +
				"  emp_last_name   employees_temp.last_name%TYPE   := 'Henry';\n" +
				"BEGIN\n" +
				"  INSERT INTO employees_temp (employee_id, first_name, last_name) \n" +
				"  VALUES (emp_id, emp_first_name, emp_last_name);\n" +
				" \n" +
				"  UPDATE employees_temp\n" +
				"  SET first_name = 'Robert'\n" +
				"  WHERE employee_id = emp_id;\n" +
				" \n" +
				"  DELETE FROM employees_temp\n" +
				"  WHERE employee_id = emp_id\n" +
				"  RETURNING first_name, last_name\n" +
				"  INTO emp_first_name, emp_last_name;\n" +
				" \n" +
				"  COMMIT;\n" +
				"  DBMS_OUTPUT.PUT_LINE (emp_first_name || ' ' || emp_last_name);\n" +
				"END;"; //

        List<SQLStatement> statementList = SQLUtils.parseStatements(sql, JdbcConstants.ORACLE);
		assertEquals(3, statementList.size());
		SQLStatement stmt = statementList.get(2);

        SchemaStatVisitor visitor = SQLUtils.createSchemaStatVisitor(JdbcConstants.ORACLE);
        for (SQLStatement statement : statementList) {
            statement.accept(visitor);
        }

//        System.out.println("Tables : " + visitor.getTables());
//        System.out.println("fields : " + visitor.getColumns());
//        System.out.println("coditions : " + visitor.getConditions());
//        System.out.println("relationships : " + visitor.getRelationships());
//        System.out.println("orderBy : " + visitor.getOrderByColumns());

        assertEquals(2, visitor.getTables().size());

//        Assert.assertTrue(visitor.getTables().containsKey(new TableStat.Name("employees")));
//        Assert.assertTrue(visitor.getTables().containsKey(new TableStat.Name("emp_name")));

//        Assert.assertEquals(7, visitor.getColumns().size());
//        Assert.assertEquals(3, visitor.getConditions().size());
//        Assert.assertEquals(1, visitor.getRelationships().size());

        // Assert.assertTrue(visitor.getColumns().contains(new TableStat.Column("employees", "salary")));

		{
			String output = SQLUtils.toOracleString(stmt);
			assertEquals("DECLARE\n" +
							"\temp_id employees_temp.employee_id%TYPE := 299;\n" +
							"\temp_first_name employees_temp.first_name%TYPE := 'Bob';\n" +
							"\temp_last_name employees_temp.last_name%TYPE := 'Henry';\n" +
							"BEGIN\n" +
							"\tINSERT INTO employees_temp (employee_id, first_name, last_name)\n" +
							"\tVALUES (emp_id, emp_first_name, emp_last_name);\n" +
							"\tUPDATE employees_temp\n" +
							"\tSET first_name = 'Robert'\n" +
							"\tWHERE employee_id = emp_id;\n" +
							"\tDELETE FROM employees_temp\n" +
							"\tWHERE employee_id = emp_id\n" +
							"\tRETURNING first_name, last_name INTO emp_first_name, emp_last_name;\n" +
							"\tCOMMIT;\n" +
							"\tDBMS_OUTPUT.PUT_LINE(emp_first_name || ' ' || emp_last_name);\n" +
							"END;", //
					output);
		}
		{
			String output = SQLUtils.toOracleString(stmt, SQLUtils.DEFAULT_LCASE_FORMAT_OPTION);
			assertEquals("declare\n" +
							"\temp_id employees_temp.employee_id%TYPE := 299;\n" +
							"\temp_first_name employees_temp.first_name%TYPE := 'Bob';\n" +
							"\temp_last_name employees_temp.last_name%TYPE := 'Henry';\n" +
							"begin\n" +
							"\tinsert into employees_temp (employee_id, first_name, last_name)\n" +
							"\tvalues (emp_id, emp_first_name, emp_last_name);\n" +
							"\tupdate employees_temp\n" +
							"\tset first_name = 'Robert'\n" +
							"\twhere employee_id = emp_id;\n" +
							"\tdelete from employees_temp\n" +
							"\twhere employee_id = emp_id\n" +
							"\treturning first_name, last_name into emp_first_name, emp_last_name;\n" +
							"\tcommit;\n" +
							"\tDBMS_OUTPUT.PUT_LINE(emp_first_name || ' ' || emp_last_name);\n" +
							"end;", //
					output);
		}
	}
}
