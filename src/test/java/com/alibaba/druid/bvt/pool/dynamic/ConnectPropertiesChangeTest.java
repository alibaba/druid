package com.alibaba.druid.bvt.pool.dynamic;

import java.lang.reflect.Field;
import java.sql.Connection;

import junit.framework.Assert;
import junit.framework.TestCase;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.support.logging.Log;

public class ConnectPropertiesChangeTest extends TestCase {

    private DruidDataSource dataSource;

    private Log             dataSourceLog;

    protected void setUp() throws Exception {
        Field logField = DruidDataSource.class.getDeclaredField("LOG");
        logField.setAccessible(true);
        dataSourceLog = (Log) logField.get(null);

        dataSourceLog.resetStat();

        dataSource = new DruidDataSource();
        dataSource.setUrl("jdbc:mock:xxx");
        dataSource.setTestOnBorrow(false);
        dataSource.setConnectionProperties("a=3;b=4");
        dataSource.init();
        
        Assert.assertEquals(1, dataSourceLog.getInfoCount());
    }

    protected void tearDown() throws Exception {
        dataSource.close();
    }

    public void test_connectPropertiesChange() throws Exception {
        Assert.assertEquals(2, dataSource.getConnectProperties().size());

        Assert.assertEquals("3", dataSource.getConnectProperties().getProperty("a"));
        Assert.assertEquals("4", dataSource.getConnectProperties().getProperty("b"));

        Connection conn = dataSource.getConnection();
        conn.close();

        dataSource.setConnectionProperties("b=5;c=6");
        
        Assert.assertEquals(2, dataSourceLog.getInfoCount());

        Assert.assertEquals(2, dataSource.getConnectProperties().size());

        Assert.assertEquals("5", dataSource.getConnectProperties().getProperty("b"));
        Assert.assertEquals("6", dataSource.getConnectProperties().getProperty("c"));
    }
}
