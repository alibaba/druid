package com.alibaba.druid.sql.dialect.oracle.parser;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import org.junit.Assert;
import org.junit.Test;

public class OracleCreateTypeTest {
	@Test
	public void testCreateAssocArrayType() {
		String sql = "CREATE TYPE email_list_tab AS TABLE OF VARCHAR2(30) INDEX BY BINARY_INTEGER;";
		SQLStatement stat = SQLUtils.parseSingleStatement(sql, DbType.oracle, false);
		System.out.println(stat);
		Assert.assertEquals(sql, stat.toString());
		System.out.println("=============");
	}
}
