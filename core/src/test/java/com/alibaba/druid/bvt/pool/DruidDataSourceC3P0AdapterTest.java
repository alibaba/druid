package com.alibaba.druid.bvt.pool;

import com.alibaba.druid.mock.MockDriver;
import com.alibaba.druid.pool.DruidDataSourceC3P0Adapter;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.SQLFeatureNotSupportedException;

import static org.junit.jupiter.api.Assertions.*;

public class DruidDataSourceC3P0AdapterTest {
    private DruidDataSourceC3P0Adapter dataSource;

    @BeforeEach
    protected void setUp() throws Exception {
        dataSource = new DruidDataSourceC3P0Adapter();
        dataSource.setJdbcUrl("jdbc:mock:xxx");
        dataSource.setInitialPoolSize(1);
    }

    @AfterEach
    protected void tearDown() throws Exception {
        dataSource.close();
    }

    @Test
    public void test_conn_1() throws Exception {
        Connection conn = dataSource.getConnection(null, null);
        conn.close();
    }

    @Test
    public void test_getDriverClass() throws Exception {
        assertNull(dataSource.getDriverClass());

        Connection conn = dataSource.getConnection();
        conn.close();

        assertEquals(MockDriver.class.getName(), dataSource.getDriverClass());
        assertEquals(MockDriver.instance, dataSource.getDriver());
    }

    @Test
    public void test_getJdbcUrl() throws Exception {
        assertEquals("jdbc:mock:xxx", dataSource.getJdbcUrl());
    }

    @Test
    public void test_getParentLogger_err() throws Exception {
        Exception error = null;
        try {
            dataSource.getParentLogger();
        } catch (SQLFeatureNotSupportedException e) {
            error = e;
        }
        assertNotNull(error);
    }

    @Test
    public void test_propertyCycle() throws Exception {
        dataSource.setPropertyCycle(3);
        assertEquals(3, dataSource.getPropertyCycle());
    }

    @Test
    public void test_acquireIncrement() throws Exception {
        dataSource.setAcquireIncrement(4);
        assertEquals(4, dataSource.getAcquireIncrement());
    }

    @Test
    public void test_overrideDefaultPassword() throws Exception {
        dataSource.setOverrideDefaultPassword("x2");
        assertEquals("x2", dataSource.getOverrideDefaultPassword());
    }

    @Test
    public void test_overrideDefaultUser() throws Exception {
        dataSource.setOverrideDefaultUser("x1");
        assertEquals("x1", dataSource.getOverrideDefaultUser());
    }

    @Test
    public void test_factoryClassLocation() throws Exception {
        dataSource.setFactoryClassLocation("x3");
        assertEquals("x3", dataSource.getFactoryClassLocation());
    }

    @Test
    public void test_maxConnectionAge() throws Exception {
        dataSource.setMaxConnectionAge(123);
        assertEquals(123, dataSource.getMaxConnectionAge());
    }

    @Test
    public void test_connectionCustomizerClassName() throws Exception {
        dataSource.setConnectionCustomizerClassName("x4");
        assertEquals("x4", dataSource.getConnectionCustomizerClassName());
    }

    @Test
    public void test_maxIdleTimeExcessConnections() throws Exception {
        dataSource.setMaxIdleTimeExcessConnections(101);
        assertEquals(101, dataSource.getMaxIdleTimeExcessConnections());
    }

    @Test
    public void test_maxAdministrativeTaskTime() throws Exception {
        dataSource.setMaxAdministrativeTaskTime(102);
        assertEquals(102, dataSource.getMaxAdministrativeTaskTime());
    }

    @Test
    public void test_userOverridesAsString() throws Exception {
        dataSource.setUserOverridesAsString("x5");
        assertEquals("x5", dataSource.getUserOverridesAsString());
    }

    @Test
    public void test_usesTraditionalReflectiveProxies() throws Exception {
        dataSource.setUsesTraditionalReflectiveProxies(true);
        assertEquals(true, dataSource.isUsesTraditionalReflectiveProxies());
    }

    @Test
    public void test_forceIgnoreUnresolvedTransactions() throws Exception {
        dataSource.setForceIgnoreUnresolvedTransactions(true);
        assertEquals(true, dataSource.isForceIgnoreUnresolvedTransactions());
    }

    @Test
    public void test_automaticTestTable() throws Exception {
        dataSource.setAutomaticTestTable("x6");
        assertEquals("x6", dataSource.getAutomaticTestTable());
    }

    @Test
    public void test_connectionTesterClassName() throws Exception {
        dataSource.setConnectionTesterClassName("x6");
        assertEquals("x6", dataSource.getConnectionTesterClassName());
    }
}
