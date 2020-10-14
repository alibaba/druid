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
package com.alibaba.druid.bvt.sql.oracle.pl;

import com.alibaba.druid.sql.OracleTest;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.visitor.SchemaStatVisitor;
import com.alibaba.druid.util.JdbcConstants;

import java.util.List;

public class Oracle_pl_open_for_0 extends OracleTest {

	public void test_0() throws Exception {
		String sql = "DECLARE\n" +
				"  sal           employees.salary%TYPE;\n" +
				"  sal_multiple  employees.salary%TYPE;\n" +
				"  factor        INTEGER := 2;\n" +
				" \n" +
				"  cv SYS_REFCURSOR;\n" +
				" \n" +
				"BEGIN\n" +
				"  DBMS_OUTPUT.PUT_LINE('factor = ' || factor);\n" +
				" \n" +
				"  OPEN cv FOR\n" +
				"    SELECT salary, salary*factor\n" +
				"    FROM employees\n" +
				"    WHERE job_id LIKE 'AD_%';   -- PL/SQL evaluates factor\n" +
				" \n" +
				"  LOOP\n" +
				"    FETCH cv INTO sal, sal_multiple;\n" +
				"    EXIT WHEN cv%NOTFOUND;\n" +
				"    DBMS_OUTPUT.PUT_LINE('sal          = ' || sal);\n" +
				"    DBMS_OUTPUT.PUT_LINE('sal_multiple = ' || sal_multiple);\n" +
				"  END LOOP;\n" +
				" \n" +
				"  factor := factor + 1;\n" +
				" \n" +
				"  DBMS_OUTPUT.PUT_LINE('factor = ' || factor);\n" +
				" \n" +
				"  OPEN cv FOR\n" +
				"    SELECT salary, salary*factor\n" +
				"    FROM employees\n" +
				"    WHERE job_id LIKE 'AD_%';   -- PL/SQL evaluates factor\n" +
				" \n" +
				"  LOOP\n" +
				"    FETCH cv INTO sal, sal_multiple;\n" +
				"    EXIT WHEN cv%NOTFOUND;\n" +
				"    DBMS_OUTPUT.PUT_LINE('sal          = ' || sal);\n" +
				"    DBMS_OUTPUT.PUT_LINE('sal_multiple = ' || sal_multiple);\n" +
				"  END LOOP;\n" +
				" \n" +
				"  CLOSE cv;\n" +
				"END;"; //

		List<SQLStatement> statementList = SQLUtils.parseStatements(sql, JdbcConstants.ORACLE);
		SQLStatement stmt = statementList.get(0);

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
			String output = SQLUtils.toOracleString(stmt);
			assertEquals("DECLARE\n" +
							"\tsal employees.salary%TYPE;\n" +
							"\tsal_multiple employees.salary%TYPE;\n" +
							"\tfactor INTEGER := 2;\n" +
							"\tcv SYS_REFCURSOR;\n" +
							"BEGIN\n" +
							"\tDBMS_OUTPUT.PUT_LINE('factor = ' || factor);\n" +
							"\tOPEN cv FOR \n" +
							"\t\tSELECT salary, salary * factor\n" +
							"\t\tFROM employees\n" +
							"\t\tWHERE job_id LIKE 'AD_%';\n" +
							"\tLOOP\n" +
							"\t\tFETCH cv INTO sal, sal_multiple;\n" +
							"\t\tEXIT WHEN cv%NOTFOUND;\n" +
							"\t\tDBMS_OUTPUT.PUT_LINE('sal          = ' || sal);\n" +
							"\t\tDBMS_OUTPUT.PUT_LINE('sal_multiple = ' || sal_multiple);\n" +
							"\tEND LOOP;\n" +
							"\tfactor := factor + 1;\n" +
							"\tDBMS_OUTPUT.PUT_LINE('factor = ' || factor);\n" +
							"\tOPEN cv FOR \n" +
							"\t\tSELECT salary, salary * factor\n" +
							"\t\tFROM employees\n" +
							"\t\tWHERE job_id LIKE 'AD_%';\n" +
							"\tLOOP\n" +
							"\t\tFETCH cv INTO sal, sal_multiple;\n" +
							"\t\tEXIT WHEN cv%NOTFOUND;\n" +
							"\t\tDBMS_OUTPUT.PUT_LINE('sal          = ' || sal);\n" +
							"\t\tDBMS_OUTPUT.PUT_LINE('sal_multiple = ' || sal_multiple);\n" +
							"\tEND LOOP;\n" +
							"\tCLOSE cv;\n" +
							"END;", //
					output);
		}
		{
			String output = SQLUtils.toOracleString(stmt, SQLUtils.DEFAULT_LCASE_FORMAT_OPTION);
			assertEquals("declare\n" +
							"\tsal employees.salary%TYPE;\n" +
							"\tsal_multiple employees.salary%TYPE;\n" +
							"\tfactor INTEGER := 2;\n" +
							"\tcv SYS_REFCURSOR;\n" +
							"begin\n" +
							"\tDBMS_OUTPUT.PUT_LINE('factor = ' || factor);\n" +
							"\topen cv for \n" +
							"\t\tselect salary, salary * factor\n" +
							"\t\tfrom employees\n" +
							"\t\twhere job_id like 'AD_%';\n" +
							"\tloop\n" +
							"\t\tfetch cv into sal, sal_multiple;\n" +
							"\t\texit when cv%NOTFOUND;\n" +
							"\t\tDBMS_OUTPUT.PUT_LINE('sal          = ' || sal);\n" +
							"\t\tDBMS_OUTPUT.PUT_LINE('sal_multiple = ' || sal_multiple);\n" +
							"\tend loop;\n" +
							"\tfactor := factor + 1;\n" +
							"\tDBMS_OUTPUT.PUT_LINE('factor = ' || factor);\n" +
							"\topen cv for \n" +
							"\t\tselect salary, salary * factor\n" +
							"\t\tfrom employees\n" +
							"\t\twhere job_id like 'AD_%';\n" +
							"\tloop\n" +
							"\t\tfetch cv into sal, sal_multiple;\n" +
							"\t\texit when cv%NOTFOUND;\n" +
							"\t\tDBMS_OUTPUT.PUT_LINE('sal          = ' || sal);\n" +
							"\t\tDBMS_OUTPUT.PUT_LINE('sal_multiple = ' || sal_multiple);\n" +
							"\tend loop;\n" +
							"\tclose cv;\n" +
							"end;", //
					output);
		}
	}
}
