package com.alibaba.druid.bvt.proxy;

import java.sql.SQLClientInfoException;
import java.util.Properties;

import junit.framework.TestCase;

import com.alibaba.druid.filter.FilterEventAdapter;
import com.alibaba.druid.proxy.jdbc.ConnectionProxyImpl;
import com.alibaba.druid.proxy.jdbc.DataSourceProxy;
import com.alibaba.druid.proxy.jdbc.DataSourceProxyConfig;
import com.alibaba.druid.proxy.jdbc.DataSourceProxyImpl;

public class ConnectionProxyImplTest extends TestCase {
	public void test_connection() throws Exception {
		DataSourceProxyConfig config = new DataSourceProxyConfig();
		DataSourceProxy dataSource = new DataSourceProxyImpl(null, config);
		
		FilterEventAdapter filter = new FilterEventAdapter (){};
		filter.init(dataSource);
		
		ConnectionProxyImpl rawConnection = new ConnectionProxyImpl(null, null, new Properties(), 0) {
			public void setClientInfo(String name, String value) throws SQLClientInfoException {
				
			}
		};
		
		ConnectionProxyImpl connection = new ConnectionProxyImpl(dataSource, rawConnection, new Properties(), 1001);
		
		connection.setClientInfo("name", null);
	}

}
