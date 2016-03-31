package com.alibaba.druid.bvt.pool;

import java.sql.Connection;
import java.util.Properties;

import junit.framework.TestCase;

import org.junit.Assert;
import org.osjava.sj.SimpleContext;

import com.alibaba.druid.pool.DruidDataSource;

public class DruidDataSourceConverterTest extends TestCase {

	private DruidDataSource dataSource;

	protected void setUp() throws Exception {
	    String osName = System.getProperty("os.name");
	    
		String root = DruidDataSourceConverterTest.class
				.getResource("/com/alibaba/druid/pool/simplejndi/").toString();
		if (root.startsWith("file:/")) {
			root = root.substring("file://".length() - 1);
		}
		
		if (osName.toLowerCase().indexOf("win") == -1) {
		    root = "/" + root;
		}
		Properties props = new Properties();
		props.put("org.osjava.sj.root", root);
		props.put("java.naming.factory.initial",
				"org.osjava.sj.SimpleContextFactory");
		props.put("org.osjava.sj.delimiter", "/");
		javax.naming.Context ctx = new SimpleContext(props);
		dataSource = (DruidDataSource) ctx.lookup("jdbc/druidTest");
		dataSource.init();
	}

	protected void tearDown() throws Exception {
		dataSource.close();
	}

	public void test_conn() throws Exception {
		Assert.assertEquals(true, dataSource.isInited());
		Connection conn = dataSource.getConnection();
		Assert.assertEquals(1, dataSource.getActiveCount());
		conn.close();
		Assert.assertEquals(0, dataSource.getActiveCount());
	}
}
