package com.alibaba.druid.bvt.pool;

import java.lang.reflect.Field;

import junit.framework.Assert;
import junit.framework.TestCase;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.support.logging.Log;

public class ConfigErrorTestMysql extends TestCase {

    private DruidDataSource dataSource;

    protected void setUp() throws Exception {
        dataSource = new DruidDataSource();
        dataSource.setUrl("jdbc:mysql:xxx");
        dataSource.setTestOnBorrow(false);
        dataSource.setInitialSize(0);
    }

    protected void tearDown() throws Exception {
        dataSource.close();
    }

    public void test_success() throws Exception {
        dataSource.setTestWhileIdle(true);

        Field field = DruidDataSource.class.getDeclaredField("LOG");
        dataSource.setValidationQuery("select 1");
        field.setAccessible(true);
        Log LOG = (Log) field.get(null);

        LOG.resetStat();

        Assert.assertEquals(0, LOG.getWarnCount());
        dataSource.init();
        Assert.assertEquals(0, LOG.getWarnCount());
    }

    public void test_warn() throws Exception {
        dataSource.setTestWhileIdle(false);

        Field field = DruidDataSource.class.getDeclaredField("LOG");
        field.setAccessible(true);
        Log LOG = (Log) field.get(null);

        LOG.resetStat();

        Assert.assertEquals(0, LOG.getWarnCount());
        dataSource.init();
        Assert.assertEquals(1, LOG.getWarnCount());
    }
}
