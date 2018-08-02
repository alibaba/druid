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

public class Oracle_pl_exception_8 extends OracleTest {

    public void test_0() throws Exception {
        String sql = "DROP TABLE results;\n" +
				"CREATE TABLE results (\n" +
				"  res_name   VARCHAR(20),\n" +
				"  res_answer VARCHAR2(3)\n" +
				");\n" +
				" \n" +
				"CREATE UNIQUE INDEX res_name_ix ON results (res_name);\n" +
				"INSERT INTO results (res_name, res_answer) VALUES ('SMYTHE', 'YES');\n" +
				"INSERT INTO results (res_name, res_answer) VALUES ('JONES', 'NO');\n" +
				" \n" +
				"DECLARE\n" +
				"  name    VARCHAR2(20) := 'SMYTHE';\n" +
				"  answer  VARCHAR2(3) := 'NO';\n" +
				"  suffix  NUMBER := 1;\n" +
				"BEGIN\n" +
				"  FOR i IN 1..5 LOOP  -- Try transaction at most 5 times.\n" +
				" \n" +
				"    DBMS_OUTPUT.PUT('Try #' || i);\n" +
				" \n" +
				"    BEGIN  -- sub-block begins\n" +
				" \n" +
				"       SAVEPOINT start_transaction;\n" +
				" \n" +
				"       -- transaction begins\n" +
				" \n" +
				"       DELETE FROM results WHERE res_answer = 'NO';\n" +
				" \n" +
				"       INSERT INTO results (res_name, res_answer) VALUES (name, answer);\n" +
				" \n" +
				"       -- Nonunique name raises DUP_VAL_ON_INDEX.\n" +
				" \n" +
				"       -- If transaction succeeded:\n" +
				" \n" +
				"       COMMIT;\n" +
				"       DBMS_OUTPUT.PUT_LINE(' succeeded.');\n" +
				"       EXIT;\n" +
				" \n" +
				"    EXCEPTION\n" +
				"      WHEN DUP_VAL_ON_INDEX THEN\n" +
				"        DBMS_OUTPUT.PUT_LINE(' failed; trying again.');\n" +
				"        ROLLBACK TO start_transaction;    -- Undo changes.\n" +
				"        suffix := suffix + 1;             -- Try to fix problem.\n" +
				"        name := name || TO_CHAR(suffix);\n" +
				"    END;  -- sub-block ends\n" +
				" \n" +
				"  END LOOP;\n" +
				"END;"; //

        List<SQLStatement> statementList = SQLUtils.parseStatements(sql, JdbcConstants.ORACLE);
		assertEquals(6, statementList.size());

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
			assertEquals("DROP TABLE results;\n" +
							"CREATE TABLE results (\n" +
							"\tres_name VARCHAR(20),\n" +
							"\tres_answer VARCHAR2(3)\n" +
							");\n" +
							"CREATE UNIQUE INDEX res_name_ix ON results(res_name);\n" +
							"INSERT INTO results (res_name, res_answer)\n" +
							"VALUES ('SMYTHE', 'YES');\n" +
							"INSERT INTO results (res_name, res_answer)\n" +
							"VALUES ('JONES', 'NO');\n" +
							"DECLARE\n" +
							"\tname VARCHAR2(20) := 'SMYTHE';\n" +
							"\tanswer VARCHAR2(3) := 'NO';\n" +
							"\tsuffix NUMBER := 1;\n" +
							"BEGIN\n" +
							"\tFOR i IN 1..5\n" +
							"\tLOOP\n" +
							"\t\tDBMS_OUTPUT.PUT('Try #' || i);\n" +
							"\t\tBEGIN\n" +
							"\t\t\tSAVEPOINT TO start_transaction;\n" +
							"\t\t\tDELETE FROM results\n" +
							"\t\t\tWHERE res_answer = 'NO';\n" +
							"\t\t\tINSERT INTO results (res_name, res_answer)\n" +
							"\t\t\tVALUES (name, answer);\n" +
							"\t\t\tCOMMIT;\n" +
							"\t\t\tDBMS_OUTPUT.PUT_LINE(' succeeded.');\n" +
							"\t\t\tEXIT;\n" +
							"\t\tEXCEPTION\n" +
							"\t\t\tWHEN DUP_VAL_ON_INDEX THEN\n" +
							"\t\t\t\tDBMS_OUTPUT.PUT_LINE(' failed; trying again.');\n" +
							"\t\t\t\tROLLBACK TO start_transaction;\n" +
							"\t\t\t\tsuffix := suffix + 1;\n" +
							"\t\t\t\tname := name || TO_CHAR(suffix);\n" +
							"\t\tEND;\n" +
							"\tEND LOOP;\n" +
							"END;", //
					output);
		}
		{
			String output = SQLUtils.toSQLString(statementList, JdbcConstants.ORACLE, SQLUtils.DEFAULT_LCASE_FORMAT_OPTION);
			assertEquals("drop table results;\n" +
							"create table results (\n" +
							"\tres_name VARCHAR(20),\n" +
							"\tres_answer VARCHAR2(3)\n" +
							");\n" +
							"create UNIQUE index res_name_ix on results(res_name);\n" +
							"insert into results (res_name, res_answer)\n" +
							"values ('SMYTHE', 'YES');\n" +
							"insert into results (res_name, res_answer)\n" +
							"values ('JONES', 'NO');\n" +
							"declare\n" +
							"\tname VARCHAR2(20) := 'SMYTHE';\n" +
							"\tanswer VARCHAR2(3) := 'NO';\n" +
							"\tsuffix NUMBER := 1;\n" +
							"begin\n" +
							"\tfor i in 1..5\n" +
							"\tloop\n" +
							"\t\tDBMS_OUTPUT.PUT('Try #' || i);\n" +
							"\t\tbegin\n" +
							"\t\t\tsavepoint to start_transaction;\n" +
							"\t\t\tdelete from results\n" +
							"\t\t\twhere res_answer = 'NO';\n" +
							"\t\t\tinsert into results (res_name, res_answer)\n" +
							"\t\t\tvalues (name, answer);\n" +
							"\t\t\tcommit;\n" +
							"\t\t\tDBMS_OUTPUT.PUT_LINE(' succeeded.');\n" +
							"\t\t\texit;\n" +
							"\t\texception\n" +
							"\t\t\twhen DUP_VAL_ON_INDEX then\n" +
							"\t\t\t\tDBMS_OUTPUT.PUT_LINE(' failed; trying again.');\n" +
							"\t\t\t\trollback to start_transaction;\n" +
							"\t\t\t\tsuffix := suffix + 1;\n" +
							"\t\t\t\tname := name || TO_CHAR(suffix);\n" +
							"\t\tend;\n" +
							"\tend loop;\n" +
							"end;", //
					output);
		}
	}
}
