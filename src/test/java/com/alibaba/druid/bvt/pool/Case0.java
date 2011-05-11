/*
 * Copyright 2011 Alibaba.com All right reserved. This software is the confidential and proprietary information of
 * Alibaba.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only
 * in accordance with the terms of the license agreement you entered into with Alibaba.com.
 */
package com.alibaba.druid.bvt.pool;

import java.sql.Connection;

import junit.framework.Assert;
import junit.framework.TestCase;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.util.JMXUtils;

public class Case0 extends TestCase {

    private String jdbcUrl;
    private String user;
    private String password;
    private String driverClass;
    private int    initialSize = 10;
    private int    minPoolSize = 1;
    private int    maxPoolSize = 2;
    private int    maxActive   = 2;

    protected void setUp() throws Exception {
        jdbcUrl = "jdbc:fake:dragoon_v25masterdb";
        user = "dragoon25";
        password = "dragoon25";
        driverClass = "com.alibaba.druid.mock.MockDriver";
    }

    public void test_singleThread() throws Exception {
        final DruidDataSource dataSource = new DruidDataSource();

        JMXUtils.register("com.alibaba.druid:type=DruidDataSource", dataSource);

        Class.forName("com.alibaba.druid.mock.MockDriver");

        dataSource.setInitialSize(initialSize);
        dataSource.setMaxActive(maxActive);
        dataSource.setMinIdle(minPoolSize);
        dataSource.setMaxIdle(maxPoolSize);
        dataSource.setPoolPreparedStatements(true);
        dataSource.setDriverClass(driverClass);
        dataSource.setUrl(jdbcUrl);
        dataSource.setPoolPreparedStatements(true);
        dataSource.setUser(user);
        dataSource.setPassword(password);

        final int LOOP_COUNT = 1000 * 1000;

        Assert.assertEquals(0, dataSource.getCreateCount());
        Assert.assertEquals(0, dataSource.getDestroyCount());
        Assert.assertEquals(0, dataSource.getPoolingSize());

        for (int i = 0; i < LOOP_COUNT; ++i) {
            Connection conn = dataSource.getConnection();

            Assert.assertEquals(initialSize, dataSource.getCreateCount());

            Assert.assertEquals(i + 1, dataSource.getConnectCount());
            Assert.assertEquals(1, dataSource.getActiveCount());
            Assert.assertEquals(i, dataSource.getCloseCount());
            Assert.assertEquals(0, dataSource.getConnectErrorCount());
            Assert.assertEquals(initialSize - 1, dataSource.getPoolingSize());
            Assert.assertEquals(i, dataSource.getRecycleCount());

            conn.close();

            Assert.assertEquals(i + 1, dataSource.getConnectCount());
            Assert.assertEquals(0, dataSource.getActiveCount());
            Assert.assertEquals(i + 1, dataSource.getCloseCount());
            Assert.assertEquals(0, dataSource.getConnectErrorCount());
            Assert.assertEquals(initialSize, dataSource.getPoolingSize());
            Assert.assertEquals(i + 1, dataSource.getRecycleCount());
        }

        Assert.assertEquals(initialSize, dataSource.getCreateCount());
        Assert.assertEquals(0, dataSource.getDestroyCount());

        dataSource.close();
        Assert.assertEquals(dataSource.getCreateCount(), dataSource.getDestroyCount());
    }
}
