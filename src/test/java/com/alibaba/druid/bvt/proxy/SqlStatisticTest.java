package com.alibaba.druid.bvt.proxy;

import junit.framework.Assert;
import junit.framework.TestCase;

import com.alibaba.druid.stat.JdbcSqlStat;


public class SqlStatisticTest extends TestCase {
	public void test_sql_stat() throws Exception {
		JdbcSqlStat stat = new JdbcSqlStat("SELECT * FROM t_user");
		Assert.assertEquals(null, stat.getExecuteLastStartTime());
		Assert.assertEquals(null, stat.getExecuteNanoSpanMaxOccurTime());
		Assert.assertEquals(null, stat.getExecuteErrorLastTime());
		
		stat.error(new Exception());
		Assert.assertNotNull(stat.getExecuteErrorLast());
		Assert.assertNotNull(stat.getExecuteErrorLastTime());
	}
}
