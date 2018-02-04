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

public class Oracle_pl_3 extends OracleTest {

    public void test_0() throws Exception {
        String sql = "create or replace type type_body_elements\n" +
				"as object\n" +
				"(\n" +
				"  some_string varchar2(64),\n" +
				"  member function function_one\n" +
				"  return varchar2,\n" +
				"  member function function_two\n" +
				"  return varchar2\n" +
				");\n" +
				"/\n" +
				"\n" +
				"create or replace type body type_body_elements\n" +
				"is\n" +
				"\n" +
				"  member function function_one\n" +
				"  return varchar2\n" +
				"  is\n" +
				"  begin\n" +
				"    return 'the function_one result';\n" +
				"  end function_one;\n" +
				"\n" +
				"  member function function_two\n" +
				"  return varchar2\n" +
				"  is\n" +
				"  begin\n" +
				"    return 'the function_two result';\n" +
				"  end function_two;\n" +
				"\n" +
				"end;\n" +
				"/\n"; //

        List<SQLStatement> statementList = SQLUtils.parseStatements(sql, JdbcConstants.ORACLE);
		assertEquals(4, statementList.size());
		SQLStatement stmt = statementList.get(0);

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
			assertEquals("CREATE OR REPLACE TYPE type_body_elements AS OBJECT (\n" +
							"\tsome_string varchar2(64), \n" +
							"\tMEMBER FUNCTION function_one () RETURN varchar2, \n" +
							"\tMEMBER FUNCTION function_two () RETURN varchar2\n" +
							");", //
					output);
		}
		{
			String output = SQLUtils.toOracleString(stmt, SQLUtils.DEFAULT_LCASE_FORMAT_OPTION);
			assertEquals("create or replace type type_body_elements AS OBJECT (\n" +
							"\tsome_string varchar2(64), \n" +
							"\tmember function function_one () return varchar2, \n" +
							"\tmember function function_two () return varchar2\n" +
							");", //
					output);
		}
	}
}
