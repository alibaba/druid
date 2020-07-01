package com.alibaba.druid.bvt.pool.dynamic;

import java.lang.reflect.Field;

import com.alibaba.druid.PoolTestCase;
import junit.framework.TestCase;

import org.junit.Assert;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.DruidDataSourceStatLogger;
import com.alibaba.druid.pool.DruidDataSourceStatLoggerImpl;
import com.alibaba.druid.support.logging.Log;

public class DruidDataSourceStatLoggerImplTest extends PoolTestCase {

    private DruidDataSource dataSource;

    private Log             statLog;

    protected void setUp() throws Exception {
        super.setUp();

        Field logField = DruidDataSourceStatLoggerImpl.class.getDeclaredField("LOG");
        logField.setAccessible(true);
        statLog = (Log) logField.get(null);

        statLog.resetStat();

        dataSource = new DruidDataSource();
        dataSource.setUrl("jdbc:mock:xxx");
        dataSource.setTestOnBorrow(false);
        dataSource.setConnectionProperties("a=3;b=4");
        dataSource.setFilters("stat");
        dataSource.init();

    }

    protected void tearDown() throws Exception {
        dataSource.close();

        super.tearDown();
    }

    public void test_connectPropertiesChange() throws Exception {
        Assert.assertTrue(dataSource.isWrapperFor(DruidDataSourceStatLogger.class));
        Assert.assertTrue(dataSource.isWrapperFor(DruidDataSourceStatLoggerImpl.class));
        DruidDataSourceStatLoggerImpl loggerImpl = dataSource.unwrap(DruidDataSourceStatLoggerImpl.class);
        Assert.assertSame(statLog, loggerImpl.getLogger());
        
        dataSource.setConnectionProperties("druid.stat.loggerName=xxx");
        Assert.assertNotSame(statLog, loggerImpl.getLogger());
    }
}
