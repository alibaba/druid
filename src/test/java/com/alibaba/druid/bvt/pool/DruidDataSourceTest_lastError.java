package com.alibaba.druid.bvt.pool;

import java.sql.Connection;
import java.sql.SQLException;

import junit.framework.TestCase;

import org.junit.Assert;

import com.alibaba.druid.filter.FilterAdapter;
import com.alibaba.druid.filter.FilterChain;
import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.proxy.jdbc.ConnectionProxy;

/**
 * 这个场景测试minIdle > maxActive
 * 
 * @author wenshao [szujobs@hotmail.com]
 */
public class DruidDataSourceTest_lastError extends TestCase {

    private DruidDataSource dataSource;

    protected void setUp() throws Exception {
        dataSource = new DruidDataSource();
        dataSource.setUrl("jdbc:mock:xxx");
        dataSource.setTestOnBorrow(false);

        dataSource.getProxyFilters().add(new FilterAdapter() {

            public void connection_setAutoCommit(FilterChain chain, ConnectionProxy connection, boolean autoCommit)
                                                                                                                   throws SQLException {
                throw new SQLException();
            }
        });
    }

    protected void tearDown() throws Exception {
        dataSource.close();
    }

    public void test_error() throws Exception {
        Assert.assertNull(dataSource.getLastError());
        Assert.assertNull(dataSource.getLastErrorTime());
        Assert.assertEquals(0, dataSource.getLastErrorTimeMillis());
        
        Connection conn = dataSource.getConnection();

        Exception error = null;
        try {
            conn.setAutoCommit(false);
        } catch (Exception e) {
            error = e;
        }
        
        Assert.assertNotNull(error);
        
        Assert.assertNotNull(dataSource.getLastError());
        Assert.assertNotNull(dataSource.getLastErrorTime());
        Assert.assertEquals(true, dataSource.getLastErrorTimeMillis() > 0);
    }
}
