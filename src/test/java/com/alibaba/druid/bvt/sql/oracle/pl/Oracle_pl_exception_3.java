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

public class Oracle_pl_exception_3 extends OracleTest {

    public void test_0() throws Exception {
        String sql = "DECLARE\n" +
				"  salary_too_high   EXCEPTION;\n" +
				"  current_salary    NUMBER := 20000;\n" +
				"  max_salary        NUMBER := 10000;\n" +
				"  erroneous_salary  NUMBER;\n" +
				"BEGIN\n" +
				"\n" +
				"  BEGIN\n" +
				"    IF current_salary > max_salary THEN\n" +
				"      RAISE salary_too_high;   -- raise exception\n" +
				"    END IF;\n" +
				"  EXCEPTION\n" +
				"    WHEN salary_too_high THEN  -- start handling exception\n" +
				"      erroneous_salary := current_salary;\n" +
				"      DBMS_OUTPUT.PUT_LINE('Salary ' || erroneous_salary ||' is out of range.');\n" +
				"      DBMS_OUTPUT.PUT_LINE ('Maximum salary is ' || max_salary || '.');\n" +
				"      RAISE;  -- reraise current exception (exception name is optional)\n" +
				"  END;\n" +
				"\n" +
				"EXCEPTION\n" +
				"  WHEN salary_too_high THEN    -- finish handling exception\n" +
				"    current_salary := max_salary;\n" +
				"\n" +
				"    DBMS_OUTPUT.PUT_LINE (\n" +
				"      'Revising salary from ' || erroneous_salary ||\n" +
				"      ' to ' || current_salary || '.'\n" +
				"    );\n" +
				"END;"; //

        List<SQLStatement> statementList = SQLUtils.parseStatements(sql, JdbcConstants.ORACLE);
		assertEquals(1, statementList.size());

        SchemaStatVisitor visitor = SQLUtils.createSchemaStatVisitor(JdbcConstants.ORACLE);
        for (SQLStatement statement : statementList) {
            statement.accept(visitor);
        }

//        System.out.println("Tables : " + visitor.getTables());
//        System.out.println("fields : " + visitor.getColumns());
//        System.out.println("coditions : " + visitor.getConditions());
//        System.out.println("relationships : " + visitor.getRelationships());
//        System.out.println("orderBy : " + visitor.getOrderByColumns());

        assertEquals(0, visitor.getTables().size());

//        Assert.assertTrue(visitor.getTables().containsKey(new TableStat.Name("employees")));
//        Assert.assertTrue(visitor.getTables().containsKey(new TableStat.Name("emp_name")));

//        Assert.assertEquals(7, visitor.getColumns().size());
//        Assert.assertEquals(3, visitor.getConditions().size());
//        Assert.assertEquals(1, visitor.getRelationships().size());

        // Assert.assertTrue(visitor.getColumns().contains(new TableStat.Column("employees", "salary")));

		{
			String output = SQLUtils.toSQLString(statementList, JdbcConstants.ORACLE);
			System.out.println(output);
			assertEquals("DECLARE\n" +
							"\tsalary_too_high EXCEPTION;\n" +
							"\tcurrent_salary NUMBER := 20000;\n" +
							"\tmax_salary NUMBER := 10000;\n" +
							"\terroneous_salary NUMBER;\n" +
							"BEGIN\n" +
							"\tBEGIN\n" +
							"\t\tIF current_salary > max_salary THEN\n" +
							"\t\t\tRAISE salary_too_high;\n" +
							"\t\tEND IF;\n" +
							"\tEXCEPTION\n" +
							"\t\tWHEN salary_too_high THEN\n" +
							"\t\t\terroneous_salary := current_salary;\n" +
							"\t\t\tDBMS_OUTPUT.PUT_LINE('Salary ' || erroneous_salary || ' is out of range.');\n" +
							"\t\t\tDBMS_OUTPUT.PUT_LINE('Maximum salary is ' || max_salary || '.');\n" +
							"\t\t\tRAISE;\n" +
							"\tEND;\n" +
							"EXCEPTION\n" +
							"\tWHEN salary_too_high THEN\n" +
							"\t\tcurrent_salary := max_salary;\n" +
							"\t\tDBMS_OUTPUT.PUT_LINE('Revising salary from ' || erroneous_salary || ' to ' || current_salary || '.');\n" +
							"END;", //
					output);
		}
		{
			String output = SQLUtils.toSQLString(statementList, JdbcConstants.ORACLE, SQLUtils.DEFAULT_LCASE_FORMAT_OPTION);
			assertEquals("declare\n" +
							"\tsalary_too_high EXCEPTION;\n" +
							"\tcurrent_salary NUMBER := 20000;\n" +
							"\tmax_salary NUMBER := 10000;\n" +
							"\terroneous_salary NUMBER;\n" +
							"begin\n" +
							"\tbegin\n" +
							"\t\tif current_salary > max_salary then\n" +
							"\t\t\traise salary_too_high;\n" +
							"\t\tend if;\n" +
							"\texception\n" +
							"\t\twhen salary_too_high then\n" +
							"\t\t\terroneous_salary := current_salary;\n" +
							"\t\t\tDBMS_OUTPUT.PUT_LINE('Salary ' || erroneous_salary || ' is out of range.');\n" +
							"\t\t\tDBMS_OUTPUT.PUT_LINE('Maximum salary is ' || max_salary || '.');\n" +
							"\t\t\traise;\n" +
							"\tend;\n" +
							"exception\n" +
							"\twhen salary_too_high then\n" +
							"\t\tcurrent_salary := max_salary;\n" +
							"\t\tDBMS_OUTPUT.PUT_LINE('Revising salary from ' || erroneous_salary || ' to ' || current_salary || '.');\n" +
							"end;", //
					output);
		}
	}
}
