package com.alibaba.druid.benckmark.sqlcase;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Date;

import junit.framework.Assert;

import com.alibaba.druid.benckmark.BenchmarkCase;
import com.alibaba.druid.benckmark.SQLExecutor;

public class SelectNow extends BenchmarkCase {
	private String sql;
	private Connection conn;
	
	public SelectNow() {
		super ("SelectNow");
		
		sql = "SELECT NOW()";
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
		Date now = null;
		while (rs.next()) {
			now = rs.getTimestamp(1);
			rowCount++;
		}
		Assert.assertNotNull(now);
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
