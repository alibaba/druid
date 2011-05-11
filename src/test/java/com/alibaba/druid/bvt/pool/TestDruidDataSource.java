package com.alibaba.druid.bvt.pool;

import java.sql.Connection;

import junit.framework.Assert;
import junit.framework.TestCase;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.PoolableConnection;

public class TestDruidDataSource extends TestCase {
	public void test_0 () throws Exception {
		DruidDataSource dataSource = new DruidDataSource();
		
		Class.forName("com.alibaba.druid.mock.MockDriver");
		
		dataSource.setInitialSize(10);
		dataSource.setMaxIdle(10);
		dataSource.setDriverClass("com.alibaba.druid.mock.MockDriver");
		dataSource.setUrl("jdbc:mock:xxx");
		
		Assert.assertEquals(0, dataSource.getConnectCount());
		Assert.assertEquals(0, dataSource.getConnectErrorCount());
		Assert.assertEquals(0, dataSource.getCloseCount());
		Assert.assertEquals(0, dataSource.getPoolingSize());
		
		Connection conn = dataSource.getConnection();
		
		Assert.assertTrue(conn instanceof PoolableConnection);
		
		Assert.assertEquals(1, dataSource.getConnectCount());
		Assert.assertEquals(0, dataSource.getConnectErrorCount());
		Assert.assertEquals(0, dataSource.getCloseCount());
		Assert.assertEquals(0, dataSource.getRecycleCount());
		Assert.assertEquals(1, dataSource.getActiveCount());
		Assert.assertEquals(9, dataSource.getPoolingSize());
		
		conn.close();
		
		Assert.assertEquals(1, dataSource.getConnectCount());
		Assert.assertEquals(0, dataSource.getConnectErrorCount());
		Assert.assertEquals(1, dataSource.getCloseCount());
		Assert.assertEquals(1, dataSource.getRecycleCount());
		Assert.assertEquals(0, dataSource.getActiveCount());
		Assert.assertEquals(10, dataSource.getPoolingSize());
		
		conn.close(); // 重复close
		
		Assert.assertEquals(1, dataSource.getConnectCount());
		Assert.assertEquals(0, dataSource.getConnectErrorCount());
		Assert.assertEquals(1, dataSource.getCloseCount());
		Assert.assertEquals(1, dataSource.getRecycleCount());
		Assert.assertEquals(0, dataSource.getActiveCount());
		
		dataSource.close();
	}
}
