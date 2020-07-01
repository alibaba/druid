package com.alibaba.druid.bvt.filter.log;

import junit.framework.TestCase;

import org.junit.Assert;
import org.slf4j.LoggerFactory;

import com.alibaba.druid.filter.logging.Slf4jLogFilter;


public class Slf4jLogFilterTest extends TestCase {
    public void test_slf4j() throws Exception {
        Slf4jLogFilter filter = new Slf4jLogFilter();
        Assert.assertEquals("druid.sql.DataSource", filter.getDataSourceLoggerName());
        Assert.assertEquals("druid.sql.Connection", filter.getConnectionLoggerName());
        Assert.assertEquals("druid.sql.Statement", filter.getStatementLoggerName());
        Assert.assertEquals("druid.sql.ResultSet", filter.getResultSetLoggerName());
        
        filter.setDataSourceLoggerName("x.sql.DataSource");
        filter.setConnectionLoggerName("x.sql.Connection");
        filter.setStatementLoggerName("x.sql.Statement");
        filter.setResultSetLoggerName("x.sql.ResultSet");
        
        Assert.assertEquals("x.sql.DataSource", filter.getDataSourceLoggerName());
        Assert.assertEquals("x.sql.Connection", filter.getConnectionLoggerName());
        Assert.assertEquals("x.sql.Statement", filter.getStatementLoggerName());
        Assert.assertEquals("x.sql.ResultSet", filter.getResultSetLoggerName());
        
        filter.setDataSourceLogger(LoggerFactory.getLogger("y.sql.DataSource"));
        filter.setConnectionLogger(LoggerFactory.getLogger("y.sql.Connection"));
        filter.setStatementLogger(LoggerFactory.getLogger("y.sql.Statement"));
        filter.setResultSetLogger(LoggerFactory.getLogger("y.sql.ResultSet"));
        
        Assert.assertEquals("y.sql.DataSource", filter.getDataSourceLoggerName());
        Assert.assertEquals("y.sql.Connection", filter.getConnectionLoggerName());
        Assert.assertEquals("y.sql.Statement", filter.getStatementLoggerName());
        Assert.assertEquals("y.sql.ResultSet", filter.getResultSetLoggerName());
        
        filter.isDataSourceLogEnabled();
        filter.isConnectionLogEnabled();
        filter.isConnectionLogErrorEnabled();
        filter.isStatementLogEnabled();
        filter.isStatementLogErrorEnabled();
        filter.isResultSetLogEnabled();
        filter.isResultSetLogErrorEnabled();
    }
}
