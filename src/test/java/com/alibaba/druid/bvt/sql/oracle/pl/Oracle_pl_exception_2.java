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

public class Oracle_pl_exception_2 extends OracleTest {

    public void test_0() throws Exception {
        String sql = "DECLARE\n" +
				"  stock_price   NUMBER := 9.73;\n" +
				"  net_earnings  NUMBER := 0;\n" +
				"  pe_ratio      NUMBER;\n" +
				"BEGIN\n" +
				"  pe_ratio := stock_price / net_earnings;  -- raises ZERO_DIVIDE exception\n" +
				"  DBMS_OUTPUT.PUT_LINE('Price/earnings ratio = ' || pe_ratio);\n" +
				"EXCEPTION\n" +
				"  WHEN ZERO_DIVIDE THEN\n" +
				"    DBMS_OUTPUT.PUT_LINE('Company had zero earnings.');\n" +
				"    pe_ratio := NULL;\n" +
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
							"\tstock_price NUMBER := 9.73;\n" +
							"\tnet_earnings NUMBER := 0;\n" +
							"\tpe_ratio NUMBER;\n" +
							"BEGIN\n" +
							"\tpe_ratio := stock_price / net_earnings;\n" +
							"\tDBMS_OUTPUT.PUT_LINE('Price/earnings ratio = ' || pe_ratio);\n" +
							"EXCEPTION\n" +
							"\tWHEN ZERO_DIVIDE THEN\n" +
							"\t\tDBMS_OUTPUT.PUT_LINE('Company had zero earnings.');\n" +
							"\t\tpe_ratio := NULL;\n" +
							"END;", //
					output);
		}
		{
			String output = SQLUtils.toSQLString(statementList, JdbcConstants.ORACLE, SQLUtils.DEFAULT_LCASE_FORMAT_OPTION);
			assertEquals("declare\n" +
							"\tstock_price NUMBER := 9.73;\n" +
							"\tnet_earnings NUMBER := 0;\n" +
							"\tpe_ratio NUMBER;\n" +
							"begin\n" +
							"\tpe_ratio := stock_price / net_earnings;\n" +
							"\tDBMS_OUTPUT.PUT_LINE('Price/earnings ratio = ' || pe_ratio);\n" +
							"exception\n" +
							"\twhen ZERO_DIVIDE then\n" +
							"\t\tDBMS_OUTPUT.PUT_LINE('Company had zero earnings.');\n" +
							"\t\tpe_ratio := null;\n" +
							"end;", //
					output);
		}
	}
}
