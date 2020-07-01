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

public class Oracle_pl_case_2 extends OracleTest {

    public void test_0() throws Exception {
        String sql = "CASE l_employee_type\n" +
				"   WHEN 'S' \n" +
				"   THEN\n" +
				"      award_bonus (l_employee_id);\n" +
				"   WHEN 'H' \n" +
				"   THEN\n" +
				"      award_bonus (l_employee_id);\n" +
				"   WHEN 'C' \n" +
				"   THEN\n" +
				"       award_commissioned_bonus (\n" +
				"          l_employee_id);\n" +
				"   ELSE\n" +
				"       RAISE invalid_employee_type;\n" +
				"END CASE;"; //

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
			assertEquals("CASE l_employee_type\n" +
							"\tWHEN 'S' THEN award_bonus(l_employee_id); \n" +
							"\tWHEN 'H' THEN award_bonus(l_employee_id); \n" +
							"\tWHEN 'C' THEN award_commissioned_bonus(l_employee_id);\n" +
							"\tELSE RAISE invalid_employee_type;\n" +
							"END CASE;", //
					output);
		}
		{
			String output = SQLUtils.toOracleString(stmt, SQLUtils.DEFAULT_LCASE_FORMAT_OPTION);
			assertEquals("case l_employee_type\n" +
							"\twhen 'S' then award_bonus(l_employee_id); \n" +
							"\twhen 'H' then award_bonus(l_employee_id); \n" +
							"\twhen 'C' then award_commissioned_bonus(l_employee_id);\n" +
							"\telse raise invalid_employee_type;\n" +
							"end case;", //
					output);
		}
	}
}
