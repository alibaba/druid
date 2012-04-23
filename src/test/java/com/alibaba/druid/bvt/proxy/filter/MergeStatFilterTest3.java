package com.alibaba.druid.bvt.proxy.filter;

import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.Statement;

import junit.framework.Assert;
import junit.framework.TestCase;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.util.IOUtils;

public class MergeStatFilterTest3 extends TestCase {
	private DruidDataSource dataSource;

	protected void setUp() throws Exception {
		dataSource = new DruidDataSource();
		dataSource.setUrl("jdbc:mock:xx");
		dataSource.setFilters("mergeStat");
		dataSource.setDbType("postgresql");
	}

	protected void tearDown() throws Exception {
		dataSource.close();
	}

	public void test_merge() throws Exception {
		String sqllist = IOUtils.read(new InputStreamReader(this.getClass().getResourceAsStream("/bvt/parser/postgresql.txt")));
		String[] ss = sqllist.split("--");
		for(String sql:ss)
		{
			Connection conn = dataSource.getConnection();
			Statement stmt = conn.createStatement();
			stmt.execute(sql);
			stmt.close();
			conn.close();
		}


		Assert.assertEquals(1, dataSource.getDataSourceStat().getSqlStatMap()
				.size());
		System.out.println(dataSource.getDataSourceStat().getSqlStatMap().keySet().iterator().next());
		
	}

}
