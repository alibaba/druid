package com.alibaba.druid.bvt.pool;

import java.sql.SQLException;
import java.util.Properties;

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
public class DruidDataSourceTest_lastCreateError extends TestCase {

    private DruidDataSource dataSource;

    protected void setUp() throws Exception {
        dataSource = new DruidDataSource();
        dataSource.setUrl("jdbc:mock:xxx");
        dataSource.setTestOnBorrow(false);

        dataSource.getProxyFilters().add(new FilterAdapter() {

            public ConnectionProxy connection_connect(FilterChain chain, Properties info) throws SQLException {
                throw new SQLException();
            }
        });
    }

    protected void tearDown() throws Exception {
        dataSource.close();
    }

    public void test_error() throws Exception {
        Assert.assertNull(dataSource.getLastCreateError());
        Assert.assertNull(dataSource.getLastCreateErrorTime());
        Assert.assertEquals(0, dataSource.getLastCreateErrorTimeMillis());

        Exception error = null;
        try {
            dataSource.getConnection(100);
        } catch (Exception e) {
            error = e;
        }

        Assert.assertNotNull(error);

        Assert.assertNotNull(dataSource.getLastCreateError());
        Assert.assertNotNull(dataSource.getLastCreateErrorTime());
        Assert.assertEquals(true, dataSource.getLastCreateErrorTimeMillis() > 0);
    }
}
