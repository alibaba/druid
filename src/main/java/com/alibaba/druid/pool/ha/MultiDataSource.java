package com.alibaba.druid.pool.ha;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.sql.DataSource;

import com.alibaba.druid.pool.DruidDataSource;

public abstract class MultiDataSource implements DataSource {

    protected final List<DruidDataSource> dataSources;

    private int                           loginTimeout = 0;
    private PrintWriter                   logWriter    = new PrintWriter(System.out);
    
    public MultiDataSource() {
        dataSources  = new CopyOnWriteArrayList<DruidDataSource>();
    }
    
    public MultiDataSource(List<DruidDataSource> dataSources) {
        this.dataSources  = dataSources;
    }

    public List<DruidDataSource> getDataSources() {
        return dataSources;
    }

    @Override
    public PrintWriter getLogWriter() throws SQLException {
        return logWriter;
    }

    @Override
    public void setLogWriter(PrintWriter out) throws SQLException {
        this.logWriter = out;
    }

    @Override
    public void setLoginTimeout(int seconds) throws SQLException {
        loginTimeout = seconds;
    }

    @Override
    public int getLoginTimeout() throws SQLException {
        return loginTimeout;
    }

    @Override
    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        if (iface == null) {
            return false;
        }

        if (iface.isInstance(this)) {
            return true;
        }

        return false;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T unwrap(Class<T> iface) throws SQLException {
        if (iface == null) {
            return null;
        }

        if (iface.isInstance(this)) {
            return (T) this;
        }

        return null;
    }

    @Override
    public Connection getConnection(String username, String password) throws SQLException {
        throw new UnsupportedOperationException();
    }
}
