package com.alibaba.druid.sql.parser;

import java.util.List;

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.util.JdbcConstants;

import junit.framework.TestCase;

/**
 * <p>
 * 测试PostgreSQL包含中文SQL语句兼容性
 * </p>
 * <p>
 * 备注：Druid支持可以使用中文标点（空格、括号、逗号）作为标识符。
 * PostgreSQL将中文标点仅作为字符串，将导致SQL解析出错，例如:
 * <code>SELECT col_1 AS 一级（测试）列 FROM table_1</code>
 * 通过增加词法分析器IgnoreChinese特性和对应处理，支持忽略中文标点。
 * </p>
 * 
 * @author garfield
 *
 */
public class PGChineseSQLParseTest extends TestCase {

	public void test_chinese() throws Exception {
		String sql = "SELECT col_1 AS 一级（测试）列 FROM table_1";
		String expected = "SELECT col_1 AS 一级（测试）列\nFROM table_1";
		List<SQLStatement> stmtList = SQLUtils.parseStatements(sql, JdbcConstants.POSTGRESQL);
		assertEquals(1, stmtList.size());
		for (SQLStatement stmt : stmtList) {
			assertEquals(expected, stmt.toString());
		}
	}
}
