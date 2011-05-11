package com.alibaba.druid.benckmark.sqlcase.dragoon;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import junit.framework.Assert;

import com.alibaba.druid.benckmark.BenchmarkCase;
import com.alibaba.druid.benckmark.SQLExecutor;

public class SelectSysUser extends BenchmarkCase {
	private String sql;
	private Connection conn;
	
	public SelectSysUser() {
		super ("Dragoon-SelectSysUser");
		
		sql = "SELECT * FROM sys_user s";
	}
	
	@Override
	public void setUp(SQLExecutor sqlExec) throws Exception {
		conn = sqlExec.getConnection();
	}

	@Override
	public void execute(SQLExecutor sqlExec) throws Exception {
		Statement stmt = conn.createStatement();
		ResultSet rs = stmt.executeQuery(sql);
		int rowCount = 0;
		int value = 0;
		while (rs.next()) {
			value = rs.getInt(1);
			rowCount++;
		}
		Assert.assertEquals(1, value);
		Assert.assertEquals(1, rowCount);
		rs.close();
		stmt.close();
	}

	@Override
	public void tearDown(SQLExecutor sqlExec) throws Exception {
		conn.close();
		conn = null;
	}

}
