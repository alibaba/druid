package com.alibaba.druid.benckmark;

import java.sql.Connection;
import java.sql.SQLException;

public abstract class SQLExecutor {
	private final String name;

	public SQLExecutor(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}
	
	public abstract Connection getConnection() throws SQLException;
}
