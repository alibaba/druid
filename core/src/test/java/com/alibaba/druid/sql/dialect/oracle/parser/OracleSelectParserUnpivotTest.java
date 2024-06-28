package com.alibaba.druid.sql.dialect.oracle.parser;

import java.util.Arrays;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;

public class OracleSelectParserUnpivotTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testUnpivotSingleColumn() {
		String sql = "SELECT TERM, subject, STU_NAME, score\n"
			+ "FROM (\n"
			+ "	SELECT '罗飞' AS STU_NAME, '2001-2002' AS TERM, '90' AS wjf, '88' AS xxds, '85' AS sjjg\n"
			+ "		, '71' AS czxt\n"
			+ "	FROM DUAL\n"
			+ ")\n"
			+ "UNPIVOT (score FOR subject IN (wjf AS '微积分', xxds AS '线性代数', sjjg AS '数据结构', czxt AS '操作系统')) t";
		SQLStatement stat = SQLUtils.parseSingleStatement(sql, DbType.oracle, false);
		System.out.println(stat.toString());
		Assert.assertEquals(sql, stat.toString());
		System.out.println("=============");
	}

	@Test
	public void testUnpivotSingleColumn2() {
		String sql = "SELECT TERM, subject, STU_NAME, score, hello\n"
			+ "FROM (\n"
			+ "	SELECT '罗飞' AS STU_NAME, '2001-2002' AS TERM, '90' AS wjf, '88' AS xxds, '85' AS sjjg\n"
			+ "		, '72' AS czxt\n"
			+ "	FROM DUAL\n"
			+ ")\n"
			+ "UNPIVOT (score FOR (subject, hello) IN (wjf\n"
			+ "AS ('微积分', '微积分'), xxds\n"
			+ "AS ('线性代数', 'abc'), sjjg\n"
			+ "AS ('数据结构', 'def'), czxt\n"
			+ "AS ('操作系统', 'ghk'))) t";
		SQLStatement stat = SQLUtils.parseSingleStatement(sql, DbType.oracle, false);
		System.out.println(stat.toString());
		Assert.assertEquals(sql, stat.toString());
		System.out.println("=============");
	}

	@Test
	public void testUnpivotMultiColumns() {
		String sql = "SELECT TERM, subject, NAME, score\n"
			+ "FROM (\n"
			+ "	SELECT '罗飞' AS STU_NAME, '2001-2002' AS TERM, '90' AS wjf, '88' AS xxds, '85' AS sjjg\n"
			+ "		, '73' AS czxt\n"
			+ "	FROM DUAL\n"
			+ "	UNION ALL\n"
			+ "	SELECT '罗游' AS STU_NAME, '2002-2003' AS TERM, '91' AS wjf, '81' AS xxds, '81' AS sjjg\n"
			+ "		, '74' AS czxt\n"
			+ "	FROM DUAL\n"
			+ ")\n"
			+ "UNPIVOT ( (NAME, score) FOR subject IN ((STU_NAME, wjf) AS '微积分', (STU_NAME, xxds) AS '线性代数', (STU_NAME, sjjg) AS '数据结构', (STU_NAME, czxt) AS '操作系统')) t";
		SQLStatement stat = SQLUtils.parseSingleStatement(sql, DbType.oracle, false);
		System.out.println(stat.toString());
		Assert.assertEquals(sql, stat.toString());
		System.out.println("=============");
	}

	@Test
	public void testUnpivotMultiColumns2() {
		String sql = "SELECT TERM, subject, NAME, score, hello\n"
			+ "FROM (\n"
			+ "	SELECT '罗飞' AS STU_NAME, '2001-2002' AS TERM, '90' AS wjf, '88' AS xxds, '85' AS sjjg\n"
			+ "		, '75' AS czxt\n"
			+ "	FROM DUAL\n"
			+ "	UNION ALL\n"
			+ "	SELECT '罗游' AS STU_NAME, '2002-2003' AS TERM, '91' AS wjf, '81' AS xxds, '81' AS sjjg\n"
			+ "		, '76' AS czxt\n"
			+ "	FROM DUAL\n"
			+ "	UNION ALL\n"
			+ "	SELECT '罗飞' AS STU_NAME, '2002-2003' AS TERM, '91' AS wjf, '81' AS xxds, '81' AS sjjg\n"
			+ "		, '77' AS czxt\n"
			+ "	FROM DUAL\n"
			+ ")\n"
			+ "UNPIVOT ( (NAME, score) FOR (subject, hello) IN ((STU_NAME, wjf)\n"
			+ "AS ('微积分', '123'), (STU_NAME, xxds)\n"
			+ "AS ('线性代数', 'abc'), (STU_NAME, sjjg)\n"
			+ "AS ('数据结构', 'def'), (STU_NAME, czxt)\n"
			+ "AS ('操作系统', 'ghk'))) t";
		SQLStatement stat = SQLUtils.parseSingleStatement(sql, DbType.oracle, false);
		System.out.println(stat.toString());
		Assert.assertEquals(sql, stat.toString());
		System.out.println("=============");
	}

	@Test
	public void testOdpsUnpivotSingleColumn() {
		String sql = "SELECT *\n"
			+ "FROM mf_shops\n"
			+ "UNPIVOT (sales FOR shop IN (shop1 AS 'shop_name_1', shop2 AS 'shop_name_2', shop3 AS 'shop_name_3', shop4 AS 'shop_name_4'))";
		SQLStatement stat = SQLUtils.parseSingleStatement(sql, DbType.oracle, false);
		System.out.println(stat.toString());
		Assert.assertEquals(sql, stat.toString());
		System.out.println("=============");
	}

	@Test
	public void testOdpsUnpivotMultiColumns() {
		String sql = "SELECT *\n"
			+ "FROM mf_shops\n"
			+ "UNPIVOT ( (sales1, sales2) FOR shop IN ((shop1, shop2) AS 'east_shop', (shop3, shop4) AS 'west_shop'))";
		SQLStatement stat = SQLUtils.parseSingleStatement(sql, DbType.oracle, false);
		System.out.println(stat.toString());
		Assert.assertEquals(sql, stat.toString());
		System.out.println("=============");
	}

	@Test
	public void testOdpsUnpivotMultiColumns2() {
		String sql = "SELECT *\n"
			+ "FROM mf_shops\n"
			+ "UNPIVOT ( (sales1, sales2) FOR (shop_name, location) IN ((shop1, shop2)\n"
			+ "AS ('east_shop', 'east'), (shop3, shop4)\n"
			+ "AS ('west_shop', 'west')))";
		SQLStatement stat = SQLUtils.parseSingleStatement(sql, DbType.oracle, false);
		System.out.println(stat.toString());
		Assert.assertEquals(sql, stat.toString());
		System.out.println("=============");
	}

}
