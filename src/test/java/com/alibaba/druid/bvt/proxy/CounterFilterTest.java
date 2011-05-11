/*
 * Copyright 2011 Alibaba Group.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.alibaba.druid.bvt.proxy;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

import junit.framework.TestCase;

import org.junit.Assert;

import com.alibaba.druid.filter.FilterChain;
import com.alibaba.druid.filter.FilterChainImpl;
import com.alibaba.druid.filter.stat.StatFilter;
import com.alibaba.druid.mock.MockDriver;
import com.alibaba.druid.proxy.jdbc.ConnectionProxy;
import com.alibaba.druid.proxy.jdbc.DataSourceProxyConfig;
import com.alibaba.druid.proxy.jdbc.DataSourceProxyImpl;
import com.alibaba.druid.proxy.jdbc.ResultSetProxy;
import com.alibaba.druid.stat.JdbcResultSetStat;

public class CounterFilterTest extends TestCase {
	String sql = "SELECT * FROM PATROL";

	public void test_countFilter() throws Exception {
		DataSourceProxyConfig config = new DataSourceProxyConfig();
		config.setUrl("");
		
		StatFilter filter = new StatFilter();
		filter.init(new DataSourceProxyImpl(null, config));
		
		filter.reset();
		
		Assert.assertNull(StatFilter.getStatFilter(new DataSourceProxyImpl(null, config)));
		Assert.assertNull(filter.getSqlStat(Integer.MAX_VALUE));
		Assert.assertNull(filter.getConnectionConnectLastTime());

		FilterChain chain = new FilterChainImpl(new DataSourceProxyImpl(null, config)) {
			public ConnectionProxy connection_connect(Properties info) throws SQLException {
				throw new SQLException();
			}
		};

		Exception error = null;
		try {
			filter.connection_connect(chain, new Properties());
		} catch (SQLException ex) {
			error = ex;
		}
		Assert.assertNotNull(error);
		Assert.assertEquals(1, filter.getConnectionConnectErrorCount());
		Assert.assertNotNull(filter.getConnectionConnectLastTime());
	}

	public void test_count_filter() throws Exception {
		DataSourceProxyConfig config = new DataSourceProxyConfig();
		config.setUrl("");
		
		StatFilter filter = new StatFilter();

		MockDriver driver = new MockDriver();
		DataSourceProxyImpl dataSource = new DataSourceProxyImpl(driver, config);

		filter.init(dataSource);
		config.getFilters().add(filter);

		Connection conn = dataSource.connect(null);

		Statement stmt = conn.createStatement();
		ResultSetProxy rs = (ResultSetProxy) stmt.executeQuery(sql);
		JdbcResultSetStat.Entry resultSetStat = StatFilter.getResultSetInfo(rs);
		rs.close();
		stmt.close();

		conn.close();
		conn.close();
	}

}
