package com.alibaba.druid.bvt.pool;

import com.alibaba.druid.mock.MockDriver;
import com.alibaba.druid.mock.MockStatementBase;
import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.ExceptionSorter;
import com.alibaba.druid.pool.vendor.MySqlExceptionSorter;
import com.alibaba.druid.util.JdbcUtils;
import com.mysql.jdbc.Driver;
import junit.framework.TestCase;
import org.junit.Assert;

import java.sql.*;
import java.util.Properties;
import java.util.logging.Logger;

/**
 * 这个场景测试exceptionSorter_extend
 * 
 * @author xiaoying [caohongxi001@gmail.com]
 */
public class DruidDataSourceTest_exceptionSorter_extend extends TestCase {

    public static class SubDriver extends com.mysql.jdbc.Driver{

        /**
         * Construct a new driver and register it with DriverManager
         *
         * @throws SQLException if a database error occurs.
         */
        public SubDriver() throws SQLException {
        }
    }
    public static class SubDriver1 implements java.sql.Driver{

        /**
         * Construct a new driver and register it with DriverManager
         *
         * @throws SQLException if a database error occurs.
         */
        public SubDriver1() throws SQLException {
        }

        @Override
        public Connection connect(String url, Properties info) throws SQLException {
            return null;
        }

        @Override
        public boolean acceptsURL(String url) throws SQLException {
            return false;
        }

        @Override
        public DriverPropertyInfo[] getPropertyInfo(String url, Properties info) throws SQLException {
            return new DriverPropertyInfo[0];
        }

        @Override
        public int getMajorVersion() {
            return 0;
        }

        @Override
        public int getMinorVersion() {
            return 0;
        }

        @Override
        public boolean jdbcCompliant() {
            return false;
        }

        @Override
        public Logger getParentLogger() throws SQLFeatureNotSupportedException {
            return null;
        }
    }

    /**
     * 测试继承自com.mysql.jdbc.Driver的子类可以设置sorter
     * @throws Exception
     */
    public void testExceptionSorter() throws Exception {
        DruidDataSource dataSource1 = new DruidDataSource();
        try {
            dataSource1.setDriverClassName(SubDriver.class.getName());
            dataSource1.init();
            Assert.assertNotNull(dataSource1.getExceptionSorter());
            Assert.assertEquals(MySqlExceptionSorter.class.getName(), dataSource1.getExceptionSorter().getClass().getName());
        } finally {
            JdbcUtils.close(dataSource1);
        }
    }

    /**
     * 测试实现自java.sql.Driver的类未设置sorter
     * @throws Exception
     */
    public void testExceptionSorterNull() throws Exception {
        DruidDataSource dataSource1 = new DruidDataSource();
        try {
            dataSource1.setDriverClassName(SubDriver1.class.getName());
            dataSource1.init();
            Assert.assertEquals("sorter is not null", null, dataSource1.getExceptionSorter());
        } finally {
            JdbcUtils.close(dataSource1);
        }

    }
}
