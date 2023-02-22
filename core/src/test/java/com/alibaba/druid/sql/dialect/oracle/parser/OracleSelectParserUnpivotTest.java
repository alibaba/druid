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
				+ "  FROM (SELECT '罗飞' STU_NAME,\n"
				+ "               '2001-2002' TERM,\n"
				+ "               '90' AS wjf,\n"
				+ "               '88' AS xxds,\n"
				+ "               '85' sjjg,\n"
				+ "               '70' czxt\n"
				+ "          FROM DUAL)\n"
				+ "UNPIVOT(score FOR subject IN(\n"
				+ "wjf   as '微积分',\n"
				+ "xxds as '线性代数',\n"
				+ "sjjg as '数据结构',\n"
				+ "czxt as '操作系统')) t";
		SQLStatement stat = SQLUtils.parseSingleStatement(sql, DbType.oracle, false);
		System.out.println(stat.toString());
	}

	@Test
	public void testUnpivotMultiColumns() {
		String sql = "SELECT TERM, subject, NAME, score\n"
				+ "  FROM (SELECT '罗飞' STU_NAME,\n"
				+ "               '2001-2002' TERM,\n"
				+ "               '90' AS wjf,\n"
				+ "               '88' AS xxds,\n"
				+ "               '85' sjjg,\n"
				+ "               '70' czxt\n"
				+ "          FROM DUAL\n"
				+ "        UNION ALL\n"
				+ "        SELECT '罗游' STU_NAME,\n"
				+ "               '2002-2003' TERM,\n"
				+ "               '91' AS wjf,\n"
				+ "               '81' AS xxds,\n"
				+ "               '81' sjjg,\n"
				+ "               '71' czxt\n"
				+ "          FROM DUAL\n"
				+ "          )\n"
				+ "UNPIVOT((NAME, score) FOR subject IN(\n"
				+ "(STU_NAME, wjf)   as '微积分'||'123',\n"
				+ "(STU_NAME, xxds) as '线性代数',\n"
				+ "(STU_NAME, sjjg) as '数据结构',\n"
				+ "(STU_NAME, czxt) as '操作系统')) t";
		SQLStatement stat = SQLUtils.parseSingleStatement(sql, DbType.oracle, false);
		System.out.println(stat.toString());
	}

	@Test
	public void testUnpivotMultiColumns2() {
		String sql = "SELECT TERM, subject, NAME, score, hello\n"
				+ "  FROM (SELECT '罗飞' STU_NAME,\n"
				+ "               '2001-2002' TERM,\n"
				+ "               '90' AS wjf,\n"
				+ "               '88' AS xxds,\n"
				+ "               '85' sjjg,\n"
				+ "               '70' czxt\n"
				+ "          FROM DUAL\n"
				+ "        UNION ALL\n"
				+ "        SELECT '罗游' STU_NAME,\n"
				+ "               '2002-2003' TERM,\n"
				+ "               '91' AS wjf,\n"
				+ "               '81' AS xxds,\n"
				+ "               '81' sjjg,\n"
				+ "               '71' czxt\n"
				+ "          FROM DUAL\n"
				+ "        UNION ALL\n"
				+ "        SELECT '罗飞' STU_NAME,\n"
				+ "               '2002-2003' TERM,\n"
				+ "               '91' AS wjf,\n"
				+ "               '81' AS xxds,\n"
				+ "               '81' sjjg,\n"
				+ "               '71' czxt\n"
				+ "          FROM DUAL\n"
				+ "          ) \n"
				+ "UNPIVOT((NAME,score) FOR (subject,hello) IN(\n"
				+ "(STU_NAME,wjf)   as ('微积分','123'),\n"
				+ "(STU_NAME,xxds) as ('线性代数','abc'),\n"
				+ "(STU_NAME,sjjg) as ('数据结构','def'),\n"
				+ "(STU_NAME,czxt) as ('操作系统','ghk'))) t";
		SQLStatement stat = SQLUtils.parseSingleStatement(sql, DbType.oracle, false);
		System.out.println(stat.toString());
	}

}
