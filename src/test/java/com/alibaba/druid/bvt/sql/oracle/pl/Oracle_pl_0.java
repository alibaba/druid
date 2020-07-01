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

public class Oracle_pl_0 extends OracleTest {

    public void test_0() throws Exception {
        String sql = "CREATE PROCEDURE remove_emp (employee_id NUMBER) AS\n" +
				"   tot_emps NUMBER;\n" +
				"   BEGIN\n" +
				"      DELETE FROM employees\n" +
				"      WHERE employees.employee_id = remove_emp.employee_id;\n" +
				"   tot_emps := tot_emps - 1;\n" +
				"   END;"; //

        List<SQLStatement> statementList = SQLUtils.parseStatements(sql, JdbcConstants.ORACLE);
		SQLStatement stmt = statementList.get(0);

        assertEquals(1, statementList.size());

        SchemaStatVisitor visitor = SQLUtils.createSchemaStatVisitor(JdbcConstants.ORACLE);
        for (SQLStatement statement : statementList) {
            statement.accept(visitor);
        }

		System.out.println(stmt);

        System.out.println("Tables : " + visitor.getTables());
        System.out.println("fields : " + visitor.getColumns());
        System.out.println("coditions : " + visitor.getConditions());
        System.out.println("relationships : " + visitor.getRelationships());
//        System.out.println("orderBy : " + visitor.getOrderByColumns());

        assertEquals(1, visitor.getTables().size());
        assertEquals(1, visitor.getColumns().size());

        assertTrue(visitor.containsTable("employees"));
		assertTrue(visitor.containsColumn("employees", "employee_id"));
//        Assert.assertTrue(visitor.getTables().containsKey(new TableStat.Name("emp_name")));

//        Assert.assertEquals(7, visitor.getColumns().size());
//        Assert.assertEquals(3, visitor.getConditions().size());
//        Assert.assertEquals(1, visitor.getRelationships().size());

        // Assert.assertTrue(visitor.getColumns().contains(new TableStat.Column("employees", "salary")));

		{
			String output = SQLUtils.toOracleString(stmt);
			assertEquals("CREATE PROCEDURE remove_emp (\n" +
							"\temployee_id NUMBER\n" +
							")\n" +
							"AS\n" +
							"\ttot_emps NUMBER;\n" +
							"BEGIN\n" +
							"\tDELETE FROM employees\n" +
							"\tWHERE employees.employee_id = remove_emp.employee_id;\n" +
							"\ttot_emps := tot_emps - 1;\n" +
							"END;", //
					output);
		}
		{
			String output = SQLUtils.toOracleString(stmt, SQLUtils.DEFAULT_LCASE_FORMAT_OPTION);
			assertEquals("create procedure remove_emp (\n" +
							"\temployee_id NUMBER\n" +
							")\n" +
							"as\n" +
							"\ttot_emps NUMBER;\n" +
							"begin\n" +
							"\tdelete from employees\n" +
							"\twhere employees.employee_id = remove_emp.employee_id;\n" +
							"\ttot_emps := tot_emps - 1;\n" +
							"end;", //
					output);
		}
	}
}
