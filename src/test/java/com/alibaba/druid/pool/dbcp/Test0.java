package com.alibaba.druid.pool.dbcp;

import java.sql.Connection;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.apache.commons.dbcp.BasicDataSource;

import com.alibaba.druid.mock.MockConnection;
import com.alibaba.druid.mock.MockDriver;

public class Test0 extends TestCase {

    public void test_idle() throws Exception {
        MockDriver driver = MockDriver.instance;

        BasicDataSource dataSource = new BasicDataSource();
        dataSource.setUrl("jdbc:mock:xxx");
        dataSource.setDriverClassName("com.alibaba.druid.mock.MockDriver");
        dataSource.setInitialSize(0);
        dataSource.setMaxActive(4);
        dataSource.setMaxIdle(4);
        dataSource.setMinIdle(1);
        dataSource.setMinEvictableIdleTimeMillis(5000 * 1);
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

        dataSource.close();
    }
}
