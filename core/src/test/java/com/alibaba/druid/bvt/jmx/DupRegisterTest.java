package com.alibaba.druid.bvt.jmx;

import com.alibaba.druid.pool.DruidDataSource;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.management.MBeanServer;

import java.lang.management.ManagementFactory;

import static org.junit.jupiter.api.Assertions.*;

public class DupRegisterTest {
    private DruidDataSource dataSource;

    @Test
    public void test_0() throws Exception {
        dataSource.init();

        MBeanServer mbeanServer = ManagementFactory.getPlatformMBeanServer();
        mbeanServer.registerMBean(dataSource, dataSource.getObjectName());
        assertTrue(mbeanServer.isRegistered(dataSource.getObjectName()));
    }

    @BeforeEach
    protected void setUp() throws Exception {
        dataSource = new DruidDataSource();
        dataSource.setUrl("jdbc:mock:xxx");
        dataSource.setMinEvictableIdleTimeMillis(10);
        dataSource.setTimeBetweenEvictionRunsMillis(10);
    }

    @AfterEach
    protected void tearDown() throws Exception {
        dataSource.close();
    }
}
