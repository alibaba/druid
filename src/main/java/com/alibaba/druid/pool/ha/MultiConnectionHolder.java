package com.alibaba.druid.pool.ha;

import java.sql.Connection;

public class MultiConnectionHolder {

    private final Connection       connection;
    private final DataSourceHolder dataSourceHolder;

    public MultiConnectionHolder(DataSourceHolder dataSourceHolder, Connection connection){
        super();
        this.dataSourceHolder = dataSourceHolder;
        this.connection = connection;
    }

    public Connection getConnection() {
        return connection;
    }

    public DataSourceHolder getDataSourceHolder() {
        return dataSourceHolder;
    }

}
