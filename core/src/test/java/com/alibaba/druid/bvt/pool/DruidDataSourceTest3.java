package com.alibaba.druid.bvt.pool;

import com.alibaba.druid.filter.FilterAdapter;
import com.alibaba.druid.filter.FilterChain;
import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.proxy.jdbc.ConnectionProxy;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.Properties;
import java.util.concurrent.CountDownLatch;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 这个场景测试并发初始化
 *
 * @author wenshao [szujobs@hotmail.com]
 */
public class DruidDataSourceTest3 {
    private DruidDataSource dataSource;
    private volatile Exception error;
    private volatile Exception errorB;

    @BeforeEach
    protected void setUp() throws Exception {
        dataSource = new DruidDataSource();
        dataSource.setUrl("jdbc:mock:xxx");
        dataSource.setTestOnBorrow(false);
        dataSource.setInitialSize(1);

        dataSource.getProxyFilters().add(new FilterAdapter() {
            public ConnectionProxy connection_connect(FilterChain chain, Properties info) throws SQLException {
                try {
                    Thread.sleep(Long.MAX_VALUE);
                } catch (InterruptedException e) {
                    // skip
                }
                return null;
            }
        });
    }

    @AfterEach
    protected void tearDown() throws Exception {
        dataSource.close();
    }

    @Test
    public void test_error() throws Exception {
        final CountDownLatch startedLatch = new CountDownLatch(1);
        final CountDownLatch endLatch = new CountDownLatch(1);

        Thread threadA = new Thread("A") {
            public void run() {
                try {
                    startedLatch.countDown();
                    dataSource.init();
                } catch (SQLException e) {
                    error = e;
                } finally {
                    endLatch.countDown();
                }
            }
        };
        threadA.start();

        startedLatch.await();

        Thread.sleep(10);

        assertFalse(dataSource.isInited());

        final CountDownLatch startedLatchB = new CountDownLatch(1);
        final CountDownLatch endLatchB = new CountDownLatch(1);
        Thread threadB = new Thread("B") {
            public void run() {
                try {
                    startedLatchB.countDown();
                    dataSource.init();
                } catch (SQLException e) {
                    errorB = e;
                } finally {
                    endLatchB.countDown();
                }
            }
        };
        threadB.start();
        startedLatchB.await();

        threadB.interrupt();
        endLatchB.await();

        assertNotNull(errorB);
        assertTrue(errorB.getCause() instanceof InterruptedException);

        threadA.interrupt();

        endLatch.await();
        endLatchB.await();
        assertNotNull(error);

        assertEquals(1, dataSource.getCreateErrorCount());
    }
}
