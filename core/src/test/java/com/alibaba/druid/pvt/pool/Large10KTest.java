package com.alibaba.druid.pvt.pool;

import java.lang.management.ManagementFactory;
import java.sql.Connection;

import junit.framework.TestCase;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.util.JdbcUtils;

public class Large10KTest extends TestCase {
    private DruidDataSource[] dataSources;

    protected void setUp() throws Exception {
        long xmx = ManagementFactory.getMemoryMXBean().getHeapMemoryUsage().getMax() / (1000 * 1000); // m

        final int dataSourceCount;

        if (xmx <= 256) {
            dataSourceCount = 1024 * 1;
        } else if (xmx <= 512) {
            dataSourceCount = 1024 * 2;
        } else if (xmx <= 1024) {
            dataSourceCount = 1024 * 4;
        } else if (xmx <= 2048) {
            dataSourceCount = 1024 * 8;
        } else {
            dataSourceCount = 1024 * 16;
        }

        dataSources = new DruidDataSource[dataSourceCount];

        for (int i = 0; i < dataSources.length; ++i) {
            DruidDataSource dataSource = new DruidDataSource();
            dataSource.setUrl("jdbc:mock:xxx");
            dataSource.setTestOnBorrow(false);
            dataSource.setTestWhileIdle(false);

            dataSources[i] = dataSource;
        }
    }

    protected void tearDown() throws Exception {
        for (int i = 0; i < dataSources.length; ++i) {
            JdbcUtils.close(dataSources[i]);
        }
    }

    public void test_large() throws Exception {
        Connection[] connections = new Connection[dataSources.length * 8];
        for (int i = 0; i < dataSources.length; ++i) {
            for (int j = 0; j < 8; ++j) {
                connections[i * 8 + j] = dataSources[i].getConnection();
            }
        }

        for (int i = 0; i < dataSources.length; ++i) {
            for (int j = 0; j < 8; ++j) {
                connections[i * 8 + j].close();
            }
        }
    }
}
