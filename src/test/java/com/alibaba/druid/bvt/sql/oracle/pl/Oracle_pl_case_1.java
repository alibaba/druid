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

public class Oracle_pl_case_1 extends OracleTest {

    public void test_0() throws Exception {
        String sql = "DECLARE\n" +
				"  grade CHAR(1);\n" +
				"BEGIN\n" +
				"  grade := 'B';\n" +
				"  \n" +
				"  CASE\n" +
				"    WHEN grade = 'A' THEN DBMS_OUTPUT.PUT_LINE('Excellent');\n" +
				"    WHEN grade = 'B' THEN DBMS_OUTPUT.PUT_LINE('Very Good');\n" +
				"    WHEN grade = 'C' THEN DBMS_OUTPUT.PUT_LINE('Good');\n" +
				"    WHEN grade = 'D' THEN DBMS_OUTPUT.PUT_LINE('Fair');\n" +
				"    WHEN grade = 'F' THEN DBMS_OUTPUT.PUT_LINE('Poor');\n" +
				"  END CASE;\n" +
				"EXCEPTION\n" +
				"  WHEN CASE_NOT_FOUND THEN\n" +
				"    DBMS_OUTPUT.PUT_LINE('No such grade');\n" +
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
							"\tgrade CHAR(1);\n" +
							"BEGIN\n" +
							"\tgrade := 'B';\n" +
							"\tCASE\n" +
							"\t\tWHEN grade = 'A' THEN DBMS_OUTPUT.PUT_LINE('Excellent'); \n" +
							"\t\tWHEN grade = 'B' THEN DBMS_OUTPUT.PUT_LINE('Very Good'); \n" +
							"\t\tWHEN grade = 'C' THEN DBMS_OUTPUT.PUT_LINE('Good'); \n" +
							"\t\tWHEN grade = 'D' THEN DBMS_OUTPUT.PUT_LINE('Fair'); \n" +
							"\t\tWHEN grade = 'F' THEN DBMS_OUTPUT.PUT_LINE('Poor');\n" +
							"\tEND CASE;\n" +
							"EXCEPTION\n" +
							"\tWHEN CASE_NOT_FOUND THEN DBMS_OUTPUT.PUT_LINE('No such grade');\n" +
							"END;", //
					output);
		}
		{
			String output = SQLUtils.toOracleString(stmt, SQLUtils.DEFAULT_LCASE_FORMAT_OPTION);
			assertEquals("declare\n" +
							"\tgrade CHAR(1);\n" +
							"begin\n" +
							"\tgrade := 'B';\n" +
							"\tcase\n" +
							"\t\twhen grade = 'A' then DBMS_OUTPUT.PUT_LINE('Excellent'); \n" +
							"\t\twhen grade = 'B' then DBMS_OUTPUT.PUT_LINE('Very Good'); \n" +
							"\t\twhen grade = 'C' then DBMS_OUTPUT.PUT_LINE('Good'); \n" +
							"\t\twhen grade = 'D' then DBMS_OUTPUT.PUT_LINE('Fair'); \n" +
							"\t\twhen grade = 'F' then DBMS_OUTPUT.PUT_LINE('Poor');\n" +
							"\tend case;\n" +
							"exception\n" +
							"\twhen CASE_NOT_FOUND then DBMS_OUTPUT.PUT_LINE('No such grade');\n" +
							"end;", //
					output);
		}
	}
}
