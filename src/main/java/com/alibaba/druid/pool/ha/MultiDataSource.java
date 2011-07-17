package com.alibaba.druid.pool.ha;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicInteger;

import com.alibaba.druid.pool.DataSourceAdapter;
import com.alibaba.druid.pool.DruidDataSource;

public abstract class MultiDataSource extends DataSourceAdapter {

    protected ArrayList<DruidDataSource> dataSources;

    private Properties                   properties       = new Properties();

    private final AtomicInteger          connectionIdSeed = new AtomicInteger();
    private final AtomicInteger          statementIdSeed  = new AtomicInteger();

    public int createConnectionId() {
        return connectionIdSeed.getAndIncrement();
    }

    public int createStatementId() {
        return statementIdSeed.getAndIncrement();
    }

    public MultiDataSource(){
        dataSources = new ArrayList<DruidDataSource>();
    }

    public List<DruidDataSource> getDataSources() {
        return Collections.unmodifiableList(dataSources);
    }

    public void setDataSources(List<DruidDataSource> dataSources) {
        this.dataSources = new ArrayList<DruidDataSource>(dataSources);
        ;
    }

    public Properties getProperties() {
        return properties;
    }

    @Override
    public abstract Connection getConnection() throws SQLException;

    public abstract Connection getConnectionInternal(int connectionId, String sql) throws SQLException;
}
