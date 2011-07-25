package com.alibaba.druid.bvt.pool;

import java.sql.Connection;

import junit.framework.Assert;
import junit.framework.TestCase;

import com.alibaba.druid.mock.MockConnection;
import com.alibaba.druid.mock.MockDriver;
import com.alibaba.druid.pool.DruidDataSource;

public class TestIdle extends TestCase {

    public void test_idle() throws Exception {
        MockDriver driver = new MockDriver();

        DruidDataSource dataSource = new DruidDataSource();
        dataSource.setUrl("jdbc:mock:xxx");
        dataSource.setDriver(driver);
        dataSource.setInitialSize(2);
        dataSource.setMaxActive(4);
        dataSource.setMaxIdle(4);
        dataSource.setMinIdle(1);
        dataSource.setMinEvictableIdleTimeMillis(50 * 1);
        dataSource.setTimeBetweenEvictionRunsMillis(10);
        dataSource.setTestWhileIdle(true);
        dataSource.setTestOnBorrow(false);
        dataSource.setValidationQuery("SELECT 1");

        {
            Connection conn = dataSource.getConnection();

            Assert.assertEquals(dataSource.getInitialSize(), driver.getConnections().size());

            conn.close();
            Assert.assertEquals(dataSource.getInitialSize(), driver.getConnections().size());
        }

        {
            int count = 4;
            Connection[] connections = new Connection[4];
            for (int i = 0; i < count; ++i) {
                connections[i] = dataSource.getConnection();
            }
            Assert.assertEquals(4, driver.getConnections().size());
            for (int i = 0; i < count; ++i) {
                connections[i].close();
            }
            Assert.assertEquals(4, driver.getConnections().size());

            Thread.sleep(dataSource.getMinEvictableIdleTimeMillis() * 2);
            Assert.assertEquals(dataSource.getMinIdle(), driver.getConnections().size());
        }

        System.out.println("----------raw close all connection");
        for (MockConnection rawConn : driver.getConnections()) {
            rawConn.close();
        }

        Thread.sleep(dataSource.getMinEvictableIdleTimeMillis() * 2);
        Assert.assertEquals(0, driver.getConnections().size());
        Assert.assertEquals(0, dataSource.getPoolingCount());
        {
            Connection conn = dataSource.getConnection();
            Assert.assertEquals(dataSource.getMinIdle(), driver.getConnections().size());
            conn.close();
            Assert.assertEquals(dataSource.getMinIdle(), driver.getConnections().size());
        }

        dataSource.close();
    }
}
