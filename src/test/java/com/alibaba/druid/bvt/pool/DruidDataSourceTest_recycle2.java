package com.alibaba.druid.bvt.pool;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import junit.framework.TestCase;

import org.junit.Assert;

import com.alibaba.druid.filter.FilterAdapter;
import com.alibaba.druid.filter.FilterChain;
import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.DruidPooledConnection;
import com.alibaba.druid.proxy.jdbc.ConnectionProxy;

/**
 * 这个场景测试initialSize > maxActive
 * 
 * @author wenshao<szujobs@hotmail.com>
 */
public class DruidDataSourceTest_recycle2 extends TestCase {

    private DruidDataSource dataSource;

    protected void setUp() throws Exception {
        dataSource = new DruidDataSource();
        dataSource.setUrl("jdbc:mock:xxx");
        dataSource.setTestOnBorrow(false);
        dataSource.getProxyFilters().add(new FilterAdapter() {

            public void connection_rollback(FilterChain chain, ConnectionProxy connection) throws SQLException {
                throw new SQLException();
            }
        });
    }

    protected void tearDown() throws Exception {
        dataSource.close();
    }

    public void test_recycle() throws Exception {
        DruidPooledConnection conn = dataSource.getConnection();

        Statement stmt = conn.createStatement();
        stmt.execute("select 1");

        Assert.assertEquals(0, dataSource.getPoolingCount());
        Assert.assertEquals(1, dataSource.getActiveCount());

        conn.close();

        Assert.assertEquals(1, dataSource.getPoolingCount());
        Assert.assertEquals(0, dataSource.getActiveCount());
    }

    public void test_recycle_error() throws Exception {
        DruidPooledConnection conn = dataSource.getConnection();
        conn.setAutoCommit(false);
        conn.setReadOnly(false);

        Statement stmt = conn.createStatement();
        stmt.execute("select 1");

        Assert.assertEquals(0, dataSource.getPoolingCount());
        Assert.assertEquals(1, dataSource.getActiveCount());

        Exception error = null;
        try {
            conn.close();
        } catch (Exception e) {
            error = e;
        }
        Assert.assertNull(error);
        
        {
            Connection conn2 = dataSource.getConnection();
            conn2.close();
        }

        Assert.assertEquals(1, dataSource.getPoolingCount());
        Assert.assertEquals(0, dataSource.getActiveCount());
    }

    public void f_test_recycle_error_interrupt() throws Exception {
        final AtomicReference<Exception> errorRef = new AtomicReference<Exception>();

        final CountDownLatch closeBeforeLatch = new CountDownLatch(1);
        final CountDownLatch lockLatch = new CountDownLatch(1);
        final CountDownLatch endLatch = new CountDownLatch(1);

        Thread thread = new Thread() {

            public void run() {
                try {
                    DruidPooledConnection conn = dataSource.getConnection();
                    conn.setAutoCommit(false);
                    conn.setReadOnly(false);

                    Statement stmt = conn.createStatement();
                    stmt.execute("select 1");

                    Assert.assertEquals(0, dataSource.getPoolingCount());
                    Assert.assertEquals(1, dataSource.getActiveCount());

                    closeBeforeLatch.countDown();
                    lockLatch.await();

                    conn.close();
                } catch (Exception e) {
                    errorRef.set(e);
                } finally {
                    endLatch.countDown();
                }
            }
        };
        thread.start();

        Assert.assertTrue(closeBeforeLatch.await(1, TimeUnit.SECONDS));

        dataSource.getLock().lock();
        lockLatch.countDown();
        
        Thread.sleep(10);

        thread.interrupt();

        Assert.assertTrue(endLatch.await(1, TimeUnit.MINUTES));

        Exception error = errorRef.get();
        Assert.assertNull(error);
    }
}
