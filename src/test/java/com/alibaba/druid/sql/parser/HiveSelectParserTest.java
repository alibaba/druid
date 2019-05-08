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
		List<SQLStatement> stmtList = SQLUtils.parseStatements(sql, JdbcConstants.HIVE);
		for (SQLStatement stmt : stmtList) {
			String result = stmt.toString();
			if (result == null || result.toUpperCase().indexOf("LIMIT") < 0) {
				throw new Exception("'LIMIT' not found in the result");
			}
		}
	}

	public void test_sortby() throws Exception {
		String sql = "SELECT col_1, col_2 FROM table_1 SORT BY col_1 ASC, col_2 DESC";
		List<SQLStatement> stmtList = SQLUtils.parseStatements(sql, JdbcConstants.HIVE);
		for (SQLStatement stmt : stmtList) {
			String result = stmt.toString();
			if (result == null || result.toUpperCase().indexOf("SORT BY") < 0) {
				throw new Exception("'SORT BY' not found in the result");
			}
		}
	}
	
	public void test_distributeby() throws Exception {
		String sql = "SELECT col_1, col_2 FROM table_1 DISTRIBUTE BY col_1";
		List<SQLStatement> stmtList = SQLUtils.parseStatements(sql, JdbcConstants.HIVE);
		for (SQLStatement stmt : stmtList) {
			String result = stmt.toString();
			if (result == null || result.toUpperCase().indexOf("DISTRIBUTE BY") < 0) {
				throw new Exception("'DISTRIBUTE BY' not found in the result");
			}
		}
	}
	
	public void test_clusterby() throws Exception {
		String sql = "SELECT col_1, col_2 FROM table_1 CLUSTER BY col_1";
		List<SQLStatement> stmtList = SQLUtils.parseStatements(sql, JdbcConstants.HIVE);
		for (SQLStatement stmt : stmtList) {
			String result = stmt.toString();
			if (result == null || result.toUpperCase().indexOf("CLUSTER BY") < 0) {
				throw new Exception("'CLUSTER BY' not found in the result");
			}
		}
	}
	
	public void test_complex() throws Exception {
		String sql = "SELECT col_1, col_2 FROM table_1 DISTRIBUTE BY col_1 SORT BY col_1 ASC, col_2 DESC LIMIT 5";
		List<SQLStatement> stmtList = SQLUtils.parseStatements(sql, JdbcConstants.HIVE);
		for (SQLStatement stmt : stmtList) {
			String result = stmt.toString();
			if (result == null || result.toUpperCase().indexOf("DISTRIBUTE BY") < 0) {
				throw new Exception("'DISTRIBUTE BY' not found in the result");
			}
			if (result == null || result.toUpperCase().indexOf("SORT BY") < 0) {
				throw new Exception("'SORT BY' not found in the result");
			}
			if (result == null || result.toUpperCase().indexOf("LIMIT") < 0) {
				throw new Exception("'LIMIT' not found in the result");
			}
		}
	}

	public void test_union_order_limit_1() throws Exception {
		String sql = "SELECT col_1 col FROM table_1 "
				+ "union "
				+ "SELECT col_2 col FROM table_2 "
				+ "union "
				+ "SELECT col_3 col FROM table_3 "
				+ "ORDER BY col ASC LIMIT 5";
		List<SQLStatement> stmtList = SQLUtils.parseStatements(sql, JdbcConstants.HIVE);
		for (SQLStatement stmt : stmtList) {
			String result = stmt.toString();
			if (result == null || result.toUpperCase().indexOf("LIMIT") < 0) {
				throw new Exception("'LIMIT' not found in the result");
			}
		}
	}
	
	public void test_union_order_limit_2() throws Exception {
		String sql = "select tmp.col col from (SELECT col_1 col FROM table_1 ORDER BY col LIMIT 5) tmp "
				+ "union "
				+ "SELECT col_2 col FROM table_2 "
				+ "union "
				+ "SELECT col_3 col FROM table_3 "
				+ "ORDER BY col ASC LIMIT 5";
		List<SQLStatement> stmtList = SQLUtils.parseStatements(sql, JdbcConstants.HIVE);
		for (SQLStatement stmt : stmtList) {
			String result = stmt.toString();
			if (result == null || result.toUpperCase().indexOf("LIMIT") < 0) {
				throw new Exception("'LIMIT' not found in the result");
			}
		}
	}
}
