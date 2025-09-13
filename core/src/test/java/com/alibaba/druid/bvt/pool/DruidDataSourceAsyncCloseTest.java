package com.alibaba.druid.bvt.pool;

import com.alibaba.druid.mock.MockConnectionClosedException;
import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.DruidPooledConnection;
import com.alibaba.druid.util.JdbcUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.sql.SQLException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.Assert.assertEquals;

public class DruidDataSourceAsyncCloseTest {
    protected DruidDataSource dataSource;
    protected ExecutorService executor;

    @Before
    public void setUp() {
        dataSource = new DruidDataSource();
        dataSource.setUrl("jdbc:mock:xxx");

        executor = Executors.newFixedThreadPool(2);
    }

    @After
    public void tearDown() {
        JdbcUtils.close(dataSource);
        executor.shutdownNow();
    }

    @Test
    public void test() throws Exception {
        int count = 1024 * 16;
        final CountDownLatch latch = new CountDownLatch(count);

        for (int i = 0; i < count; i++) {
            DruidPooledConnection connection = dataSource.getConnection();
            Runnable task = () -> {
                try {
                    SQLException ex = new MockConnectionClosedException();
                    connection.handleException(ex);
                } catch (SQLException ignored) {
                    // ignored
                } finally {
                    JdbcUtils.close(connection);
                    latch.countDown();
                }
            };
            executor.submit(task);
        }
        latch.await();

        assertEquals(0, dataSource.getActiveCount());
    }
}
