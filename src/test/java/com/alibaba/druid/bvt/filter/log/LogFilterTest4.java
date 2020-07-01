package com.alibaba.druid.bvt.filter.log;

import java.util.Properties;

import junit.framework.TestCase;

import org.junit.Assert;

import com.alibaba.druid.filter.logging.Log4jFilter;
import com.alibaba.druid.filter.logging.LogFilter;

public class LogFilterTest4 extends TestCase {

    public void test_properties() throws Exception {
        LogFilter filter = new Log4jFilter();

        Assert.assertEquals(true, filter.isConnectionLogEnabled());
        Assert.assertEquals(true, filter.isStatementLogEnabled());
        Assert.assertEquals(false, filter.isStatementExecutableSqlLogEnable());
        Assert.assertEquals(true, filter.isResultSetLogEnabled());
    }

    public void test_properties_1() throws Exception {

        System.setProperty("druid.log.conn", "false");
        System.setProperty("druid.log.stmt", "false");
        System.setProperty("druid.log.rs", "false");
        System.setProperty("druid.log.stmt.executableSql", "true");

        try {
            LogFilter filter = new Log4jFilter();

            Assert.assertEquals(false, filter.isConnectionLogEnabled());
            Assert.assertEquals(false, filter.isStatementLogEnabled());
            Assert.assertEquals(true, filter.isStatementExecutableSqlLogEnable());
            Assert.assertEquals(false, filter.isResultSetLogEnabled());
            
            Properties properties = new Properties();
            properties.setProperty("druid.log.conn", "true");
            properties.setProperty("druid.log.stmt", "true");
            properties.setProperty("druid.log.rs", "true");
            properties.setProperty("druid.log.stmt.executableSql", "false");
            
            filter.configFromProperties(properties);
            
            Assert.assertEquals(true, filter.isConnectionLogEnabled());
            Assert.assertEquals(true, filter.isStatementLogEnabled());
            Assert.assertEquals(false, filter.isStatementExecutableSqlLogEnable());
            Assert.assertEquals(true, filter.isResultSetLogEnabled());
        } finally {
            System.clearProperty("druid.log.conn");
            System.clearProperty("druid.log.stmt");
            System.clearProperty("druid.log.rs");
            System.clearProperty("druid.log.stmt.executableSql");
        }
    }
}
