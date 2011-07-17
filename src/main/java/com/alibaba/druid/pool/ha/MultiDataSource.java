package com.alibaba.druid.pool.ha;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;

import com.alibaba.druid.pool.DataSourceAdapter;
import com.alibaba.druid.pool.DruidDataSource;

public abstract class MultiDataSource extends DataSourceAdapter {

    protected ArrayList<DruidDataSource> dataSources;

    public MultiDataSource(){
        dataSources = new ArrayList<DruidDataSource>();
    }

    @Override
    public Connection getConnection() throws SQLException {
        throw new UnsupportedOperationException();
    }
}
