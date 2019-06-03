package com.alibaba.druid.sql.parser;

import java.util.List;

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.util.JdbcConstants;

import junit.framework.TestCase;

/**
 * <pre>
 * HiveSQL Syntax Parsing Test <br>
 * Unsupported Syntax <br>
 * 1. Hint <br>
 * SELECT / *+ STREAMTABLE(a) * / ... <br>
 * SELECT / *+ MAPJOIN(b) * / ... <br>
 * 
 * 2. TABLESAMPLE
 * SELECT * FROM source TABLESAMPLE(BUCKET 3 OUT OF 32 ON rand()) s;
 * 
 * 3. Outer Lateral Views
 * SELECT * FROM src LATERAL VIEW OUTER explode(array()) C AS a limit 10;
 * 
 * 4. WINDOW clause
 * SELECT a, SUM(b) OVER w
 * FROM T
 * WINDOW w AS (PARTITION BY c ORDER BY d ROWS UNBOUNDED PRECEDING);
 * 
 * ***** ATTACHED SELECT SYNTAX ***** <br>
 * [WITH CommonTableExpression (, CommonTableExpression)*] <br>
 * SELECT [ALL | DISTINCT] select_expr, select_expr, ... <br>
 * FROM table_reference <br>
 * [WHERE where_condition] <br>
 * [GROUP BY col_list] <br>
 * [ORDER BY col_list] <br>
 * [CLUSTER BY col_list <br>
 * | [DISTRIBUTE BY col_list] [SORT BY col_list] <br>
 * ] <br>
 * [LIMIT [offset,] rows] <br>
 * </pre>
 * 
 * 
 * 
 * @author garfield
 *
 */
public class HiveSelectParserTest extends TestCase {

	public void test_distinct() throws Exception {
		String sql = "SELECT DISTINCT col1, col2 FROM t1";

		String expected = "SELECT DISTINCT col1, col2\n" //
				+ "FROM t1"; //
		List<SQLStatement> stmtList = SQLUtils.parseStatements(sql, JdbcConstants.HIVE);
		assertEquals(1, stmtList.size());
		for (SQLStatement stmt : stmtList) {
			assertEquals(expected, stmt.toString());
		}
	}

	public void test_all() throws Exception {
		String sql = "SELECT ALL col1, col2 FROM t1";

		String expected = "SELECT ALL col1, col2\n" //
				+ "FROM t1"; //
		List<SQLStatement> stmtList = SQLUtils.parseStatements(sql, JdbcConstants.HIVE);
		assertEquals(1, stmtList.size());
		for (SQLStatement stmt : stmtList) {
			assertEquals(expected, stmt.toString());
		}
	}

	public void test_with() throws Exception {
		String sql = "with q1 as (select * from src where key= '5')," //
				+ "q2 as (select * from src s2 where key = '4') " //
				+ "select * from q1 union all select * from q2"; //

		String expected = "WITH q1 AS (\n" //
				+ "\t\tSELECT *\n" //
				+ "\t\tFROM src\n" //
				+ "\t\tWHERE key = '5'\n" //
				+ "\t), \n" //
				+ "\tq2 AS (\n" //
				+ "\t\tSELECT *\n" //
				+ "\t\tFROM src s2\n" //
				+ "\t\tWHERE key = '4'\n" //
				+ "\t)\n" //
				+ "SELECT *\n" //
				+ "FROM q1\n" //
				+ "UNION ALL\n" //
				+ "SELECT *\n" //
				+ "FROM q2"; //
		List<SQLStatement> stmtList = SQLUtils.parseStatements(sql, JdbcConstants.HIVE);
		assertEquals(1, stmtList.size());
		for (SQLStatement stmt : stmtList) {
			assertEquals(expected, stmt.toString());
		}
	}

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

	public void test_limit_offset() throws Exception {
		String sql = "SELECT col_1, col_2 FROM table_1 LIMIT 2,5";

		String expected = "SELECT col_1, col_2\n" //
				+ "FROM table_1\n" //
				+ "LIMIT 2, 5"; //
		List<SQLStatement> stmtList = SQLUtils.parseStatements(sql, JdbcConstants.HIVE);
		assertEquals(1, stmtList.size());
		for (SQLStatement stmt : stmtList) {
			assertEquals(expected, stmt.toString());
		}
	}

	public void test_groupby() throws Exception {
		String sql = "SELECT pv_users.gender, " //
				+ "count(DISTINCT pv_users.userid), " //
				+ "count(*), " //
				+ "sum(DISTINCT pv_users.userid) " //
				+ "FROM pv_users " //
				+ "GROUP BY pv_users.gender";//

		String expected = "SELECT pv_users.gender, COUNT(DISTINCT pv_users.userid), COUNT(*)\n" //
				+ "\t, SUM(DISTINCT pv_users.userid)\n" //
				+ "FROM pv_users\n" //
				+ "GROUP BY pv_users.gender"; //
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

	public void test_distributeby_sortby_limit() throws Exception {
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

	public void test_join_multi_table() throws Exception {
		String sql = "SELECT a.val, b.val, c.val FROM a " //
				+ "JOIN b ON (a.key = b.key1) " //
				+ "JOIN c ON (c.key = b.key2)"; //

		String expected = "SELECT a.val, b.val, c.val\n" //
				+ "FROM a\n" //
				+ "\tJOIN b ON a.key = b.key1\n" //
				+ "\tJOIN c ON c.key = b.key2"; //
		List<SQLStatement> stmtList = SQLUtils.parseStatements(sql, JdbcConstants.HIVE);
		assertEquals(1, stmtList.size());
		for (SQLStatement stmt : stmtList) {
			assertEquals(expected, stmt.toString());
		}
	}

	public void test_join_semi() throws Exception {
		String sql = "SELECT a.key, a.val FROM a LEFT SEMI JOIN b ON (a.key = b.key)";

		String expected = "SELECT a.key, a.val\n" //
				+ "FROM a\n" //
				+ "\tLEFT SEMI JOIN b ON a.key = b.key"; //
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

	public void test_regex_column() throws Exception {
		String sql = "SELECT `(ds|hr)?+.+` FROM sales";

		String expected = "SELECT `(ds|hr)?+.+`\n" //
				+ "FROM sales"; //
		List<SQLStatement> stmtList = SQLUtils.parseStatements(sql, JdbcConstants.HIVE);
		assertEquals(1, stmtList.size());
		for (SQLStatement stmt : stmtList) {
			assertEquals(expected, stmt.toString());
		}
	}

	public void test_subquery() throws Exception {
		String sql = "SELECT t3.col FROM (" //
				+ "SELECT a+b AS col FROM t1 " //
				+ "UNION ALL " //
				+ "SELECT c+d AS col FROM t2) t3"; //

		String expected = "SELECT t3.col\n" //
				+ "FROM (\n" //
				+ "\tSELECT a + b AS col\n" //
				+ "\tFROM t1\n" //
				+ "\tUNION ALL\n" //
				+ "\tSELECT c + d AS col\n" //
				+ "\tFROM t2\n" //
				+ ") t3"; //
		List<SQLStatement> stmtList = SQLUtils.parseStatements(sql, JdbcConstants.HIVE);
		assertEquals(1, stmtList.size());
		for (SQLStatement stmt : stmtList) {
			assertEquals(expected, stmt.toString());
		}
	}

	public void test_lateral_view() throws Exception {
		String sql = "SELECT * FROM exampleTable " + "LATERAL VIEW explode(col1) myTable1 AS myCol1 "
				+ "LATERAL VIEW explode(myCol1) myTable2 AS myCol2";

		String expected = "SELECT *\n" //
				+ "FROM exampleTable\n" + "\tLATERAL VIEW explode(col1) myTable1 AS myCol1\n"
				+ "\tLATERAL VIEW explode(myCol1) myTable2 AS myCol2"; //
		List<SQLStatement> stmtList = SQLUtils.parseStatements(sql, JdbcConstants.HIVE);
		assertEquals(1, stmtList.size());
		for (SQLStatement stmt : stmtList) {
			assertEquals(expected, stmt.toString());
		}
	}
}
