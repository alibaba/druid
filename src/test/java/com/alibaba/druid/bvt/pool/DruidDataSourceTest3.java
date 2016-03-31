package com.alibaba.druid.bvt.pool;

import java.sql.SQLException;
import java.util.Properties;
import java.util.concurrent.CountDownLatch;

import junit.framework.TestCase;

import org.junit.Assert;

import com.alibaba.druid.filter.FilterAdapter;
import com.alibaba.druid.filter.FilterChain;
import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.proxy.jdbc.ConnectionProxy;

/**
 * 这个场景测试并发初始化
 * @author wenshao [szujobs@hotmail.com]
 *
 */
public class DruidDataSourceTest3 extends TestCase {

    private DruidDataSource    dataSource;
    private volatile Exception error;
    private volatile Exception errorB;

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

        Assert.assertFalse(dataSource.isInited());

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
        
        Assert.assertNotNull(errorB);
        Assert.assertTrue(errorB.getCause() instanceof InterruptedException);

        threadA.interrupt();

        endLatch.await();
        endLatchB.await();
        Assert.assertNotNull(error);
        
        Assert.assertEquals(1, dataSource.getCreateErrorCount());

    }
}
