package com.alibaba.druid.pool.ha;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicInteger;

import com.alibaba.druid.pool.DataSourceAdapter;
import com.alibaba.druid.pool.DruidDataSource;

public abstract class MultiDataSource extends DataSourceAdapter {


    private Properties                   properties       = new Properties();

    private final AtomicInteger          connectionIdSeed = new AtomicInteger();
    private final AtomicInteger          statementIdSeed  = new AtomicInteger();

    public MultiDataSource(){
        
    }

    public int createConnectionId() {
        return connectionIdSeed.getAndIncrement();
    }

    public int createStatementId() {
        return statementIdSeed.getAndIncrement();
    }

    public abstract List<DruidDataSource> getDataSources();

    public abstract void setDataSources(List<DruidDataSource> dataSources);

    public Properties getProperties() {
        return properties;
    }

    @Override
    public abstract Connection getConnection() throws SQLException;

    public abstract Connection getConnectionInternal(int connectionId, String sql) throws SQLException;
}
