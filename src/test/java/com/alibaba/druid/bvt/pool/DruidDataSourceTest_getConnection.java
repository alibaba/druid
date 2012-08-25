package com.alibaba.druid.bvt.pool;

import junit.framework.TestCase;

import org.junit.Assert;

import com.alibaba.druid.pool.DruidDataSource;

/**
 * 这个场景测试defaultAutoCommit
 * 
 * @author wenshao<szujobs@hotmail.com>
 */
public class DruidDataSourceTest_getConnection extends TestCase {

    private DruidDataSource dataSource;

    protected void setUp() throws Exception {
        dataSource = new DruidDataSource();
        dataSource.setUrl("jdbc:mock:xxx");
        dataSource.setTestOnBorrow(false);

    }

    protected void tearDown() throws Exception {
        dataSource.close();
    }

    public void test_conn_error() throws Exception {
        Exception error = null;
        try {
            dataSource.getConnection(null, null);
        } catch (UnsupportedOperationException e) {
            error = e;
        }
        Assert.assertNotNull(error);
    }

}
