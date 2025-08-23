package com.alibaba.druid.bvt.filter.log;

import static org.junit.Assert.assertEquals;

import junit.framework.TestCase;

import org.slf4j.LoggerFactory;

import com.alibaba.druid.filter.logging.Slf4jLogFilter;


public class Slf4jLogFilterTest extends TestCase {
    public void test_slf4j() throws Exception {
        Slf4jLogFilter filter = new Slf4jLogFilter();
        assertEquals("druid.sql.DataSource", filter.getDataSourceLoggerName());
        assertEquals("druid.sql.Connection", filter.getConnectionLoggerName());
        assertEquals("druid.sql.Statement", filter.getStatementLoggerName());
        assertEquals("druid.sql.ResultSet", filter.getResultSetLoggerName());

        filter.setDataSourceLoggerName("x.sql.DataSource");
        filter.setConnectionLoggerName("x.sql.Connection");
        filter.setStatementLoggerName("x.sql.Statement");
        filter.setResultSetLoggerName("x.sql.ResultSet");

        assertEquals("x.sql.DataSource", filter.getDataSourceLoggerName());
        assertEquals("x.sql.Connection", filter.getConnectionLoggerName());
        assertEquals("x.sql.Statement", filter.getStatementLoggerName());
        assertEquals("x.sql.ResultSet", filter.getResultSetLoggerName());

        filter.setDataSourceLogger(LoggerFactory.getLogger("y.sql.DataSource"));
        filter.setConnectionLogger(LoggerFactory.getLogger("y.sql.Connection"));
        filter.setStatementLogger(LoggerFactory.getLogger("y.sql.Statement"));
        filter.setResultSetLogger(LoggerFactory.getLogger("y.sql.ResultSet"));

        assertEquals("y.sql.DataSource", filter.getDataSourceLoggerName());
        assertEquals("y.sql.Connection", filter.getConnectionLoggerName());
        assertEquals("y.sql.Statement", filter.getStatementLoggerName());
        assertEquals("y.sql.ResultSet", filter.getResultSetLoggerName());

        filter.isDataSourceLogEnabled();
        filter.isConnectionLogEnabled();
        filter.isConnectionLogErrorEnabled();
        filter.isStatementLogEnabled();
        filter.isStatementLogErrorEnabled();
        filter.isResultSetLogEnabled();
        filter.isResultSetLogErrorEnabled();
    }
}
