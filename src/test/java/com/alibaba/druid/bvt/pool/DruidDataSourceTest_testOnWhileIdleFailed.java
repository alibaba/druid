package com.alibaba.druid.bvt.pool;

import java.sql.Connection;
import java.util.concurrent.atomic.AtomicInteger;

import javax.sql.PooledConnection;

import junit.framework.TestCase;

import org.junit.Assert;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.ValidConnectionCheckerAdapter;

/**
 * 这个场景测试defaultAutoCommit
 * 
 * @author wenshao [szujobs@hotmail.com]
 */
public class DruidDataSourceTest_testOnWhileIdleFailed extends TestCase {

    private DruidDataSource dataSource;

    private AtomicInteger   validCount = new AtomicInteger();

    protected void setUp() throws Exception {
        dataSource = new DruidDataSource();
        dataSource.setUrl("jdbc:mock:xxx");
        dataSource.setTestOnBorrow(false);
        dataSource.setTestWhileIdle(true);
        dataSource.setTimeBetweenEvictionRunsMillis(20);
        dataSource.setValidationQuery("select 'x'");
        dataSource.setValidConnectionChecker(new ValidConnectionCheckerAdapter() {

            @Override
            public boolean isValidConnection(Connection c, String query, int validationQueryTimeout) {
                int count = validCount.getAndIncrement();

                if (count == 0) {
                    return true;
                }

                if (count == 1) {
                    return false;
                }

                return true;
            }
        });
    }

    protected void tearDown() throws Exception {
        dataSource.close();
    }

    public void test_conn() throws Exception {
        {
            PooledConnection conn = dataSource.getPooledConnection();
            conn.close();
        }

        Assert.assertEquals(1, validCount.get()); // createValidate

        Assert.assertEquals(1, dataSource.getPoolingCount());
        Assert.assertEquals(1, dataSource.getCreateCount());
        Assert.assertEquals(0, dataSource.getDiscardCount());
        Assert.assertEquals(1, dataSource.getConnectCount());
        Assert.assertEquals(1, dataSource.getCloseCount());

        Thread.sleep(21);
        
        {
            PooledConnection conn = dataSource.getPooledConnection();
            conn.close();
        }
        
        Assert.assertEquals(3, validCount.get()); // createValidate

        Assert.assertEquals(1, dataSource.getPoolingCount());
        Assert.assertEquals(2, dataSource.getCreateCount());
        Assert.assertEquals(1, dataSource.getDiscardCount());
        Assert.assertEquals(3, dataSource.getConnectCount());
        Assert.assertEquals(2, dataSource.getCloseCount());
    }
}
