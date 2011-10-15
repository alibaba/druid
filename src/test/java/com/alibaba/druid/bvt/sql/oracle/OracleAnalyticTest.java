package com.alibaba.druid.bvt.sql.oracle;

import com.alibaba.druid.sql.ast.statement.SQLSelectStatement;
import com.alibaba.druid.sql.dialect.oracle.parser.OracleStatementParser;
import com.alibaba.druid.sql.test.TestUtils;

import junit.framework.Assert;
import junit.framework.TestCase;

public class OracleAnalyticTest extends TestCase {

	public void test_isEmpty() throws Exception {
		String sql = "SELECT last_name, salary, STDDEV(salary) OVER (ORDER BY hire_date) \"StdDev\" " +
				"FROM employees " +
				"WHERE department_id = 30;";

		String expect = "SELECT last_name, salary, STDDEV(salary) OVER (ORDER BY hire_date) AS \"StdDev\"\n" +
                "FROM employees\n" +
                "WHERE department_id = 30;\n";
		OracleStatementParser parser = new OracleStatementParser(sql);
		SQLSelectStatement stmt = (SQLSelectStatement) parser
				.parseStatementList().get(0);

		String text = TestUtils.outputOracle(stmt);

		Assert.assertEquals(expect, text);

		System.out.println(text);
	}
}
