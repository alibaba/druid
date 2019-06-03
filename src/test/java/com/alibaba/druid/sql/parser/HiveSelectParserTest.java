package com.alibaba.druid.sql.parser;

import java.util.List;

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.util.JdbcConstants;

import junit.framework.TestCase;

/**
 * Hivesql syntax parser test
 * 
 * @author garfield
 *
 */
public class HiveSelectParserTest extends TestCase {

	public void test_limit() throws Exception {
		String sql = "SELECT col_1, col_2 FROM table_1 LIMIT 5";
		
		String expected = "SELECT col_1, col_2\n" //
				+ "FROM table_1\n" //
				+ "LIMIT 5"; //
		List<SQLStatement> stmtList = SQLUtils.parseStatements(sql, JdbcConstants.HIVE);
		assertEquals(1, stmtList.size());
		for (SQLStatement stmt : stmtList) {
			assertEquals(expected, stmt.toString());
		}
	}

	public void test_sortby() throws Exception {
		String sql = "SELECT col_1, col_2 FROM table_1 SORT BY col_1 ASC, col_2 DESC";
		
		String expected = "SELECT col_1, col_2\n" //
				+ "FROM table_1\n" //
				+ "SORT BY col_1 ASC, col_2 DESC"; //
		List<SQLStatement> stmtList = SQLUtils.parseStatements(sql, JdbcConstants.HIVE);
		assertEquals(1, stmtList.size());
		for (SQLStatement stmt : stmtList) {
			assertEquals(expected, stmt.toString());
		}
	}

	public void test_distributeby() throws Exception {
		String sql = "SELECT col_1, col_2 FROM table_1 DISTRIBUTE BY col_1";
		
		String expected = "SELECT col_1, col_2\n" //
				+ "FROM table_1\n" //
				+ "DISTRIBUTE BY col_1"; //
		List<SQLStatement> stmtList = SQLUtils.parseStatements(sql, JdbcConstants.HIVE);
		assertEquals(1, stmtList.size());
		for (SQLStatement stmt : stmtList) {
			assertEquals(expected, stmt.toString());
		}
	}

	public void test_clusterby() throws Exception {
		String sql = "SELECT col_1, col_2 FROM table_1 CLUSTER BY col_1";
		
		String expected = "SELECT col_1, col_2\n" //
				+ "FROM table_1\n" //
				+ "CLUSTER BY col_1"; //
		List<SQLStatement> stmtList = SQLUtils.parseStatements(sql, JdbcConstants.HIVE);
		assertEquals(1, stmtList.size());
		for (SQLStatement stmt : stmtList) {
			assertEquals(expected, stmt.toString());
		}
	}

	public void test_complex() throws Exception {
		String sql = "SELECT col_1, col_2 FROM table_1 " //
				+ "DISTRIBUTE BY col_1 " //
				+ "SORT BY col_1 ASC, col_2 DESC " //
				+ "LIMIT 5"; //
		
		String expected = "SELECT col_1, col_2\n" //
				+ "FROM table_1\n" //
				+ "DISTRIBUTE BY col_1\n" //
				+ "SORT BY col_1 ASC, col_2 DESC\n" //
				+ "LIMIT 5"; //
		List<SQLStatement> stmtList = SQLUtils.parseStatements(sql, JdbcConstants.HIVE);
		assertEquals(1, stmtList.size());
		for (SQLStatement stmt : stmtList) {
			assertEquals(expected, stmt.toString());
		}
	}

	public void test_union_order_limit_1() throws Exception {
		String sql = "SELECT col_1 col FROM table_1 " //
				+ "union " //
				+ "SELECT col_2 col FROM table_2 " //
				+ "union " //
				+ "SELECT col_3 col FROM table_3 " //
				+ "ORDER BY col ASC " //
				+ "LIMIT 5"; //
		
		String expected = "SELECT col_1 AS col\n" //
				+ "FROM table_1\n" //
				+ "UNION\n" //
				+ "SELECT col_2 AS col\n" //
				+ "FROM table_2\n" //
				+ "UNION\n" //
				+ "SELECT col_3 AS col\n" //
				+ "FROM table_3\n" //
				+ "ORDER BY col ASC\n" //
				+ "LIMIT 5"; //
		List<SQLStatement> stmtList = SQLUtils.parseStatements(sql, JdbcConstants.HIVE);
		assertEquals(1, stmtList.size());
		for (SQLStatement stmt : stmtList) {
			assertEquals(expected, stmt.toString());
		}
	}

	public void test_union_order_limit_2() throws Exception {
		String sql = "select tmp.col col from (SELECT col_1 col FROM table_1 ORDER BY col LIMIT 5) tmp " //
				+ "union " //
				+ "SELECT col_2 col FROM table_2 " //
				+ "union " //
				+ "SELECT col_3 col FROM table_3 " //
				+ "ORDER BY col ASC LIMIT 5"; //
		
		String expected = "SELECT tmp.col AS col\n" //
				+ "FROM (\n" //
				+ "\tSELECT col_1 AS col\n" //
				+ "\tFROM table_1\n" //
				+ "\tORDER BY col\n" //
				+ "\tLIMIT 5\n" //
				+ ") tmp\n" //
				+ "UNION\n" //
				+ "SELECT col_2 AS col\n" //
				+ "FROM table_2\n" //
				+ "UNION\n" //
				+ "SELECT col_3 AS col\n" //
				+ "FROM table_3\n" //
				+ "ORDER BY col ASC\n" //
				+ "LIMIT 5"; //
		List<SQLStatement> stmtList = SQLUtils.parseStatements(sql, JdbcConstants.HIVE);
		assertEquals(1, stmtList.size());
		for (SQLStatement stmt : stmtList) {
			assertEquals(expected, stmt.toString());
		}
	}
}
