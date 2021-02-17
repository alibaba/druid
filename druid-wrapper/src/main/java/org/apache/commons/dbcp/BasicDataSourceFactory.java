package org.apache.commons.dbcp;

import java.util.Properties;

import javax.sql.DataSource;

import com.alibaba.druid.pool.DruidDataSourceFactory;

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
