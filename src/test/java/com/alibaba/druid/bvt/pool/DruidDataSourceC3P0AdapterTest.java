package com.alibaba.druid.bvt.pool;

import java.sql.Connection;
import java.sql.SQLFeatureNotSupportedException;

import junit.framework.TestCase;

import org.junit.Assert;

import com.alibaba.druid.mock.MockDriver;
import com.alibaba.druid.pool.DruidDataSourceC3P0Adapter;

public class DruidDataSourceC3P0AdapterTest extends TestCase {

    private DruidDataSourceC3P0Adapter dataSource;

    protected void setUp() throws Exception {
        dataSource = new DruidDataSourceC3P0Adapter();
        dataSource.setJdbcUrl("jdbc:mock:xxx");
        dataSource.setInitialPoolSize(1);
    }

    protected void tearDown() throws Exception {
        dataSource.close();
    }

    public void test_conn_1() throws Exception {
        Connection conn = dataSource.getConnection(null, null);
        conn.close();
    }

    public void test_getDriverClass() throws Exception {
        Assert.assertNull(dataSource.getDriverClass());

        Connection conn = dataSource.getConnection();
        conn.close();

        Assert.assertEquals(MockDriver.class.getName(), dataSource.getDriverClass());
        Assert.assertEquals(MockDriver.instance, dataSource.getDriver());
    }

    public void test_getJdbcUrl() throws Exception {
        Assert.assertEquals("jdbc:mock:xxx", dataSource.getJdbcUrl());
    }

    public void test_getParentLogger_err() throws Exception {
        Exception error = null;
        try {
            dataSource.getParentLogger();
        } catch (SQLFeatureNotSupportedException e) {
            error = e;
        }
        Assert.assertNotNull(error);
    }

    public void test_propertyCycle() throws Exception {
        dataSource.setPropertyCycle(3);
        Assert.assertEquals(3, dataSource.getPropertyCycle());
    }

    public void test_acquireIncrement() throws Exception {
        dataSource.setAcquireIncrement(4);
        Assert.assertEquals(4, dataSource.getAcquireIncrement());
    }

    public void test_overrideDefaultPassword() throws Exception {
        dataSource.setOverrideDefaultPassword("x2");
        Assert.assertEquals("x2", dataSource.getOverrideDefaultPassword());
    }

    public void test_overrideDefaultUser() throws Exception {
        dataSource.setOverrideDefaultUser("x1");
        Assert.assertEquals("x1", dataSource.getOverrideDefaultUser());
    }

    public void test_factoryClassLocation() throws Exception {
        dataSource.setFactoryClassLocation("x3");
        Assert.assertEquals("x3", dataSource.getFactoryClassLocation());
    }

    public void test_maxConnectionAge() throws Exception {
        dataSource.setMaxConnectionAge(123);
        Assert.assertEquals(123, dataSource.getMaxConnectionAge());
    }

    public void test_connectionCustomizerClassName() throws Exception {
        dataSource.setConnectionCustomizerClassName("x4");
        Assert.assertEquals("x4", dataSource.getConnectionCustomizerClassName());
    }

    public void test_maxIdleTimeExcessConnections() throws Exception {
        dataSource.setMaxIdleTimeExcessConnections(101);
        Assert.assertEquals(101, dataSource.getMaxIdleTimeExcessConnections());
    }

    public void test_maxAdministrativeTaskTime() throws Exception {
        dataSource.setMaxAdministrativeTaskTime(102);
        Assert.assertEquals(102, dataSource.getMaxAdministrativeTaskTime());
    }

    public void test_userOverridesAsString() throws Exception {
        dataSource.setUserOverridesAsString("x5");
        Assert.assertEquals("x5", dataSource.getUserOverridesAsString());
    }

    public void test_usesTraditionalReflectiveProxies() throws Exception {
        dataSource.setUsesTraditionalReflectiveProxies(true);
        Assert.assertEquals(true, dataSource.isUsesTraditionalReflectiveProxies());
    }

    public void test_forceIgnoreUnresolvedTransactions() throws Exception {
        dataSource.setForceIgnoreUnresolvedTransactions(true);
        Assert.assertEquals(true, dataSource.isForceIgnoreUnresolvedTransactions());
    }

    public void test_automaticTestTable() throws Exception {
        dataSource.setAutomaticTestTable("x6");
        Assert.assertEquals("x6", dataSource.getAutomaticTestTable());
    }

    public void test_connectionTesterClassName() throws Exception {
        dataSource.setConnectionTesterClassName("x6");
        Assert.assertEquals("x6", dataSource.getConnectionTesterClassName());
    }
}
