package com.alibaba.druid.benckmark;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DirectSQLExecutor extends SQLExecutor {
	private final String jdbcUrl;
	private final String user;
	private final String password;
	private final String driverClassName;

	public DirectSQLExecutor(String name, String jdbcUrl, String user, String password) {
		super(name);

		this.jdbcUrl = jdbcUrl;
		this.user = user;
		this.password = password;

		try {
			driverClassName = getDriverClassName(jdbcUrl);
			Class.forName(driverClassName);
		} catch (Exception ex) {
			throw new IllegalStateException(ex.getMessage(), ex);
		}
	}

	public String getDriverClassName(String url) throws SQLException {
		if (url.startsWith("jdbc:derby:")) {
			return "org.apache.derby.jdbc.EmbeddedDriver";
		} else if (url.startsWith("jdbc:mysql:")) {
			return "com.mysql.jdbc.Driver";
		} else if (url.startsWith("jdbc:oracle:")) {
			return "oracle.jdbc.driver.OracleDriver";
		} else if (url.startsWith("jdbc:microsoft:")) {
			return "com.microsoft.jdbc.sqlserver.SQLServerDriver";
		} else if (url.startsWith("jdbc:jtds:")) {
			return "net.sourceforge.jtds.jdbc.Driver";
		} else if (url.startsWith("jdbc:fake:")) {
			return "com.alibaba.druid.mock.MockDriver";
		} else if (url.startsWith("jdbc:wrap-jdbc:")) {
			return "com.alibaba.druid.proxy.DruidDriver";
		} else {
			throw new SQLException("unkow jdbc driver");
		}
	}

	@Override
	public Connection getConnection() throws SQLException {
		return DriverManager.getConnection(jdbcUrl, user, password);
	}

}
