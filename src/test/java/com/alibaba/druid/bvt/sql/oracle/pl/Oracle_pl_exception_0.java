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

public class Oracle_pl_exception_0 extends OracleTest {

    public void test_0() throws Exception {
        String sql = "CREATE OR REPLACE PROCEDURE select_item (\n" +
				"  t_column VARCHAR2,\n" +
				"  t_name   VARCHAR2\n" +
				") AUTHID DEFINER\n" +
				"IS\n" +
				"  temp VARCHAR2(30);\n" +
				"BEGIN\n" +
				"  temp := t_column;  -- For error message if next SELECT fails\n" +
				" \n" +
				"  -- Fails if table t_name does not have column t_column:\n" +
				" \n" +
				"  SELECT COLUMN_NAME INTO temp\n" +
				"  FROM USER_TAB_COLS \n" +
				"  WHERE TABLE_NAME = UPPER(t_name)\n" +
				"  AND COLUMN_NAME = UPPER(t_column);\n" +
				" \n" +
				"  temp := t_name;  -- For error message if next SELECT fails\n" +
				" \n" +
				"  -- Fails if there is no table named t_name:\n" +
				" \n" +
				"  SELECT OBJECT_NAME INTO temp\n" +
				"  FROM USER_OBJECTS\n" +
				"  WHERE OBJECT_NAME = UPPER(t_name)\n" +
				"  AND OBJECT_TYPE = 'TABLE';\n" +
				" \n" +
				"EXCEPTION\n" +
				"  WHEN NO_DATA_FOUND THEN\n" +
				"    DBMS_OUTPUT.PUT_LINE ('No Data found for SELECT on ' || temp);\n" +
				"  WHEN OTHERS THEN\n" +
				"    DBMS_OUTPUT.PUT_LINE ('Unexpected error');\n" +
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
			assertEquals("CREATE OR REPLACE PROCEDURE select_item (\n" +
							"\tt_column VARCHAR2, \n" +
							"\tt_name VARCHAR2\n" +
							") AUTHID DEFINER\n" +
							"AS\n" +
							"\ttemp VARCHAR2(30);\n" +
							"BEGIN\n" +
							"\ttemp := t_column;\n" +
							"\t-- For error message if next SELECT fails\n" +
							"\t-- Fails if table t_name does not have column t_column:\n" +
							"\tSELECT COLUMN_NAME\n" +
							"\tINTO temp\n" +
							"\tFROM USER_TAB_COLS\n" +
							"\tWHERE TABLE_NAME = UPPER(t_name)\n" +
							"\t\tAND COLUMN_NAME = UPPER(t_column);\n" +
							"\ttemp := t_name;\n" +
							"\t-- For error message if next SELECT fails\n" +
							"\t-- Fails if there is no table named t_name:\n" +
							"\tSELECT OBJECT_NAME\n" +
							"\tINTO temp\n" +
							"\tFROM USER_OBJECTS\n" +
							"\tWHERE OBJECT_NAME = UPPER(t_name)\n" +
							"\t\tAND OBJECT_TYPE = 'TABLE';\n" +
							"EXCEPTION\n" +
							"\tWHEN NO_DATA_FOUND THEN DBMS_OUTPUT.PUT_LINE('No Data found for SELECT on ' || temp);\n" +
							"\tWHEN OTHERS THEN\n" +
							"\t\tDBMS_OUTPUT.PUT_LINE('Unexpected error');\n" +
							"\t\tRAISE;\n" +
							"END;", //
					output);
		}
		{
			String output = SQLUtils.toSQLString(statementList, JdbcConstants.ORACLE, SQLUtils.DEFAULT_LCASE_FORMAT_OPTION);
			assertEquals("create or replace procedure select_item (\n" +
							"\tt_column VARCHAR2, \n" +
							"\tt_name VARCHAR2\n" +
							") authid DEFINER\n" +
							"as\n" +
							"\ttemp VARCHAR2(30);\n" +
							"begin\n" +
							"\ttemp := t_column;\n" +
							"\t-- For error message if next SELECT fails\n" +
							"\t-- Fails if table t_name does not have column t_column:\n" +
							"\tselect COLUMN_NAME\n" +
							"\tinto temp\n" +
							"\tfrom USER_TAB_COLS\n" +
							"\twhere TABLE_NAME = UPPER(t_name)\n" +
							"\t\tand COLUMN_NAME = UPPER(t_column);\n" +
							"\ttemp := t_name;\n" +
							"\t-- For error message if next SELECT fails\n" +
							"\t-- Fails if there is no table named t_name:\n" +
							"\tselect OBJECT_NAME\n" +
							"\tinto temp\n" +
							"\tfrom USER_OBJECTS\n" +
							"\twhere OBJECT_NAME = UPPER(t_name)\n" +
							"\t\tand OBJECT_TYPE = 'TABLE';\n" +
							"exception\n" +
							"\twhen NO_DATA_FOUND then DBMS_OUTPUT.PUT_LINE('No Data found for SELECT on ' || temp);\n" +
							"\twhen OTHERS then\n" +
							"\t\tDBMS_OUTPUT.PUT_LINE('Unexpected error');\n" +
							"\t\traise;\n" +
							"end;", //
					output);
		}
	}
}
