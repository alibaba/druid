package com.alibaba.druid.bvt.pool.basic;

import java.sql.Connection;

import org.junit.Assert;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.GetConnectionTimeoutException;
import com.alibaba.druid.util.JdbcUtils;

import junit.framework.TestCase;

public class MaxEvictableIdleTimeMillisTest extends TestCase {

    private DruidDataSource dataSource;

    protected void setUp() throws Exception {
        dataSource = new DruidDataSource();
        dataSource.setUrl("jdbc:mock:xxx");
        dataSource.setTestOnBorrow(false);
        dataSource.setMaxActive(50);
        dataSource.setMinIdle(5);
        dataSource.setMinEvictableIdleTimeMillis(10);
        dataSource.setMaxEvictableIdleTimeMillis(100);
        dataSource.setMaxWait(20);
        dataSource.init();
    }

    protected void tearDown() throws Exception {
        dataSource.close();
    }

    public void test_error() throws Exception {
        Exception error = null;
        try {
            dataSource.setMaxEvictableIdleTimeMillis(1);
        } catch (Exception ex) {
            error = ex;
        }
        Assert.assertNotNull(error);
        Assert.assertEquals(100, dataSource.getMaxEvictableIdleTimeMillis());
    }
    
    public void test_error2() throws Exception {
        Exception error = null;
        try {
            dataSource.setMaxEvictableIdleTimeMillis(1);
        } catch (Exception ex) {
            error = ex;
        }
        Assert.assertNotNull(error);
        Assert.assertEquals(100, dataSource.getMaxEvictableIdleTimeMillis());
    }

    public void test_max() throws Exception {
        connect(10);

        Assert.assertEquals(10, dataSource.getPoolingCount());
        Thread.sleep(20);
        dataSource.shrink(true);
        Assert.assertEquals(5, dataSource.getPoolingCount());

        Thread.sleep(100);
        dataSource.shrink(true);
        Assert.assertEquals(0, dataSource.getPoolingCount());
    }

    public int connect(int count) throws Exception {
        int successCount = 0;
        Connection[] connections = new Connection[count];
        for (int i = 0; i < count; ++i) {
            try {
                connections[i] = dataSource.getConnection();
                successCount++;
            } catch (GetConnectionTimeoutException e) {
                // skip
            }
        }

        for (int i = 0; i < count; ++i) {
            JdbcUtils.close(connections[i]);
        }

        return successCount;
    }
}
