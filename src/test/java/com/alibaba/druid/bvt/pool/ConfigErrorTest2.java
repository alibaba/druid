package com.alibaba.druid.bvt.pool;

import java.lang.reflect.Field;
import java.sql.Connection;

import junit.framework.Assert;
import junit.framework.TestCase;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.support.logging.Log;

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
        Field field = DruidDataSource.class.getDeclaredField("LOG");
        field.setAccessible(true);
        Log LOG = (Log) field.get(null);
        
        LOG.resetStat();
        
        Assert.assertEquals(0, LOG.getErrorCount());

        Connection conn = dataSource.getConnection();
        conn.close();
        
        Assert.assertEquals(1, LOG.getErrorCount());
    }
}
