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

public class Oracle_pl_exception_1 extends OracleTest {

    public void test_0() throws Exception {
        String sql = "CREATE OR REPLACE PROCEDURE loc_var AUTHID DEFINER IS\n" +
				"  stmt_no  POSITIVE;\n" +
				"  name_    VARCHAR2(100);\n" +
				"BEGIN\n" +
				"  stmt_no := 1;\n" +
				"\n" +
				"  SELECT table_name INTO name_\n" +
				"  FROM user_tables\n" +
				"  WHERE table_name LIKE 'ABC%';\n" +
				"\n" +
				"  stmt_no := 2;\n" +
				"\n" +
				"  SELECT table_name INTO name_\n" +
				"  FROM user_tables\n" +
				"  WHERE table_name LIKE 'XYZ%';\n" +
				"EXCEPTION\n" +
				"  WHEN NO_DATA_FOUND THEN\n" +
				"    DBMS_OUTPUT.PUT_LINE ('Table name not found in query ' || stmt_no);\n" +
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
			assertEquals("CREATE OR REPLACE PROCEDURE loc_var AUTHID DEFINER\n" +
							"AS\n" +
							"\tstmt_no POSITIVE;\n" +
							"\tname_ VARCHAR2(100);\n" +
							"BEGIN\n" +
							"\tstmt_no := 1;\n" +
							"\tSELECT table_name\n" +
							"\tINTO name_\n" +
							"\tFROM user_tables\n" +
							"\tWHERE table_name LIKE 'ABC%';\n" +
							"\tstmt_no := 2;\n" +
							"\tSELECT table_name\n" +
							"\tINTO name_\n" +
							"\tFROM user_tables\n" +
							"\tWHERE table_name LIKE 'XYZ%';\n" +
							"EXCEPTION\n" +
							"\tWHEN NO_DATA_FOUND THEN DBMS_OUTPUT.PUT_LINE('Table name not found in query ' || stmt_no);\n" +
							"END;", //
					output);
		}
		{
			String output = SQLUtils.toSQLString(statementList, JdbcConstants.ORACLE, SQLUtils.DEFAULT_LCASE_FORMAT_OPTION);
			assertEquals("create or replace procedure loc_var authid DEFINER\n" +
							"as\n" +
							"\tstmt_no POSITIVE;\n" +
							"\tname_ VARCHAR2(100);\n" +
							"begin\n" +
							"\tstmt_no := 1;\n" +
							"\tselect table_name\n" +
							"\tinto name_\n" +
							"\tfrom user_tables\n" +
							"\twhere table_name like 'ABC%';\n" +
							"\tstmt_no := 2;\n" +
							"\tselect table_name\n" +
							"\tinto name_\n" +
							"\tfrom user_tables\n" +
							"\twhere table_name like 'XYZ%';\n" +
							"exception\n" +
							"\twhen NO_DATA_FOUND then DBMS_OUTPUT.PUT_LINE('Table name not found in query ' || stmt_no);\n" +
							"end;", //
					output);
		}
	}
}
