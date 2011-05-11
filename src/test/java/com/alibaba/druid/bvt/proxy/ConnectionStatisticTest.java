package com.alibaba.druid.bvt.proxy;

import junit.framework.Assert;
import junit.framework.TestCase;

import com.alibaba.druid.stat.JdbcConnectionStat;


public class ConnectionStatisticTest extends TestCase {
	public void test_connection_stat() throws Exception {
		JdbcConnectionStat.Entry stat = new JdbcConnectionStat.Entry (null, 1001L);
		Assert.assertEquals(null, stat.getEstablishTime());
		Assert.assertEquals(null, stat.getConnectStackTrace());
		Assert.assertEquals(null, stat.getLastStatementStatckTrace());
		
		stat.setLastStatementStatckTrace(new Exception());
		Assert.assertNotNull(stat.getLastStatementStatckTrace());
		
		stat.error(new Exception());
		Assert.assertNotNull(stat.getLastErrorTime());
	}
}
