package org.apache.commons.dbcp2;

import com.alibaba.druid.pool.DruidDataSourceFactory;

import javax.sql.DataSource;
import java.util.Properties;

public class BasicDataSourceFactory extends DruidDataSourceFactory {
    public static BasicDataSource createDataSource(Properties properties) throws Exception {
        BasicDataSource dataSource = new BasicDataSource();
        config(dataSource, properties);
        return dataSource;
    }

    protected BasicDataSource createDataSourceInternal(Properties properties) throws Exception {
        BasicDataSource dataSource = new BasicDataSource();
        config(dataSource, properties);
        return dataSource;
    }
}
