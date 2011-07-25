package com.alibaba.druid.pool.dbcp;

import java.sql.Connection;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.apache.commons.dbcp.BasicDataSource;

import com.alibaba.druid.mock.MockConnection;
import com.alibaba.druid.mock.MockDriver;

public class TestIdle extends TestCase {

    public void test_idle() throws Exception {
        MockDriver driver = MockDriver.instance;

        BasicDataSource dataSource = new BasicDataSource();
        dataSource.setUrl("jdbc:mock:xxx");
        dataSource.setDriverClassName("com.alibaba.druid.mock.MockDriver");
        dataSource.setInitialSize(0);
        dataSource.setMaxActive(4);
        dataSource.setMaxIdle(4);
        dataSource.setMinIdle(1);
        dataSource.setMinEvictableIdleTimeMillis(50 * 1);
        dataSource.setTimeBetweenEvictionRunsMillis(10);
        dataSource.setTestWhileIdle(false);
        dataSource.setTestOnBorrow(false);
        dataSource.setValidationQuery("SELECT 1");

        {
            Connection conn = dataSource.getConnection();

            // Assert.assertEquals(dataSource.getInitialSize(), driver.getConnections().size());
            System.out.println("raw size : " + driver.getConnections().size());

            conn.close();
            System.out.println("raw size : " + driver.getConnections().size());
        }
        
        {
            Connection conn = dataSource.getConnection();

            // Assert.assertEquals(dataSource.getInitialSize(), driver.getConnections().size());
            System.out.println("raw size : " + driver.getConnections().size());

            conn.close();
            System.out.println("raw size : " + driver.getConnections().size());
        }

        {
            int count = 4;
            Connection[] connections = new Connection[4];
            for (int i = 0; i < count; ++i) {
                connections[i] = dataSource.getConnection();
            }
            System.out.println("raw size : " + driver.getConnections().size());
            for (int i = 0; i < count; ++i) {
                connections[i].close();
            }
            System.out.println("raw size : " + driver.getConnections().size());

            System.out.println("----------sleep for evict");    
            Thread.sleep(dataSource.getMinEvictableIdleTimeMillis() * 2);
            System.out.println("raw size : " + driver.getConnections().size());
        }

        System.out.println("----------raw close all connection");
        for (MockConnection rawConn : driver.getConnections()) {
            rawConn.close();
        }

        Thread.sleep(dataSource.getMinEvictableIdleTimeMillis() * 2);
        System.out.println("raw size : " + driver.getConnections().size());
        {
            Connection conn = dataSource.getConnection();
            System.out.println("raw size : " + driver.getConnections().size());
            conn.close();
            System.out.println("raw size : " + driver.getConnections().size());
        }

        dataSource.close();
    }
}
