package com.alibaba.druid.bvt.pool;

import static org.junit.Assert.*;


import java.sql.Connection;
import java.util.concurrent.atomic.AtomicInteger;

import javax.sql.PooledConnection;

import junit.framework.TestCase;


import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.ValidConnectionCheckerAdapter;

/**
 * 这个场景测试defaultAutoCommit
 *
 * @author wenshao [szujobs@hotmail.com]
 */
public class DruidDataSourceTest_testOnWhileIdleFailed extends TestCase {
    private DruidDataSource dataSource;

    private AtomicInteger validCount = new AtomicInteger();

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

        assertEquals(1, validCount.get()); // createValidate

        assertEquals(1, dataSource.getPoolingCount());
        assertEquals(1, dataSource.getCreateCount());
        assertEquals(0, dataSource.getDiscardCount());
        assertEquals(1, dataSource.getConnectCount());
        assertEquals(1, dataSource.getCloseCount());

        Thread.sleep(21);

        {
            PooledConnection conn = dataSource.getPooledConnection();
            conn.close();
        }

        assertEquals(3, validCount.get()); // createValidate

        assertEquals(1, dataSource.getPoolingCount());
        assertEquals(2, dataSource.getCreateCount());
        assertEquals(1, dataSource.getDiscardCount());
        assertEquals(3, dataSource.getConnectCount());
        assertEquals(2, dataSource.getCloseCount());
    }
}
