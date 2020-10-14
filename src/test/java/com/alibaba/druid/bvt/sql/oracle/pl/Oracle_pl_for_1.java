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

public class Oracle_pl_for_1 extends OracleTest {

	public void test_0() throws Exception {
		String sql = "DECLARE\n" +
				"  TYPE empcurtyp IS REF CURSOR;\n" +
				"  TYPE namelist IS TABLE OF employees.last_name%TYPE;\n" +
				"  TYPE sallist IS TABLE OF employees.salary%TYPE;\n" +
				"  emp_cv  empcurtyp;\n" +
				"  names   namelist;\n" +
				"  sals    sallist;\n" +
				"BEGIN\n" +
				"  OPEN emp_cv FOR\n" +
				"    SELECT last_name, salary FROM employees\n" +
				"    WHERE job_id = 'SA_REP'\n" +
				"    ORDER BY salary DESC;\n" +
				"\n" +
				"  FETCH emp_cv BULK COLLECT INTO names, sals;\n" +
				"  CLOSE emp_cv;\n" +
				"  -- loop through the names and sals collections\n" +
				"  FOR i IN names.FIRST .. names.LAST\n" +
				"  LOOP\n" +
				"    DBMS_OUTPUT.PUT_LINE\n" +
				"      ('Name = ' || names(i) || ', salary = ' || sals(i));\n" +
				"  END LOOP;\n" +
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
			System.out.println(output);
			assertEquals("DECLARE\n" +
							"\tTYPE empcurtyp IS REF CURSOR;\n" +
							"\tTYPE employees.last_name IS TABLE OF employees.last_name%TYPE;\n" +
							"\tTYPE employees.salary IS TABLE OF employees.salary%TYPE;\n" +
							"\temp_cv empcurtyp;\n" +
							"\tnames namelist;\n" +
							"\tsals sallist;\n" +
							"BEGIN\n" +
							"\tOPEN emp_cv FOR \n" +
							"\t\tSELECT last_name, salary\n" +
							"\t\tFROM employees\n" +
							"\t\tWHERE job_id = 'SA_REP'\n" +
							"\t\tORDER BY salary DESC;\n" +
							"\tFETCH emp_cv BULK COLLECT INTO names, sals;\n" +
							"\tCLOSE emp_cv;\n" +
							"\tFOR i IN names.FIRST..names.LAST\n" +
							"\tLOOP\n" +
							"\t\tDBMS_OUTPUT.PUT_LINE('Name = ' || names(i) || ', salary = ' || sals(i));\n" +
							"\tEND LOOP;\n" +
							"END;", //
					output);
		}
		{
			String output = SQLUtils.toOracleString(stmt, SQLUtils.DEFAULT_LCASE_FORMAT_OPTION);
			assertEquals("declare\n" +
							"\ttype empcurtyp is REF CURSOR;\n" +
							"\ttype employees.last_name is TABLE OF employees.last_name%TYPE;\n" +
							"\ttype employees.salary is TABLE OF employees.salary%TYPE;\n" +
							"\temp_cv empcurtyp;\n" +
							"\tnames namelist;\n" +
							"\tsals sallist;\n" +
							"begin\n" +
							"\topen emp_cv for \n" +
							"\t\tselect last_name, salary\n" +
							"\t\tfrom employees\n" +
							"\t\twhere job_id = 'SA_REP'\n" +
							"\t\torder by salary desc;\n" +
							"\tfetch emp_cv bulk collect into names, sals;\n" +
							"\tclose emp_cv;\n" +
							"\tfor i in names.FIRST..names.LAST\n" +
							"\tloop\n" +
							"\t\tDBMS_OUTPUT.PUT_LINE('Name = ' || names(i) || ', salary = ' || sals(i));\n" +
							"\tend loop;\n" +
							"end;", //
					output);
		}
	}
}
