package com.alibaba.druid.support.ibatis;

import java.util.Map;

import javax.sql.DataSource;

public class DruidDataSourceFactory implements com.ibatis.sqlmap.engine.datasource.DataSourceFactory {

    private DataSource dataSource;

    @SuppressWarnings("rawtypes")
    public void initialize(Map map) {
        try {
            dataSource = com.alibaba.druid.pool.DruidDataSourceFactory.createDataSource(map);
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("init datasource error", e);
        }
    }

    public DataSource getDataSource() {
        return dataSource;
    }
}
