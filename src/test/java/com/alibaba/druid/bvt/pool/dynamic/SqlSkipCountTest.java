package com.alibaba.druid.bvt.pool.dynamic;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.PreparedStatement;

import com.alibaba.druid.PoolTestCase;
import junit.framework.TestCase;

import org.junit.Assert;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.DruidDataSourceStatValue;
import com.alibaba.druid.support.logging.Log;

public class SqlSkipCountTest extends PoolTestCase {

    private DruidDataSource dataSource;

    private Log             dataSourceLog;

    protected void setUp() throws Exception {
        super.setUp();

        Field logField = DruidDataSource.class.getDeclaredField("LOG");
        logField.setAccessible(true);
        dataSourceLog = (Log) logField.get(null);

        dataSourceLog.resetStat();

        dataSource = new DruidDataSource();
        dataSource.setUrl("jdbc:mock:xxx");
        dataSource.setTestOnBorrow(false);
        dataSource.setFilters("stat");
        dataSource.init();

        Assert.assertEquals(1, dataSourceLog.getInfoCount());
    }

    protected void tearDown() throws Exception {
        dataSource.close();

        super.tearDown();
    }

    public void test_connectPropertiesChange() throws Exception {
        for (int i = 0; i < 2000; ++i) {
            Connection conn = dataSource.getConnection();
            PreparedStatement stmt = conn.prepareStatement("select " + i);
            stmt.execute();
            stmt.close();
            conn.close();
        }
        {
            DruidDataSourceStatValue statValue = dataSource.getStatValueAndReset();
            Assert.assertEquals(1000, statValue.getSqlList().size());
            Assert.assertEquals(1000, statValue.getSqlSkipCount());
        }
        
        dataSource.setConnectionProperties("druid.stat.sql.MaxSize=2000");
        for (int i = 0; i < 2000; ++i) {
            Connection conn = dataSource.getConnection();
            PreparedStatement stmt = conn.prepareStatement("select " + i);
            stmt.execute();
            stmt.close();
            conn.close();
        }
        {
            DruidDataSourceStatValue statValue = dataSource.getStatValueAndReset();
            Assert.assertEquals(2000, statValue.getSqlList().size());
            Assert.assertEquals(0, statValue.getSqlSkipCount());
        }
        {
            DruidDataSourceStatValue statValue = dataSource.getStatValueAndReset();
            Assert.assertEquals(0, statValue.getSqlList().size());
            Assert.assertEquals(0, statValue.getSqlSkipCount());
        }
        
        dataSource.setConnectionProperties("druid.stat.sql.MaxSize=2000");
        for (int i = 0; i < 2000; ++i) {
            Connection conn = dataSource.getConnection();
            PreparedStatement stmt = conn.prepareStatement("select " + i);
            stmt.execute();
            stmt.close();
            conn.close();
        }
        {
            DruidDataSourceStatValue statValue = dataSource.getStatValueAndReset();
            Assert.assertEquals(2000, statValue.getSqlList().size());
            Assert.assertEquals(0, statValue.getSqlSkipCount());
        }
        
        dataSource.setConnectionProperties("druid.stat.sql.MaxSize=100");
        for (int i = 0; i < 2000; ++i) {
            Connection conn = dataSource.getConnection();
            PreparedStatement stmt = conn.prepareStatement("select " + i);
            stmt.execute();
            stmt.close();
            conn.close();
        }
        {
            DruidDataSourceStatValue statValue = dataSource.getStatValueAndReset();
            Assert.assertEquals(100, statValue.getSqlList().size());
            Assert.assertEquals(1900, statValue.getSqlSkipCount());
        }
        {
            DruidDataSourceStatValue statValue = dataSource.getStatValueAndReset();
            Assert.assertEquals(0, statValue.getSqlList().size());
            Assert.assertEquals(0, statValue.getSqlSkipCount());
        }
    }
}
