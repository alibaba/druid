package com.alibaba.druid.bvt.pool;

import java.sql.Connection;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.Assert;
import junit.framework.TestCase;

import com.alibaba.druid.pool.DruidDataSource;

/**
 * 这个场景测试initialSize > maxActive
 * 
 * @author wenshao [szujobs@hotmail.com]
 */
public class DruidDataSourceTest_maxWait extends TestCase {

    private DruidDataSource dataSource;

    protected void setUp() throws Exception {
        dataSource = new DruidDataSource();
        dataSource.setUrl("jdbc:mock:xxx");
        dataSource.setTestOnBorrow(false);
        dataSource.setMaxActive(1);
        dataSource.setMaxWait(30);
        dataSource.setInitialSize(1);
        dataSource.init();
    }

    protected void tearDown() throws Exception {
        dataSource.close();
    }

    public void test_maxWait() throws Exception {
        Connection conn = dataSource.getConnection();

        final AtomicInteger errorCount = new AtomicInteger();
        final CountDownLatch endLatch = new CountDownLatch(2);

        Thread t1 = new Thread("t-1") {

            public void run() {
                try {
                    dataSource.getConnection();
                } catch (Exception e) {
                    errorCount.incrementAndGet();
                } finally {
                    endLatch.countDown();
                }
            }
        };
        
        Thread t2 = new Thread("t-2") {
            
            public void run() {
                try {
                    dataSource.getConnection();
                } catch (Exception e) {
                    errorCount.incrementAndGet();
                } finally {
                    endLatch.countDown();
                }
            }
        };
        
        t1.start();
        t2.start();
        
        Thread.sleep(10);
        t1.interrupt();
        
        endLatch.await();
        
        Assert.assertEquals(2, errorCount.get());

        conn.close();
    }
}
