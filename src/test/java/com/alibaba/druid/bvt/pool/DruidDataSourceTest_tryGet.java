package com.alibaba.druid.bvt.pool;

import org.junit.Assert;

import junit.framework.TestCase;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.DruidPooledConnection;

public class DruidDataSourceTest_tryGet extends TestCase {

    private DruidDataSource dataSource;

    protected void setUp() throws Exception {
        dataSource = new DruidDataSource();
        dataSource.setUrl("jdbc:mock:xxx");
        dataSource.setMaxActive(1);
        dataSource.setInitialSize(1);
        dataSource.setTestOnBorrow(false);
        dataSource.init();
    }

    protected void tearDown() throws Exception {
        dataSource.close();
    }

    public void test_0() throws Exception {
        DruidPooledConnection conn1 = this.dataSource.tryGetConnection();
        Assert.assertNotNull(conn1);

        DruidPooledConnection conn2 = this.dataSource.tryGetConnection();
        Assert.assertNull(conn2);
        
        conn1.close();
    }

}
