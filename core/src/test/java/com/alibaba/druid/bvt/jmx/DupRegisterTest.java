package com.alibaba.druid.bvt.jmx;

import com.alibaba.druid.pool.DruidDataSource;
import junit.framework.TestCase;

import javax.management.MBeanServer;

import java.lang.management.ManagementFactory;

public class DupRegisterTest extends TestCase {
    private DruidDataSource dataSource;

    public void test_0() throws Exception {
        dataSource.init();

        MBeanServer mbeanServer = ManagementFactory.getPlatformMBeanServer();
        mbeanServer.registerMBean(dataSource, dataSource.getObjectName());
        assertTrue(mbeanServer.isRegistered(dataSource.getObjectName()));
    }

    protected void setUp() throws Exception {
        dataSource = new DruidDataSource();
        dataSource.setUrl("jdbc:mock:xxx");
        dataSource.setMinEvictableIdleTimeMillis(10);
        dataSource.setTimeBetweenEvictionRunsMillis(10);
    }

    protected void tearDown() throws Exception {
        dataSource.close();
    }
}
