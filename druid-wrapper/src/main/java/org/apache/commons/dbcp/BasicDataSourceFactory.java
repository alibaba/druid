package org.apache.commons.dbcp;

import com.alibaba.druid.pool.DruidDataSourceFactory;

import javax.sql.DataSource;

import java.util.Properties;

public class BasicDataSourceFactory extends DruidDataSourceFactory {
    public static DataSource createDataSource(Properties properties) throws Exception {
        BasicDataSource dataSource = new BasicDataSource();
        config(dataSource, properties);
        return dataSource;
    }

    protected DataSource createDataSourceInternal(Properties properties) throws Exception {
        BasicDataSource dataSource = new BasicDataSource();
        config(dataSource, properties);
        return dataSource;
    }
}
