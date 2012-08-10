package com.alibaba.druid.bvt.pool;

import java.lang.reflect.Field;

import junit.framework.Assert;
import junit.framework.TestCase;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.support.logging.Log;

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
        Field field = DruidDataSource.class.getDeclaredField("LOG");
        field.setAccessible(true);
        Log LOG = (Log) field.get(null);
        
        LOG.resetStat();
        
        Assert.assertEquals(0, LOG.getErrorCount());
        dataSource.init();
        Assert.assertEquals(1, LOG.getErrorCount());
    }
}
