package com.alibaba.druid.bvt.pool;

import java.sql.SQLException;
import java.util.Properties;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import junit.framework.TestCase;

import org.junit.Assert;

import com.alibaba.druid.filter.FilterAdapter;
import com.alibaba.druid.filter.FilterChain;
import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.proxy.jdbc.ConnectionProxy;

/**
 * 这个场景测试并发初始化
 *
 * @author wenshao [szujobs@hotmail.com]
 */
public class DruidDataSourceTest3 extends TestCase {
    private DruidDataSource dataSource;
    private volatile Exception error;

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

    protected void tearDown() throws Exception {
        dataSource.close();
    }

    public void test_error() throws Exception {
        final CountDownLatch startedLatch = new CountDownLatch(1);
        final CountDownLatch endLatch = new CountDownLatch(1);

        Thread threadA = new Thread("A") {
            public void run() {
                try {
                    dataSource.init();
                    startedLatch.countDown();
                } catch (SQLException e) {
                    error = e;
                } finally {
                    endLatch.countDown();
                }
            }
        };
        threadA.start();

        startedLatch.await(1000, TimeUnit.MILLISECONDS);
        // assert wating timeout as threadA hangs by waiting createConnectionThread finish initialization.
        Assert.assertEquals(1, startedLatch.getCount());
        // Now, all physical connections are created by createConntectionTread.
        Assert.assertTrue(dataSource.isInited());

        threadA.interrupt();
        endLatch.await(100, TimeUnit.MILLISECONDS);

        Assert.assertEquals(0, dataSource.getCreateErrorCount());

    }
}
