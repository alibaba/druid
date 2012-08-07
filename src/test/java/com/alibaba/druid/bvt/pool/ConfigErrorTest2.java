package com.alibaba.druid.bvt.pool;

import java.sql.Connection;

import junit.framework.Assert;
import junit.framework.TestCase;

import com.alibaba.druid.pool.DruidDataSource;

public class ConfigErrorTest2 extends TestCase {

    private DruidDataSource dataSource;

    protected void setUp() throws Exception {
        dataSource = new DruidDataSource();
        dataSource.setUrl("jdbc:wrap-jdbc:jdbc:mock:xxx");
        dataSource.setTestOnBorrow(false);
        dataSource.setTestOnReturn(false);
        dataSource.setTestWhileIdle(false);
    }

    protected void tearDown() throws Exception {
        dataSource.close();
    }

    public void test_connect() throws Exception {
        DruidDataSource.LOG.resetStat();
        Assert.assertEquals(0, DruidDataSource.LOG.getErrorCount());

        Connection conn = dataSource.getConnection();
        conn.close();
        
        Assert.assertEquals(1, DruidDataSource.LOG.getErrorCount());
    }
}
