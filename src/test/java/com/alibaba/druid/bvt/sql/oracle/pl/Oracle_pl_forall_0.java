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

public class Oracle_pl_forall_0 extends OracleTest {

    public void test_0() throws Exception {
        String sql = "DROP TABLE employees_temp;\n" +
				"CREATE TABLE employees_temp AS SELECT * FROM employees;\n" +
				"\n" +
				"DECLARE\n" +
				"  TYPE NumList IS VARRAY(20) OF NUMBER;\n" +
				"  depts NumList := NumList(10, 30, 70);  -- department numbers\n" +
				"BEGIN\n" +
				"  FORALL i IN depts.FIRST..depts.LAST\n" +
				"    DELETE FROM employees_temp\n" +
				"    WHERE department_id = depts(i);\n" +
				"END;"; //

        List<SQLStatement> statementList = SQLUtils.parseStatements(sql, JdbcConstants.ORACLE);
		assertEquals(3, statementList.size());
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
			String output = SQLUtils.toSQLString(statementList, JdbcConstants.ORACLE);
			System.out.println(output);
			assertEquals("DROP TABLE employees_temp;\n" +
							"CREATE TABLE employees_temp\n" +
							"AS\n" +
							"SELECT *\n" +
							"FROM employees;\n" +
							"DECLARE\n" +
							"\tTYPE NumList IS VARRAY(20) OF NUMBER;\n" +
							"\tdepts NumList := NumList(10, 30, 70);\n" +
							"BEGIN\n" +
							"\tFORALL i IN depts.FIRST..depts.LAST\n" +
							"\t\tDELETE FROM employees_temp\n" +
							"\t\tWHERE department_id = depts(i);\n" +
							"END;", //
					output);
		}
		{
			String output = SQLUtils.toSQLString(statementList, JdbcConstants.ORACLE, SQLUtils.DEFAULT_LCASE_FORMAT_OPTION);
			assertEquals("drop table employees_temp;\n" +
							"create table employees_temp\n" +
							"as\n" +
							"select *\n" +
							"from employees;\n" +
							"declare\n" +
							"\ttype NumList is VARRAY(20) OF NUMBER;\n" +
							"\tdepts NumList := NumList(10, 30, 70);\n" +
							"begin\n" +
							"\tforall i in depts.FIRST..depts.LAST\n" +
							"\t\tdelete from employees_temp\n" +
							"\t\twhere department_id = depts(i);\n" +
							"end;", //
					output);
		}
	}
}
