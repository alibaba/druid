package com.alibaba.druid.bvt.sql.mysql;

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.parser.SQLParserUtils;

import junit.framework.TestCase;

public class MySqlUpdateStatementLimitTest extends TestCase{
	public void test_limit(){
		String sql = "update t set name = 'x' where id < 100 limit 10";
		String rs = SQLUtils.formatMySql(sql);
		System.out.println(rs);
		
	}
}
