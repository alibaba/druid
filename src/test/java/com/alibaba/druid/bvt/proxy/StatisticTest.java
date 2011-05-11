package com.alibaba.druid.bvt.proxy;

import junit.framework.Assert;
import junit.framework.TestCase;

import com.alibaba.druid.stat.JdbcConnectionStat;


public class StatisticTest extends TestCase {
	public void test_stat() throws Exception {
		JdbcConnectionStat stat = new JdbcConnectionStat ();
		Assert.assertEquals(null, stat.getLastConnectTime());
		stat.setConcurrentCount(1);
		Assert.assertEquals(1, stat.getConcurrentMax());
		stat.setConcurrentCount(2);
		Assert.assertEquals(2, stat.getConcurrentMax());
	}
}
