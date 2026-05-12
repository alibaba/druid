package com.alibaba.druid.bvt.pool;

import com.alibaba.druid.pool.DruidDataSource;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.PreparedStatement;

import static org.junit.jupiter.api.Assertions.*;

public class DruidDataSourceTest1 {
    private DruidDataSource dataSource;

    @BeforeEach
    protected void setUp() throws Exception {
        dataSource = new DruidDataSource();
        dataSource.setUrl("jdbc:mock:xxx");
        dataSource.setTestOnBorrow(false);
    }

    @AfterEach
    protected void tearDown() throws Exception {
        dataSource.close();
    }

    @Test
    public void test_oracle() throws Exception {
        dataSource.setOracle(true);

        dataSource.init();

        Exception error = null;
        try {
            dataSource.setOracle(false);
        } catch (IllegalStateException e) {
            error = e;
        }
        assertNotNull(error);
    }

    @Test
    public void test_transactionQueryTimeout() throws Exception {
        dataSource.setTransactionQueryTimeout(123456);

        assertEquals(123456, dataSource.getTransactionQueryTimeout());
    }

    @Test
    public void test_dupCloseLogEnable() throws Exception {
        assertFalse(dataSource.isDupCloseLogEnable());

        dataSource.setDupCloseLogEnable(true);

        assertTrue(dataSource.isDupCloseLogEnable());
    }

    @Test
    public void test_getClosedPreparedStatementCount() throws Exception {
        assertEquals(0, dataSource.getClosedPreparedStatementCount());
        assertEquals(0, dataSource.getPreparedStatementCount());

        Connection conn = dataSource.getConnection();
        PreparedStatement stmt = conn.prepareStatement("select 1");
        stmt.close();

        assertEquals(1, dataSource.getPreparedStatementCount());
        assertEquals(1, dataSource.getClosedPreparedStatementCount());
    }

    @Test
    public void test_getDriverMajorVersion() throws Exception {
        assertEquals(-1, dataSource.getDriverMajorVersion());
        dataSource.init();
        assertEquals(0, dataSource.getDriverMajorVersion());
    }

    @Test
    public void test_getDriverMinorVersion() throws Exception {
        assertEquals(-1, dataSource.getDriverMinorVersion());
        dataSource.init();
        assertEquals(0, dataSource.getDriverMinorVersion());
    }

    @Test
    public void test_getExceptionSorterClassName() throws Exception {
        assertNull(dataSource.getExceptionSorterClassName());
    }
}
