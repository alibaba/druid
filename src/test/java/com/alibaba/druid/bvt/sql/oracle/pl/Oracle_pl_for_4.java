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

public class Oracle_pl_for_4 extends OracleTest {

    public void test_0() throws Exception {
        String sql = "DECLARE\n" +
				"  v_employees employees%ROWTYPE;\n" +
				"  CURSOR c1 is SELECT * FROM employees;\n" +
				"BEGIN\n" +
				"  OPEN c1;\n" +
				"  \n" +
				"  -- Fetch entire row into v_employees record:\n" +
				"  <<outer_loop>>\n" +
				"  FOR i IN 1..10 LOOP\n" +
				"    -- Process data here\n" +
				"    FOR j IN 1..10 LOOP\n" +
				"      FETCH c1 INTO v_employees;\n" +
				"      CONTINUE outer_loop WHEN c1%NOTFOUND;\n" +
				"      -- Process data here\n" +
				"    END LOOP;\n" +
				"  END LOOP outer_loop;\n" +
				" \n" +
				"  CLOSE c1;\n" +
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

        assertEquals(1, visitor.getTables().size());

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
							"\tv_employees employees%ROWTYPE;\n" +
							"\tCURSOR c1 IS\n" +
							"\t\tSELECT *\n" +
							"\t\tFROM employees;\n" +
							"BEGIN\n" +
							"\tOPEN c1;\n" +
							"\t<<outer_loop>>\n" +
							"\tFOR i IN 1..10\n" +
							"\tLOOP\n" +
							"\t\tFOR j IN 1..10\n" +
							"\t\tLOOP\n" +
							"\t\t\tFETCH c1 INTO v_employees;\n" +
							"\t\t\tCONTINUE outer_loop WHEN c1%NOTFOUND;\n" +
							"\t\tEND LOOP;\n" +
							"\tEND LOOP outer_loop;\n" +
							"\tCLOSE c1;\n" +
							"END;", //
					output);
		}
		{
			String output = SQLUtils.toSQLString(statementList, JdbcConstants.ORACLE, SQLUtils.DEFAULT_LCASE_FORMAT_OPTION);
			assertEquals("declare\n" +
							"\tv_employees employees%ROWTYPE;\n" +
							"\tcursor c1 is\n" +
							"\t\tselect *\n" +
							"\t\tfrom employees;\n" +
							"begin\n" +
							"\topen c1;\n" +
							"\t<<outer_loop>>\n" +
							"\tfor i in 1..10\n" +
							"\tloop\n" +
							"\t\tfor j in 1..10\n" +
							"\t\tloop\n" +
							"\t\t\tfetch c1 into v_employees;\n" +
							"\t\t\tcontinue outer_loop when c1%NOTFOUND;\n" +
							"\t\tend loop;\n" +
							"\tend loop outer_loop;\n" +
							"\tclose c1;\n" +
							"end;", //
					output);
		}
	}
}
