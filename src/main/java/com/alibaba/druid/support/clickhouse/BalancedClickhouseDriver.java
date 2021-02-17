package com.alibaba.druid.support.clickhouse;

import ru.yandex.clickhouse.BalancedClickhouseDataSource;

import java.sql.Connection;
import java.sql.DriverPropertyInfo;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.Properties;
import java.util.logging.Logger;

public class BalancedClickhouseDriver implements java.sql.Driver {
    private final String url;
    private BalancedClickhouseDataSource dataSource;

    public BalancedClickhouseDriver(final String url, Properties properties) {
        this.url = url;
        this.dataSource = new BalancedClickhouseDataSource(url, properties);
    }

    @Override
    public Connection connect(String url, Properties info) throws SQLException {
        if (!acceptsURL(url)) {
            throw new SQLException("TODO");
        }

        return dataSource.getConnection();
    }

    @Override
    public boolean acceptsURL(String url) throws SQLException {
        return this.url.equals(url);
    }

    @Override
    public DriverPropertyInfo[] getPropertyInfo(String url, Properties info) throws SQLException {
        return new DriverPropertyInfo[0];
    }

    @Override
    public int getMajorVersion() {
        return 0;
    }

    @Override
    public int getMinorVersion() {
        return 0;
    }

    @Override
    public boolean jdbcCompliant() {
        return false;
    }

    @Override
    public Logger getParentLogger() throws SQLFeatureNotSupportedException {
        return null;
    }
}
