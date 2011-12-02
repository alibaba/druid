package com.alibaba.druid.pool.ha.cobar;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.ha.MultiDataSource;
import com.alibaba.druid.pool.ha.MultiDataSourceConnection;


public class CobarDataSource extends MultiDataSource {
    private List<DruidDataSource> dataSources = new ArrayList<DruidDataSource>();

    @Override
    public List<DruidDataSource> getDataSources() {
        return dataSources;
    }

    public void setDataSources(List<DruidDataSource> dataSources) {
        this.dataSources = dataSources;
    }

    @Override
    public Connection getConnection() throws SQLException {
        return new MultiDataSourceConnection(this, createConnectionId());
    }

    @Override
    public Connection getConnectionInternal(MultiDataSourceConnection conn, String sql) throws SQLException {
        return null;
    }

    @Override
    public void handleNotAwailableDatasource(DruidDataSource dataSource) {
        
    }

}
