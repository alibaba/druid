package com.alibaba.druid.bvt.pool;

import java.sql.Connection;
import java.util.Properties;

import junit.framework.TestCase;

import org.junit.Assert;

import com.alibaba.druid.mock.MockConnection;
import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.DruidDataSourceFactory;

public class DruidDataSourceTest_initSql_factory extends TestCase {

    private DruidDataSource dataSource;

    protected void setUp() throws Exception {
        Properties properties = new Properties();
        properties.put(DruidDataSourceFactory.PROP_URL, "jdbc:mock:xxx");
        properties.put(DruidDataSourceFactory.PROP_INITCONNECTIONSQLS, ";;select 123");
        dataSource = (DruidDataSource) DruidDataSourceFactory.createDataSource(properties);
    }

    protected void tearDown() throws Exception {
        dataSource.close();
    }

    public void testDefault() throws Exception {
        Connection conn = dataSource.getConnection();

        MockConnection mockConn = conn.unwrap(MockConnection.class);

        Assert.assertEquals("select 123", mockConn.getLastSql());

        conn.close();
    }

}
