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

public class Oracle_pl_exception_4 extends OracleTest {

    public void test_0() throws Exception {
        String sql = "CREATE OR REPLACE PROCEDURE p AUTHID DEFINER AS\n" +
				"BEGIN\n" +
				"\n" +
				"  DECLARE\n" +
				"    past_due     EXCEPTION;\n" +
				"    due_date     DATE := trunc(SYSDATE) - 1;\n" +
				"    todays_date  DATE := trunc(SYSDATE);\n" +
				"  BEGIN\n" +
				"    IF due_date < todays_date THEN\n" +
				"      RAISE past_due;\n" +
				"    END IF;\n" +
				"  END;\n" +
				"\n" +
				"EXCEPTION\n" +
				"  WHEN OTHERS THEN\n" +
				"    ROLLBACK;\n" +
				"    RAISE;\n" +
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
			assertEquals("CREATE OR REPLACE PROCEDURE p AUTHID DEFINER\n" +
							"AS\n" +
							"BEGIN\n" +
							"\tDECLARE\n" +
							"\t\tpast_due EXCEPTION;\n" +
							"\t\tdue_date DATE := trunc(SYSDATE) - 1;\n" +
							"\t\ttodays_date DATE := trunc(SYSDATE);\n" +
							"\tBEGIN\n" +
							"\t\tIF due_date < todays_date THEN\n" +
							"\t\t\tRAISE past_due;\n" +
							"\t\tEND IF;\n" +
							"\tEND;\n" +
							"EXCEPTION\n" +
							"\tWHEN OTHERS THEN\n" +
							"\t\tROLLBACK;\n" +
							"\t\tRAISE;\n" +
							"END;", //
					output);
		}
		{
			String output = SQLUtils.toSQLString(statementList, JdbcConstants.ORACLE, SQLUtils.DEFAULT_LCASE_FORMAT_OPTION);
			assertEquals("create or replace procedure p authid DEFINER\n" +
							"as\n" +
							"begin\n" +
							"\tdeclare\n" +
							"\t\tpast_due EXCEPTION;\n" +
							"\t\tdue_date DATE := trunc(sysdate) - 1;\n" +
							"\t\ttodays_date DATE := trunc(sysdate);\n" +
							"\tbegin\n" +
							"\t\tif due_date < todays_date then\n" +
							"\t\t\traise past_due;\n" +
							"\t\tend if;\n" +
							"\tend;\n" +
							"exception\n" +
							"\twhen OTHERS then\n" +
							"\t\trollback;\n" +
							"\t\traise;\n" +
							"end;", //
					output);
		}
	}
}
