package com.alibaba.druid.bvt.pool.exception;

import com.alibaba.druid.mock.MockConnection;
import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.vendor.OracleExceptionSorter;
import com.alibaba.druid.stat.DruidDataSourceStatManager;
import com.alibaba.druid.stat.JdbcStatManager;
import com.alibaba.druid.test.util.OracleMockDriver;
import com.alibaba.druid.util.JdbcUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.concurrent.CountDownLatch;

import static org.junit.jupiter.api.Assertions.*;

public class OracleExceptionSorterTest_concurrent {
    private DruidDataSource dataSource;

    @BeforeEach
    protected void setUp() throws Exception {
        JdbcStatManager.getInstance().reset();
        dataSource = new DruidDataSource();

        dataSource.setExceptionSorter(new OracleExceptionSorter());

        dataSource.setDriver(new OracleMockDriver());
        dataSource.setUrl("jdbc:mock:xxx");
        dataSource.setPoolPreparedStatements(true);
        dataSource.setMaxOpenPreparedStatements(100);
        dataSource.setMaxActive(1);
        dataSource.setMaxWait(1000 * 100);
    }

        @AfterEach
    protected void tearDown() throws Exception {
        JdbcUtils.close(dataSource);
        DruidDataSourceStatManager.clear();
    }

    @Test
    public void test_connect() throws Exception {
        final CountDownLatch latch_0 = new CountDownLatch(1);

        Thread errorThread = new Thread() {
            public void run() {
                try {
                    Connection conn = dataSource.getConnection();

                    latch_0.countDown();

                    MockConnection mockConn = conn.unwrap(MockConnection.class);
                    assertNotNull(mockConn);

                    SQLException exception = new SQLException("xx", "xxx", 28);
                    mockConn.setError(exception);

                    try {
                        conn.createStatement();
                    } catch (SQLException ex) {
                        // ex.printStackTrace();
                    }
                    conn.close();
                } catch (Exception error) {
                    error.printStackTrace();
                }
            }
        };
        errorThread.start();

        final CountDownLatch workLatch = new CountDownLatch(2);
        final CountDownLatch workCompleteLatch = new CountDownLatch(2);
        for (int i = 0; i < 2; ++i) {
            Thread thread = new Thread() {
                public void run() {
                    try {
                        for (int i = 0; i < 1000; ++i) {
                            workLatch.countDown();
                            Connection conn = dataSource.getConnection();
                            conn.close();
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    } finally {
                        workCompleteLatch.countDown();
                    }
                }
            };
            thread.start();
        }
        workLatch.await();

        latch_0.countDown();

        workCompleteLatch.await();

        assertEquals(2001, dataSource.getConnectCount());
    }
}
