package com.alibaba.druid.pool.dbcp;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import junit.framework.TestCase;

import org.apache.commons.dbcp.BasicDataSource;

import com.alibaba.druid.mock.MockConnection;
import com.alibaba.druid.mock.MockDriver;
import com.alibaba.druid.pool.DruidDataSource;

public class TestIdle extends TestCase {

    public void test_idle() throws Exception {
        MockDriver driver = MockDriver.instance;

//        BasicDataSource dataSource = new BasicDataSource();
        DruidDataSource dataSource = new DruidDataSource();
        dataSource.setUrl("jdbc:mock:xxx");
        dataSource.setDriverClassName("com.alibaba.druid.mock.MockDriver");
        dataSource.setInitialSize(1);
        dataSource.setMaxActive(5);
        dataSource.setMaxIdle(5);
        dataSource.setMinIdle(0);
        dataSource.setMinEvictableIdleTimeMillis(5000 * 1);
        dataSource.setTimeBetweenEvictionRunsMillis(500);
        dataSource.setTestWhileIdle(true);
        dataSource.setTestOnBorrow(false);
        dataSource.setValidationQuery("SELECT 1");

        {
            Connection conn = dataSource.getConnection();

            // Assert.assertEquals(dataSource.getInitialSize(), driver.getConnections().size());
            System.out.println("raw size : " + driver.getConnections().size());

            PreparedStatement stmt = conn.prepareStatement("SELECT 1");
            ResultSet rs = stmt.executeQuery();
            rs.close();
            stmt.close();

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
