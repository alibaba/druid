package com.alibaba.druid.bvt.proxy.fake;

import java.sql.Connection;
import java.util.Properties;

import junit.framework.TestCase;

import org.junit.Assert;

import com.alibaba.druid.proxy.DruidDriver;

public class DruidDriverTest extends TestCase {
	public void test_0 () throws Exception {
		String url = "jdbc:wrap-jdbc:filters=default,commonLogging,log4j:name=preCallTest:jdbc:fake:c1";
		Properties info = new Properties();
		DruidDriver driver = new DruidDriver();
		Connection conn = driver.connect(url, info);
		Assert.assertNotNull(conn);
		Assert.assertEquals("c1", conn.getCatalog());
		
		conn.setCatalog("c2");
		Assert.assertEquals("c2", conn.getCatalog());
		
		conn.setTransactionIsolation(100);
		Assert.assertEquals(100, conn.getTransactionIsolation());
		
		conn.close();
	}
}
