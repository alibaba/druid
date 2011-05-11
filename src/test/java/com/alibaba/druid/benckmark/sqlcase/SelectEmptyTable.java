package com.alibaba.druid.benckmark.sqlcase;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import junit.framework.Assert;

import com.alibaba.druid.benckmark.BenchmarkCase;
import com.alibaba.druid.benckmark.SQLExecutor;
import com.alibaba.druid.util.JdbcUtils;

public class SelectEmptyTable extends BenchmarkCase {
	private String sql;
	private Connection conn;
	private String tableName;
	
	private String createTableSql;
	private String dropTableSql;
	public SelectEmptyTable() {
		super ("SelectEmptyTable");
		
		tableName = "T_" + System.currentTimeMillis();
		
		createTableSql = "CREATE TABLE " + tableName + "(F1 INT, F2 INT, F3 INT, F4 INT, F5 INT, F6 INT, F7 INT, F8 INT, F9 INT)";
		dropTableSql = "DROP TABLE " + tableName;
		sql = "SELECT * FROM T_" + tableName;
	}
	
	@Override
	public void setUp(SQLExecutor sqlExec) throws Exception {
		conn = sqlExec.getConnection();
		
		Statement stmt = null;
		try {
			stmt = conn.createStatement();
			stmt.execute(createTableSql);
		} finally {
			JdbcUtils.close(stmt);
		}
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
		Statement stmt = null;
		try {
			stmt = conn.createStatement();
			stmt.execute(dropTableSql);
		} finally {
			JdbcUtils.close(stmt);
		}
		
		conn.close();
		conn = null;
	}

}
