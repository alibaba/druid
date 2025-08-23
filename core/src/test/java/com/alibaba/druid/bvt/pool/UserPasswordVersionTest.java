package com.alibaba.druid.bvt.pool;

import static org.junit.Assert.*;


import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.DruidPooledConnection;
import junit.framework.TestCase;

import java.sql.Connection;
import java.util.Properties;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 这个场景测试initialSize > maxActive
 *
 * @author wenshao [szujobs@hotmail.com]
 */
public class UserPasswordVersionTest extends TestCase {
    private DruidDataSource dataSource;

    protected void setUp() throws Exception {
        dataSource = new DruidDataSource();
        dataSource.setUrl("jdbc:mock:xxx");
        dataSource.setUsername("u0");
        dataSource.setPassword("p0");
        dataSource.setTestOnBorrow(false);
        dataSource.setMaxActive(30);
        dataSource.setMaxWait(30);
        dataSource.setInitialSize(1);
        dataSource.setMinIdle(3);
        dataSource.init();
    }

    protected void tearDown() throws Exception {
        dataSource.close();
    }

    public void test_maxWait() throws Exception {
        ExecutorService executor = Executors.newFixedThreadPool(10);
        final CountDownLatch latch0 = new CountDownLatch(1);
        executor.submit(
                () -> {
                    try {
                        DruidPooledConnection[] connections = new DruidPooledConnection[10];
                        for (int i = 0; i < connections.length; i++) {
                            connections[i] = dataSource.getConnection();
                        }
                        for (int i = 0; i < connections.length; i++) {
                            connections[i].close();
                        }
                        assertEquals(connections.length, dataSource.getPoolingCount());
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        latch0.countDown();
                    }
                }
        );
        latch0.await();

        DruidPooledConnection conn = dataSource.getConnection();
        assertEquals(0, conn.getConnectionHolder().getUserPasswordVersion());

        final CountDownLatch latch1 = new CountDownLatch(1);
        executor.submit(() -> {
            try {
                Properties properties = new Properties();
                properties.put("druid.username", "u1");
                properties.put("druid.password", "p1");
                dataSource.configFromProperties(properties);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                latch1.countDown();
            }
        });
        latch1.await();

        assertEquals(9, dataSource.getPoolingCount());
        assertEquals(1, dataSource.getActiveCount());

        conn.close();

        assertEquals(9, dataSource.getPoolingCount());
        assertEquals(0, dataSource.getActiveCount());

        DruidPooledConnection conn1 = dataSource.getConnection();
        assertEquals(1, conn1.getConnectionHolder().getUserPasswordVersion());
        conn1.close();

        assertEquals(9, dataSource.getPoolingCount());
        assertEquals(0, dataSource.getActiveCount());
    }
}
