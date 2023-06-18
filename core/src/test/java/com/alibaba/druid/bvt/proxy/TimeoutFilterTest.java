package com.alibaba.druid.bvt.proxy;

import com.alibaba.druid.DruidRuntimeException;
import com.alibaba.druid.mock.MockDriver;
import com.alibaba.druid.pool.DruidDataSource;
import junit.framework.TestCase;

import java.sql.SQLException;
import java.util.Properties;
import java.util.concurrent.TimeoutException;

public class TimeoutFilterTest extends TestCase {

    public void test_connection_connect_fail() throws SQLException {
        MockDriver driver = new MockDriver();
        Properties connectionProperties = new Properties();
        connectionProperties.put("connectSleep", "3000");
        connectionProperties.put("executeSleep", "1000");
        try (final DruidDataSource dataSource = new DruidDataSource()) {
            dataSource.setUrl("jdbc:mock:xxx");
            dataSource.setDriver(driver);
            dataSource.setInitialSize(1);
            dataSource.setMinIdle(1);
            dataSource.setFilters("timeout");
            dataSource.setMaxConnect(1000);
            dataSource.setConnectProperties(connectionProperties);
            dataSource.getConnection();
        } catch (DruidRuntimeException e) {
            assertTrue(e.getCause() instanceof TimeoutException);
        }
    }

    public void test_connection_connect_success() throws SQLException {
        MockDriver driver = new MockDriver();
        Properties connectionProperties = new Properties();
        connectionProperties.put("connectSleep", "3000");
        connectionProperties.put("executeSleep", "1000");
        try (final DruidDataSource dataSource = new DruidDataSource()) {
            dataSource.setUrl("jdbc:mock:xxx");
            dataSource.setDriver(driver);
            dataSource.setInitialSize(1);
            dataSource.setMinIdle(1);
            dataSource.setFilters("timeout");
            dataSource.setMaxConnect(5000);
            dataSource.setConnectProperties(connectionProperties);
            assertNotNull(dataSource.getConnection());
        }
    }
}
