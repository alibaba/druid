package com.alibaba.druid.bvt.proxy.fake;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Properties;

import junit.framework.Assert;
import junit.framework.TestCase;

import com.alibaba.druid.mock.MockDriver;

public class FakeDriverTest extends TestCase {
	public void test_0() throws Exception {
		String url = "jdbc:fake:x1";
		Properties info = new Properties();

		String sql = "SELECT 1";

		MockDriver driver = new MockDriver();

		Connection conn = driver.connect(url, info);
		Statement stmt = conn.createStatement();

		ResultSet rs = stmt.executeQuery(sql);
		Assert.assertEquals(true, rs.next());
		Assert.assertEquals(1, rs.getInt(1));

		conn.close();
	}
}
