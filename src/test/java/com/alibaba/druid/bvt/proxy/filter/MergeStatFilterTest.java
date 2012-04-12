package com.alibaba.druid.bvt.proxy.filter;

import java.sql.Connection;
import java.sql.Statement;

import junit.framework.Assert;
import junit.framework.TestCase;

import com.alibaba.druid.pool.DruidDataSource;

public class MergeStatFilterTest extends TestCase {
	private DruidDataSource dataSource;

	protected void setUp() throws Exception {
		dataSource = new DruidDataSource();
		dataSource.setUrl("jdbc:mock:xx");
		dataSource.setFilters("mergeStat");
	}

	protected void tearDown() throws Exception {
		dataSource.close();
	}

	public void test_merge() throws Exception {
		for (int i = 0; i < 100; ++i) {
			String sql = "select * from t where id = " + i;
			Connection conn = dataSource.getConnection();

			Statement stmt = conn.createStatement();
			stmt.execute(sql);
			stmt.close();

			conn.close();
		}
		
		Assert.assertEquals(1, dataSource.getDataSourceStat().getSqlStatMap().size());
	}

}
