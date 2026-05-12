package com.alibaba.druid.bvt.pool;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.DruidPooledConnection;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.Savepoint;

import static org.junit.jupiter.api.Assertions.*;

public class DruidPooledConnectionTest {
    private DruidDataSource dataSource;

    @BeforeEach
    protected void setUp() throws Exception {
        dataSource = new DruidDataSource();
        dataSource.setUrl("jdbc:mock:xxx");
        dataSource.setTestOnBorrow(false);
        dataSource.setFilters("stat");
    }

    @AfterEach
    protected void tearDown() throws Exception {
        dataSource.close();
    }

    @Test
    public void test_rollback() throws Exception {
        Connection conn = dataSource.getConnection();
        conn.rollback();

        Savepoint savepoint = conn.setSavepoint("xx");
        conn.rollback(savepoint);

        conn.close();
    }

    @Test
    public void test_rollback_1() throws Exception {
        Connection conn = dataSource.getConnection();
        conn.setAutoCommit(false);

        conn.close();
        conn.rollback();
        conn.rollback(null);
    }

    @Test
    public void test_getOwnerThread() throws Exception {
        DruidPooledConnection conn = (DruidPooledConnection) dataSource.getConnection();

        assertEquals(Thread.currentThread(), conn.getOwnerThread());

        conn.close();
    }

    @Test
    public void test_isDiable() throws Exception {
        DruidPooledConnection conn = (DruidPooledConnection) dataSource.getConnection();

        assertEquals(false, conn.isDisable());

        conn.close();

        assertEquals(true, conn.isDisable());
    }

    @Test
    public void test_dupClose() throws Exception {
        DruidPooledConnection conn = (DruidPooledConnection) dataSource.getConnection();

        conn.close();
        conn.close();
    }

    @Test
    public void test_disable() throws Exception {
        assertEquals(0, dataSource.getPoolingCount());
        assertEquals(0, dataSource.getActiveCount());

        DruidPooledConnection conn = (DruidPooledConnection) dataSource.getConnection();
        conn.disable();
        assertEquals(true, conn.isDisable());

        assertEquals(1, dataSource.getActiveCount());

        conn.close();

        assertEquals(0, dataSource.getPoolingCount());
        assertEquals(1, dataSource.getActiveCount());
    }
}
