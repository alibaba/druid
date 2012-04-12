package com.alibaba.druid.bvt.sql.oracle;

import com.alibaba.druid.sql.SQLUtils;

import junit.framework.TestCase;

public class OracleFormatTest extends TestCase{
	public void test_formatOracle(){
		String sql = SQLUtils.formatOracle("select substr('123''''a''''bc',0,3) FROM dual");
		System.out.println(sql);
	}
}
