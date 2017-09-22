/*
 * Copyright 1999-2017 Alibaba Group Holding Ltd.
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
        String sql = "FUNCTION F_PINYIN(P_NAME IN VARCHAR2) RETURN VARCHAR2 AS\n" +
				"     V_COMPARE VARCHAR2(100);\n" +
				"     V_RETURN VARCHAR2(4000);\n" +
				"     FUNCTION F_NLSSORT(P_WORD IN VARCHAR2) RETURN VARCHAR2 AS\n" +
				"     BEGIN\n" +
				"      RETURN NLSSORT(P_WORD, 'NLS_SORT=SCHINESE_PINYIN_M');\n" +
				"     END;\n" +
				"    BEGIN\n" +
				"    FOR I IN 1..NVL(LENGTH(P_NAME), 0) LOOP\n" +
				"     V_COMPARE := F_NLSSORT(SUBSTR(P_NAME, I, 1));\n" +
				"     IF V_COMPARE >= F_NLSSORT('?') AND V_COMPARE <= F_NLSSORT('?') THEN\n" +
				"      V_RETURN := V_RETURN || 'A';\n" +
				"     ELSIF V_COMPARE >= F_NLSSORT('?') AND V_COMPARE <= F_NLSSORT('?') THEN\n" +
				"      V_RETURN := V_RETURN || 'B';\n" +
				"     ELSIF V_COMPARE >= F_NLSSORT('?') AND V_COMPARE <= F_NLSSORT('?') THEN\n" +
				"      V_RETURN := V_RETURN || 'C';\n" +
				"     ELSIF V_COMPARE >= F_NLSSORT('?') AND V_COMPARE <= F_NLSSORT('?') THEN\n" +
				"      V_RETURN := V_RETURN || 'D';\n" +
				"     ELSIF V_COMPARE >= F_NLSSORT('?') AND V_COMPARE <= F_NLSSORT('?') THEN\n" +
				"      V_RETURN := V_RETURN || 'E';\n" +
				"     ELSIF V_COMPARE >= F_NLSSORT('?') AND V_COMPARE <= F_NLSSORT('?') THEN\n" +
				"      V_RETURN := V_RETURN || 'F';\n" +
				"     ELSIF V_COMPARE >= F_NLSSORT('?') AND V_COMPARE <= F_NLSSORT('?') THEN\n" +
				"      V_RETURN := V_RETURN || 'G';\n" +
				"     ELSIF V_COMPARE >= F_NLSSORT('?') AND V_COMPARE <= F_NLSSORT('?') THEN\n" +
				"      V_RETURN := V_RETURN || 'H';\n" +
				"     ELSIF V_COMPARE >= F_NLSSORT('?') AND V_COMPARE <= F_NLSSORT('?') THEN\n" +
				"      V_RETURN := V_RETURN || 'J';\n" +
				"     ELSIF V_COMPARE >= F_NLSSORT('?') AND V_COMPARE <= F_NLSSORT('?') THEN\n" +
				"      V_RETURN := V_RETURN || 'K';\n" +
				"     ELSIF V_COMPARE >= F_NLSSORT('?') AND V_COMPARE <= F_NLSSORT('?') THEN\n" +
				"      V_RETURN := V_RETURN || 'L';\n" +
				"     ELSIF V_COMPARE >= F_NLSSORT('?') AND V_COMPARE <= F_NLSSORT('?') THEN\n" +
				"      V_RETURN := V_RETURN || 'M';\n" +
				"     ELSIF V_COMPARE >= F_NLSSORT('?') AND V_COMPARE <= F_NLSSORT('?') THEN\n" +
				"      V_RETURN := V_RETURN || 'N';\n" +
				"     ELSIF V_COMPARE >= F_NLSSORT('?') AND V_COMPARE <= F_NLSSORT('?') THEN\n" +
				"      V_RETURN := V_RETURN || 'O';\n" +
				"     ELSIF V_COMPARE >= F_NLSSORT('?') AND V_COMPARE <= F_NLSSORT('?') THEN\n" +
				"      V_RETURN := V_RETURN || 'P';\n" +
				"     ELSIF V_COMPARE >= F_NLSSORT('?') AND V_COMPARE <= F_NLSSORT('?') THEN\n" +
				"      V_RETURN := V_RETURN || 'Q';\n" +
				"     ELSIF V_COMPARE >= F_NLSSORT('?') AND V_COMPARE <= F_NLSSORT('?') THEN\n" +
				"      V_RETURN := V_RETURN || 'R';\n" +
				"     ELSIF V_COMPARE >= F_NLSSORT('?') AND V_COMPARE <= F_NLSSORT('?') THEN\n" +
				"      V_RETURN := V_RETURN || 'S';\n" +
				"     ELSIF V_COMPARE >= F_NLSSORT('?') AND V_COMPARE <= F_NLSSORT('?') THEN\n" +
				"      V_RETURN := V_RETURN || 'T';\n" +
				"     ELSIF V_COMPARE >= F_NLSSORT('?') AND V_COMPARE <= F_NLSSORT('?') THEN\n" +
				"      V_RETURN := V_RETURN || 'W';\n" +
				"     ELSIF V_COMPARE >= F_NLSSORT('?') AND V_COMPARE <= F_NLSSORT('?') THEN\n" +
				"      V_RETURN := V_RETURN || 'X';\n" +
				"     ELSIF V_COMPARE >= F_NLSSORT('?') AND V_COMPARE <= F_NLSSORT('?') THEN\n" +
				"      V_RETURN := V_RETURN || 'Y';\n" +
				"     ELSIF V_COMPARE >= F_NLSSORT('?') AND V_COMPARE <= F_NLSSORT('?') THEN\n" +
				"      V_RETURN := V_RETURN || 'Z';\n" +
				"     END IF;\n" +
				"    END LOOP;\n" +
				"    RETURN V_RETURN;\n"; //

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
			assertEquals("CREATE OR REPLACE PROCEDURE reassign (\n" +
							"\tp IN OUT NOCOPY hr.person_typ, \n" +
							"\tnew_job VARCHAR2\n" +
							")\n" +
							"BEGIN\n" +
							"\tp.change_job(new_job);\n" +
							"END;", //
					output);
		}
		{
			String output = SQLUtils.toOracleString(stmt, SQLUtils.DEFAULT_LCASE_FORMAT_OPTION);
			assertEquals("create or replace procedure reassign (\n" +
							"\tp in out nocopy hr.person_typ, \n" +
							"\tnew_job VARCHAR2\n" +
							")\n" +
							"begin\n" +
							"\tp.change_job(new_job);\n" +
							"end;", //
					output);
		}
	}
}
