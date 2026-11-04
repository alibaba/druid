package com.alibaba.druid.bvt.pool;

import com.alibaba.druid.mock.MockDriver;
import com.alibaba.druid.pool.DruidDataSource;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;
import java.util.function.BooleanSupplier;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Regression tests for the dedicated creator thread ({@code CreateConnectionThread}).
 *
 * <p>When {@code createScheduler} is null this thread is the only thing that replenishes the
 * pool, so it must not be killed by a spurious interrupt.
 * See https://github.com/alibaba/druid/issues/6741
 */
public class DruidDataSourceCreatorThreadTest {
    private static final String CREATOR_THREAD_NAME_PREFIX = "Druid-ConnectionPool-Create-";

    @Test
    public void test_spuriousInterrupt_doesNotDisableConnectionCreation() throws Exception {
        DruidDataSource dataSource = new DruidDataSource();
        try {
            dataSource.setUrl("jdbc:mock:creator-thread-spurious-interrupt");
            dataSource.setInitialSize(1);
            dataSource.setMaxActive(5);
            dataSource.setMaxWait(2000);
            dataSource.setTestOnBorrow(false);
            dataSource.setTestWhileIdle(false);
            dataSource.init();

            Thread creatorThread = findCreatorThread(dataSource);
            assertNotNull(creatorThread, "creator thread must exist after init");

            // A JDBC driver connect timeout may interrupt the thread that is opening the physical
            // connection and leave the interrupt flag set. That must not disable the pool.
            creatorThread.interrupt();

            Connection first = dataSource.getConnection();
            try {
                // The pool holds a single connection, so acquiring a second one requires the
                // creator thread. With a dead creator thread this blocks until maxWait.
                Connection second = dataSource.getConnection();
                assertNotNull(second);
                second.close();
            } finally {
                first.close();
            }
        } finally {
            dataSource.close();
        }
    }

    @Test
    public void test_breakAfterAcquireFailure_stillStopsTheCreatorThread() throws Exception {
        DruidDataSource dataSource = new DruidDataSource();
        try {
            dataSource.setUrl("jdbc:mock:creator-thread-break-after-acquire-failure");
            dataSource.setDriver(new FailingMockDriver());
            dataSource.setInitialSize(0);
            dataSource.setMaxActive(5);
            dataSource.setMaxWait(1000);
            dataSource.setConnectionErrorRetryAttempts(0);
            dataSource.setTimeBetweenConnectErrorMillis(50);
            dataSource.setBreakAfterAcquireFailure(true);
            dataSource.init();

            // A waiting thread is required: the creator thread only opens connections on demand.
            Thread getter = new Thread(() -> {
                try {
                    dataSource.getConnection().close();
                } catch (Exception ignored) {
                    // expected: the datasource gave up creating connections
                }
            });
            getter.start();

            assertTrue(waitUntil(() -> findCreatorThread(dataSource) == null, 5000),
                    "breakAfterAcquireFailure must not be turned into an endless restart loop");
            getter.join(5000);
        } finally {
            dataSource.close();
        }
    }

    @Test
    public void test_close_stopsTheCreatorThread() throws Exception {
        DruidDataSource dataSource = new DruidDataSource();
        dataSource.setUrl("jdbc:mock:creator-thread-close");
        dataSource.setInitialSize(1);
        dataSource.setTestOnBorrow(false);
        dataSource.init();

        assertNotNull(findCreatorThread(dataSource), "creator thread must exist after init");

        dataSource.close();

        assertTrue(waitUntil(() -> findCreatorThread(dataSource) == null, 5000),
                "close() must leave no creator thread behind");
    }

    private static Thread findCreatorThread(DruidDataSource dataSource) {
        String name = CREATOR_THREAD_NAME_PREFIX + System.identityHashCode(dataSource);
        for (Thread thread : Thread.getAllStackTraces().keySet()) {
            if (name.equals(thread.getName()) && thread.isAlive()) {
                return thread;
            }
        }
        return null;
    }

    private static boolean waitUntil(BooleanSupplier condition, long timeoutMillis) throws InterruptedException {
        long deadline = System.currentTimeMillis() + timeoutMillis;
        while (System.currentTimeMillis() < deadline) {
            if (condition.getAsBoolean()) {
                return true;
            }
            Thread.sleep(20);
        }
        return condition.getAsBoolean();
    }

    static class FailingMockDriver extends MockDriver {
        @Override
        public Connection connect(String url, Properties info) throws SQLException {
            throw new SQLException("simulated connect failure", "08006", 17002);
        }
    }
}
