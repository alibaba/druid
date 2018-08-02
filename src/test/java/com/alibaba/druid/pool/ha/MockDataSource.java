package com.alibaba.druid.pool.ha;

import com.alibaba.druid.mock.MockConnection;
import com.alibaba.druid.pool.WrapperAdapter;

import javax.sql.DataSource;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.logging.Logger;

public class MockDataSource extends WrapperAdapter implements DataSource {
    private String name;

    public MockDataSource(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public Connection getConnection() throws SQLException {
        return new MockConnection(null, name, null);
    }

    @Override
    public Connection getConnection(String username, String password) throws SQLException {
        return null;
    }

    @Override
    public PrintWriter getLogWriter() throws SQLException {
        return null;
    }

    @Override
    public void setLogWriter(PrintWriter out) throws SQLException {

    }

    @Override
    public void setLoginTimeout(int seconds) throws SQLException {

    }

    @Override
    public int getLoginTimeout() throws SQLException {
        return 0;
    }

    public Logger getParentLogger() throws SQLFeatureNotSupportedException {
        return null;
    }
}
