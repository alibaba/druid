package com.alibaba.druid.bvt.pool;

import java.sql.SQLException;

import com.alibaba.druid.PoolTestCase;
import org.junit.Assert;
import junit.framework.TestCase;

import com.alibaba.druid.filter.FilterAdapter;
import com.alibaba.druid.filter.FilterChain;
import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.GetConnectionTimeoutException;
import com.alibaba.druid.proxy.jdbc.ConnectionProxy;

public class DruidConnectionHolderTest3 extends PoolTestCase {

    private DruidDataSource dataSource;

    protected void setUp() throws Exception {
        super.setUp();

        dataSource = new DruidDataSource();
        dataSource.setUrl("jdbc:mock:xxx");
        dataSource.setTestOnBorrow(false);
        dataSource.setInitialSize(1);
        dataSource.getProxyFilters().add(new FilterAdapter() {

            public int connection_getTransactionIsolation(FilterChain chain, ConnectionProxy connection)
                                                                                                        throws SQLException {
                throw new MySQLException();
            }
        });

    }

    protected void tearDown() throws Exception {
        dataSource.close();

        super.tearDown();
    }

    public void test_mysqlSyntaxError() throws Exception {
        {
            Exception error = null;
            try {
                dataSource.init();
            } catch (MySQLException e) {
                error = e;
            }
            Assert.assertNotNull(error);
        }

        Assert.assertEquals(0, dataSource.getPoolingCount());

        Exception error = null;
        try {
            dataSource.getConnection(100);
        } catch (GetConnectionTimeoutException e) {
            error = e;
        }
        Assert.assertNotNull(error);
    }

    public static class MySQLException extends SQLException {

    }
}
