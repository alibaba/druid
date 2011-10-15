package com.alibaba.druid.bvt.sql.oracle;

import com.alibaba.druid.sql.ast.statement.SQLSelectStatement;
import com.alibaba.druid.sql.dialect.oracle.parser.OracleStatementParser;
import com.alibaba.druid.sql.test.TestUtils;

import junit.framework.Assert;
import junit.framework.TestCase;

public class OracleFlashbackQueryTest extends TestCase {

	public void test_isEmpty() throws Exception {
		String sql = "SELECT salary FROM employees AS OF TIMESTAMP (SYSTIMESTAMP - INTERVAL '1' DAY) WHERE last_name = 'Chung';";

		String expect = "SELECT salary\n" + "FROM employees\n"
				+ "AS OF TIMESTAMP (SYSTIMESTAMP - INTERVAL '1' DAY)\n"
				+ "WHERE last_name = 'Chung';\n";

		OracleStatementParser parser = new OracleStatementParser(sql);
		SQLSelectStatement stmt = (SQLSelectStatement) parser
				.parseStatementList().get(0);

		String text = TestUtils.outputOracle(stmt);

		Assert.assertEquals(expect, text);

		System.out.println(text);
	}
}
