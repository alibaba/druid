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
import org.junit.Assert;

import java.util.List;

public class Oracle_pl_5 extends OracleTest {

    public void test_0() throws Exception {
        String sql = "create or replace procedure cursor_insert_user(inarea in number) is\n" +
				"-- 使用游标添加数据至 用户表 用户表结构件 insertUser.sql\n" +
				"cursor c_user is select * from TEST_USER T where T.active = 1 and T.area = inarea;\n" +
				"\n" +
				"begin\n" +
				"for data in c_user loop\n" +
				"insert into USER(id, name, username, passwd, age, sex, level, area, created_date, version)\n" +
				"values (SEQ_USER.NEXTVAL, data.name, data.username, data.passwd, data.age, data.sex, data.level, data.area, data.created_date, 1);\n" +
				"end loop;\n" +
				"end;"; //

        List<SQLStatement> statementList = SQLUtils.parseStatements(sql, JdbcConstants.ORACLE);
		assertEquals(1, statementList.size());
		SQLStatement stmt = statementList.get(0);

        SchemaStatVisitor visitor = SQLUtils.createSchemaStatVisitor(JdbcConstants.ORACLE);
        for (SQLStatement statement : statementList) {
            statement.accept(visitor);
        }

        System.out.println("Tables : " + visitor.getTables());
//        System.out.println("fields : " + visitor.getColumns());
//        System.out.println("coditions : " + visitor.getConditions());
//        System.out.println("relationships : " + visitor.getRelationships());
//        System.out.println("orderBy : " + visitor.getOrderByColumns());

        assertEquals(2, visitor.getTables().size());

        assertTrue(visitor.containsTable("TEST_USER"));
        assertTrue(visitor.containsTable("USER"));
//        Assert.assertTrue(visitor.getTables().containsKey(new TableStat.Name("emp_name")));

//        Assert.assertEquals(7, visitor.getColumns().size());
//        Assert.assertEquals(3, visitor.getConditions().size());
//        Assert.assertEquals(1, visitor.getRelationships().size());

        // Assert.assertTrue(visitor.getColumns().contains(new TableStat.Column("employees", "salary")));

		{
			String output = SQLUtils.toOracleString(stmt);
			assertEquals("CREATE OR REPLACE PROCEDURE cursor_insert_user (\n" +
							"\tinarea IN number\n" +
							")\n" +
							"AS\n" +
							"\tCURSOR c_user IS\n" +
							"\t\tSELECT *\n" +
							"\t\tFROM TEST_USER T\n" +
							"\t\tWHERE T.active = 1\n" +
							"\t\t\tAND T.area = inarea;\n" +
							"BEGIN\n" +
							"\tFOR data IN c_user\n" +
							"\tLOOP\n" +
							"\t\tINSERT INTO USER\n" +
							"\t\t\t(id, name, username, passwd, age\n" +
							"\t\t\t, sex, level, area, created_date, version)\n" +
							"\t\tVALUES (SEQ_USER.NEXTVAL, data.name, data.username, data.passwd, data.age\n" +
							"\t\t\t, data.sex, data.level, data.area, data.created_date, 1);\n" +
							"\tEND LOOP;\n" +
							"END;", //
					output);
		}
		{
			String output = SQLUtils.toOracleString(stmt, SQLUtils.DEFAULT_LCASE_FORMAT_OPTION);
			assertEquals("create or replace procedure cursor_insert_user (\n" +
							"\tinarea in number\n" +
							")\n" +
							"as\n" +
							"\tcursor c_user is\n" +
							"\t\tselect *\n" +
							"\t\tfrom TEST_USER T\n" +
							"\t\twhere T.active = 1\n" +
							"\t\t\tand T.area = inarea;\n" +
							"begin\n" +
							"\tfor data in c_user\n" +
							"\tloop\n" +
							"\t\tinsert into USER\n" +
							"\t\t\t(id, name, username, passwd, age\n" +
							"\t\t\t, sex, level, area, created_date, version)\n" +
							"\t\tvalues (SEQ_USER.nextval, data.name, data.username, data.passwd, data.age\n" +
							"\t\t\t, data.sex, data.level, data.area, data.created_date, 1);\n" +
							"\tend loop;\n" +
							"end;", //
					output);
		}
	}
}
