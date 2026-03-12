package com.alibaba.druid.pvt.pool;

import com.alibaba.druid.mock.MockDriver;
import com.alibaba.druid.mock.MockStatementBase;
import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.vendor.MySqlExceptionSorter;
import com.alibaba.druid.util.JdbcUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;
import java.util.concurrent.CountDownLatch;

import static org.junit.jupiter.api.Assertions.*;

public class DiscardTest {
    private DruidDataSource dataSource;

    private MockDriver driver;

    private volatile boolean failed;

    @BeforeEach
    protected void setUp() throws Exception {
        driver = new MockDriver() {
            public ResultSet executeQuery(MockStatementBase stmt, String sql) throws SQLException {
                if (failed) {
                    throw new SQLException("", "", 1040);
                }
                return super.executeQuery(stmt, sql);
            }

            public Connection connect(String url, Properties info) throws SQLException {
                while (failed) {
                    try {
                        Thread.sleep(1);
                    } catch (InterruptedException e) {
                        throw new SQLException(e.getMessage(), e);
                    }
                }

                return super.connect(url, info);
            }
        };

        dataSource = new DruidDataSource();
        dataSource.setUrl("jdbc:mock:xxx");
        dataSource.setTestOnBorrow(false);
        dataSource.setDriver(driver);
        dataSource.setDbType("mysql");
        dataSource.setMinIdle(1);
        dataSource.setMaxActive(1);
        dataSource.setFilters("log4j");
        dataSource.setExceptionSorter(new MySqlExceptionSorter());
    }

    @AfterEach
    protected void tearDown() throws Exception {
        dataSource.close();
    }

    @Test
    public void test_db_fail() throws Exception {
        exec();

        final int THREAD_COUNT = 10;
        final CountDownLatch endLatch = new CountDownLatch(THREAD_COUNT);

        Thread[] threads = new Thread[THREAD_COUNT];

        {
            Exception error = null;

            Connection conn = dataSource.getConnection();
            Statement stmt = conn.createStatement();

            for (int i = 0; i < THREAD_COUNT; ++i) {
                threads[i] = new Thread() {
                    public void run() {
                        try {
                            exec();
                        } finally {
                            endLatch.countDown();
                        }
                    }
                };
                threads[i].start();
            }

            this.failed = true;
            try {
                ResultSet rs = stmt.executeQuery("select 1");
                rs.close();
            } catch (SQLException e) {
                error = e;
            }

            stmt.close();
            conn.close();

            assertNotNull(error);
        }

        this.failed = false;

        endLatch.await();
    }

    private void exec() {
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;

        try {
            conn = dataSource.getConnection();
            stmt = conn.createStatement();
            rs = stmt.executeQuery("select 1");
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            JdbcUtils.close(rs);
            JdbcUtils.close(stmt);
            JdbcUtils.close(conn);
        }
    }
}
