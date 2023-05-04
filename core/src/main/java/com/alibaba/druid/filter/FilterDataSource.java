package com.alibaba.druid.filter;

import com.alibaba.druid.proxy.jdbc.*;

import javax.sql.DataSource;
import java.io.PrintWriter;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.logging.Logger;

/**
 * @author addenda
 * @since 2023/5/03 18:58
 */
public class FilterDataSource implements DataSource {

    private DataSource dataSource;

    private DataSourceProxy dataSourceProxy;

    public FilterDataSource(DataSource dataSource, List<Filter> filterList) {
        this.dataSource = dataSource;
        if (filterList == null) {
            throw new IllegalArgumentException("filterList不能为空。");
        }
        dataSourceProxy = new InnerDataSourceProxyImpl(filterList);
    }

    public FilterDataSource(DataSource dataSource, Driver driver, DataSourceProxyConfig dataSourceProxyConfig, List<Filter> filterList) {
        this.dataSource = dataSource;
        if (filterList == null) {
            throw new IllegalArgumentException("filterList不能为空。");
        }
        dataSourceProxy = new InnerDataSourceProxyImpl(driver, dataSourceProxyConfig, filterList);
    }

    @Override
    public Connection getConnection() throws SQLException {
        FilterChain filterChain = new FilterChainImpl(dataSourceProxy);
        return filterChain.dataSource_connect(this);
    }

    @Override
    public Connection getConnection(String username, String password) throws SQLException {
        FilterChain filterChain = new FilterChainImpl(dataSourceProxy);
        return filterChain.dataSource_connect(this, username, password);
    }

    @Override
    public PrintWriter getLogWriter() throws SQLException {
        return dataSource.getLogWriter();
    }

    @Override
    public void setLogWriter(PrintWriter out) throws SQLException {
        dataSource.setLogWriter(out);
    }

    @Override
    public void setLoginTimeout(int seconds) throws SQLException {
        dataSource.setLoginTimeout(seconds);
    }

    @Override
    public int getLoginTimeout() throws SQLException {
        return dataSource.getLoginTimeout();
    }

    @Override
    public Logger getParentLogger() throws SQLFeatureNotSupportedException {
        return dataSource.getParentLogger();
    }

    public ConnectionProxy getConnectionDirect() throws SQLException {
        return new ConnectionProxyImpl(
                dataSourceProxy, dataSource.getConnection(), new Properties(), dataSourceProxy.createConnectionId());
    }

    public ConnectionProxy getConnectionDirect(String username, String password) throws SQLException {
        return new ConnectionProxyImpl(
                dataSourceProxy, dataSource.getConnection(username, password), new Properties(), dataSourceProxy.createConnectionId());
    }

    @Override
    public <T> T unwrap(Class<T> iface) throws SQLException {
        if (iface == null) {
            return null;
        }

        if (iface == this.getClass()) {
            return (T) this;
        }

        return dataSource.unwrap(iface);
    }

    @Override
    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        if (iface == null) {
            return false;
        }

        if (iface == this.getClass()) {
            return true;
        }

        return dataSource.isWrapperFor(iface);
    }

    private static class InnerDataSourceProxyImpl extends DataSourceProxyImpl {

        List<Filter> filterList;

        public InnerDataSourceProxyImpl(Driver rawDriver, DataSourceProxyConfig config, List<Filter> filterList) {
            super(rawDriver, config);
            this.filterList = filterList;
        }

        public InnerDataSourceProxyImpl(List<Filter> filterList) {
            super(new Driver() {

                @Override
                public Connection connect(String url, Properties info) throws SQLException {
                    throw new UnsupportedOperationException();
                }

                @Override
                public boolean acceptsURL(String url) throws SQLException {
                    throw new UnsupportedOperationException();
                }

                @Override
                public DriverPropertyInfo[] getPropertyInfo(String url, Properties info) throws SQLException {
                    throw new UnsupportedOperationException();
                }

                @Override
                public int getMajorVersion() {
                    return -1;
                }

                @Override
                public int getMinorVersion() {
                    return -1;
                }

                @Override
                public boolean jdbcCompliant() {
                    throw new UnsupportedOperationException();
                }

                @Override
                public Logger getParentLogger() throws SQLFeatureNotSupportedException {
                    throw new UnsupportedOperationException();
                }
            }, new DataSourceProxyConfig());
            this.filterList = filterList;
        }

        @Override
        public List<Filter> getProxyFilters() {
            return filterList;
        }

        @Override
        public String[] getFilterClasses() {
            List<Filter> filterConfigList = getProxyFilters();

            List<String> classes = new ArrayList<>();
            for (Filter filter : filterConfigList) {
                classes.add(filter.getClass().getName());
            }

            return classes.toArray(new String[classes.size()]);
        }

    }

}
