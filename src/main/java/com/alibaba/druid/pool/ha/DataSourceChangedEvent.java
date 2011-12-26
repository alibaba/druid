package com.alibaba.druid.pool.ha;

import com.alibaba.druid.pool.DruidDataSource;

public class DataSourceChangedEvent {

    private final DruidDataSource dataSource;

    public DataSourceChangedEvent(DruidDataSource dataSource){
        this.dataSource = dataSource;
    }

    public DruidDataSource getDataSource() {
        return dataSource;
    }

}
