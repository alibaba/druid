package com.alibaba.druid.sql.parser;

import java.util.List;

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.util.JdbcConstants;

import junit.framework.TestCase;

/**
 * Hivesql syntax "SHOW" parser test
 * 
 * @author garfield
 *
 */
public class HiveShowParserTest extends TestCase {

	public void test_show_databases_0() throws Exception {
		String sql = "SHOW DATABASES LIKE 'test*'";
		List<SQLStatement> stmtList = SQLUtils.parseStatements(sql, JdbcConstants.HIVE);
		for (SQLStatement stmt : stmtList) {
			String result = stmt.toString();
			assertEquals("SHOW DATABASES LIKE 'test*'", result);
		}
	}
	
	public void test_show_databases_1() throws Exception {
		String sql = "SHOW DATABASES";
		List<SQLStatement> stmtList = SQLUtils.parseStatements(sql, JdbcConstants.HIVE);
		for (SQLStatement stmt : stmtList) {
			String result = stmt.toString();
			assertEquals("SHOW DATABASES", result);
		}
	}
	
	public void test_show_tables_0() throws Exception {
		String sql = "SHOW TABLES IN TEST_DB 'test*'";
		List<SQLStatement> stmtList = SQLUtils.parseStatements(sql, JdbcConstants.HIVE);
		for (SQLStatement stmt : stmtList) {
			String result = stmt.toString();
			assertEquals("SHOW TABLES IN TEST_DB 'test*'", result);
		}
	}
	
	public void test_show_tables_1() throws Exception {
		String sql = "SHOW TABLES";
		List<SQLStatement> stmtList = SQLUtils.parseStatements(sql, JdbcConstants.HIVE);
		for (SQLStatement stmt : stmtList) {
			String result = stmt.toString();
			assertEquals("SHOW TABLES", result);
		}
	}
	
	public void test_show_tables_2() throws Exception {
		String sql = "SHOW TABLES IN TEST_DB";
		List<SQLStatement> stmtList = SQLUtils.parseStatements(sql, JdbcConstants.HIVE);
		for (SQLStatement stmt : stmtList) {
			String result = stmt.toString();
			assertEquals("SHOW TABLES IN TEST_DB", result);
		}
	}
	
	public void test_show_tables_3() throws Exception {
		String sql = "SHOW TABLES 'test*'";
		List<SQLStatement> stmtList = SQLUtils.parseStatements(sql, JdbcConstants.HIVE);
		for (SQLStatement stmt : stmtList) {
			String result = stmt.toString();
			assertEquals("SHOW TABLES 'test*'", result);
		}
	}
}
