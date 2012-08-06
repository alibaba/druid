package com.alibaba.druid.bvt.pool;

import junit.framework.Assert;
import junit.framework.TestCase;

import com.alibaba.druid.pool.DruidDataSource;

public class ConfigErrorTest3 extends TestCase {

    private DruidDataSource dataSource;

    protected void setUp() throws Exception {
        dataSource = new DruidDataSource();
        dataSource.setUrl("jdbc:mysql:xxx");
        dataSource.setTestOnBorrow(false);
        dataSource.setTestOnReturn(false);
        dataSource.setTestWhileIdle(false);
        dataSource.setInitialSize(0);
        dataSource.setPoolPreparedStatements(true);
    }

    protected void tearDown() throws Exception {
        dataSource.close();
    }

    public void test_connect() throws Exception {
        DruidDataSource.LOG.resetStat();
        Assert.assertEquals(0, DruidDataSource.LOG.getErrorCount());
        dataSource.init();
        Assert.assertEquals(1, DruidDataSource.LOG.getErrorCount());
    }
}
