package com.alibaba.druid.stat;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;

import junit.framework.TestCase;


public class TestOracle extends TestCase {
	public void test_0() throws Exception {
		String url = "jdbc:oracle:thin:@10.20.144.80:1521:orcl";
		Class.forName("oracle.jdbc.driver.OracleDriver");
		Connection conn = DriverManager.getConnection(url, "druid", "druid123");
		
		PreparedStatement stmt = conn.prepareStatement("select sysdate");
		stmt.executeBatch();
		
		conn.close();
	}
}
