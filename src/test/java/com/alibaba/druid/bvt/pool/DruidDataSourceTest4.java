package com.alibaba.druid.bvt.pool;

import java.sql.SQLException;
import java.util.Properties;

import junit.framework.TestCase;

import org.junit.Assert;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.ExceptionSorter;
import com.alibaba.druid.pool.vendor.MySqlValidConnectionChecker;

public class DruidDataSourceTest4 extends TestCase {

    private DruidDataSource dataSource;

    protected void setUp() throws Exception {
        dataSource = new DruidDataSource();
        dataSource.setUrl("jdbc:mock:xxx");
        dataSource.setTestOnBorrow(false);
        dataSource.setInitialSize(1);

    }

    protected void tearDown() throws Exception {
        dataSource.close();
    }

    public void test_getTransactionThresholdMillis() {
        Assert.assertEquals(0, dataSource.getTransactionThresholdMillis());

        dataSource.setTransactionThresholdMillis(100);

        Assert.assertEquals(100, dataSource.getTransactionThresholdMillis());
    }

    public void test_getTransactionHistogramRanges() {
        Assert.assertNotNull(dataSource.getTransactionHistogramRanges());
    }

    public void test_getTransactionHistogramRanges_1() {
        Assert.assertEquals(6, dataSource.getTransactionHistogramRanges().length);
    }

    public void test_setValidConnectionCheckerClassName() throws Exception {
        Assert.assertNull(dataSource.getValidConnectionChecker());
        dataSource.setValidConnectionCheckerClassName(MySqlValidConnectionChecker.class.getName());
        Assert.assertNotNull(dataSource.getValidConnectionChecker());
    }

    public void test_setMinIdle() throws Exception {
        Assert.assertEquals(0, dataSource.getMinIdle());
        dataSource.init();
        dataSource.setMinIdle(1);
        Assert.assertEquals(1, dataSource.getMinIdle());
    }

    public void test_setMinIdle_error() throws Exception {
        Assert.assertEquals(0, dataSource.getMinIdle());
        dataSource.init();
        Exception error = null;
        try {
            dataSource.setMinIdle(100);
        } catch (Exception e) {
            error = e;
        }
        Assert.assertNotNull(error);
        Assert.assertEquals(0, dataSource.getMinIdle());
    }

    public void test_setExceptionSorter_error() throws Exception {
        dataSource.setExceptionSorter("xx");
    }

    public void test_setExceptionSorter_error2() throws Exception {
        Exception error = null;
        try {
            dataSource.setExceptionSorter(MyExceptionSorter.class.getName());
        } catch (Exception e) {
            error = e;
        }
        Assert.assertNotNull(error);
    }
    
    public void test_getParentLogger() throws Exception {
        Exception error = null;
        try {
            dataSource.getParentLogger();
        } catch (Exception e) {
            error = e;
        }
        Assert.assertNotNull(error);
    }

    private class MyExceptionSorter implements ExceptionSorter {

        @Override
        public boolean isExceptionFatal(SQLException e) {
            return false;
        }

        @Override
        public void configFromProperties(Properties properties) {
            // TODO Auto-generated method stub
            
        }

    }
}
