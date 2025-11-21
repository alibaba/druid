package com.alibaba.druid.bvt.sql.oracle.pl;

import java.util.List;

import com.alibaba.druid.sql.OracleTest;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.util.JdbcConstants;

public class Oracle_pl_select_bulk_collect  extends OracleTest {

	public void test_0() throws Exception {
		String sql = "DECLARE\n" +
				"\tTYPE NumList IS TABLE OF NUMBER;\n" +
				"\tTYPE NameList IS TABLE OF VARCHAR2(20);\n" +
				"\temp_id NumList;\n" +
				"\temp_name NameList;\n" +
				"BEGIN\n" +
				"\tSELECT employee_id, last_name\n" +
				"\tBULK COLLECT INTO (emp_id, emp_name)\n" +
				"\tFROM employees\n" +
				"\tWHERE department_id = 90;\n" +
				"END;";

		List<SQLStatement> statementList = SQLUtils.parseStatements(sql, JdbcConstants.ORACLE);
		SQLStatement stmt = statementList.get(0);

		assertEquals(1, statementList.size());

		String output = SQLUtils.toOracleString(stmt);
		assertEquals("DECLARE\n" +
				"\tTYPE NumList IS TABLE OF NUMBER;\n" +
				"\tTYPE NameList IS TABLE OF VARCHAR2(20);\n" +
				"\temp_id NumList;\n" +
				"\temp_name NameList;\n" +
				"BEGIN\n" +
				"\tSELECT employee_id, last_name\n" +
				"\tBULK COLLECT INTO (emp_id, emp_name)\n" +
				"\tFROM employees\n" +
				"\tWHERE department_id = 90;\n" +
				"END;", output);
	}
}
