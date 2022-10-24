package org.apache.commons.dbcp;

import javax.sql.DataSource;

import java.util.Properties;

public class ManagedBasicDataSourceFactory extends BasicDataSourceFactory {
    public static DataSource createDataSource(Properties properties) throws Exception {
        ManagedBasicDataSource dataSource = new ManagedBasicDataSource();
        config(dataSource, properties);
        return dataSource;
    }

    protected DataSource createDataSourceInternal(Properties properties) throws Exception {
        ManagedBasicDataSource dataSource = new ManagedBasicDataSource();
        config(dataSource, properties);
        return dataSource;
    }
}
