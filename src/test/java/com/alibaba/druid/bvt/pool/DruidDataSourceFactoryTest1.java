package com.alibaba.druid.bvt.pool;

import java.sql.Connection;
import java.util.Properties;

import org.junit.Assert;
import junit.framework.TestCase;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.DruidDataSourceFactory;
import com.alibaba.druid.util.JdbcUtils;

public class DruidDataSourceFactoryTest1 extends TestCase {

    public void test_NONE() throws Exception {
        Properties properties = new Properties();
        properties.put(DruidDataSourceFactory.PROP_URL, "jdbc:mock:xxx");
        properties.put(DruidDataSourceFactory.PROP_DEFAULTTRANSACTIONISOLATION, "NONE");

        DruidDataSource dataSource = null;

        try {
            dataSource = (DruidDataSource) DruidDataSourceFactory.createDataSource(properties);

            Assert.assertEquals("jdbc:mock:xxx", dataSource.getUrl());
            Assert.assertEquals(Connection.TRANSACTION_NONE, dataSource.getDefaultTransactionIsolation().intValue());

        } finally {
            JdbcUtils.close(dataSource);
        }
    }

    public void test_READ_COMMITTED() throws Exception {
        Properties properties = new Properties();
        properties.put(DruidDataSourceFactory.PROP_URL, "jdbc:mock:xxx");
        properties.put(DruidDataSourceFactory.PROP_DEFAULTTRANSACTIONISOLATION, "READ_COMMITTED");

        DruidDataSource dataSource = null;

        try {
            dataSource = (DruidDataSource) DruidDataSourceFactory.createDataSource(properties);

            Assert.assertEquals("jdbc:mock:xxx", dataSource.getUrl());
            Assert.assertEquals(Connection.TRANSACTION_READ_COMMITTED,
                                dataSource.getDefaultTransactionIsolation().intValue());

        } finally {
            JdbcUtils.close(dataSource);
        }
    }

    public void test_READ_UNCOMMITTED() throws Exception {
        Properties properties = new Properties();
        properties.put(DruidDataSourceFactory.PROP_URL, "jdbc:mock:xxx");
        properties.put(DruidDataSourceFactory.PROP_DEFAULTTRANSACTIONISOLATION, "READ_UNCOMMITTED");

        DruidDataSource dataSource = null;

        try {
            dataSource = (DruidDataSource) DruidDataSourceFactory.createDataSource(properties);

            Assert.assertEquals("jdbc:mock:xxx", dataSource.getUrl());
            Assert.assertEquals(Connection.TRANSACTION_READ_UNCOMMITTED,
                                dataSource.getDefaultTransactionIsolation().intValue());

        } finally {
            JdbcUtils.close(dataSource);
        }
    }

    public void test_REPEATABLE_READ() throws Exception {
        Properties properties = new Properties();
        properties.put(DruidDataSourceFactory.PROP_URL, "jdbc:mock:xxx");
        properties.put(DruidDataSourceFactory.PROP_DEFAULTTRANSACTIONISOLATION, "REPEATABLE_READ");

        DruidDataSource dataSource = null;

        try {
            dataSource = (DruidDataSource) DruidDataSourceFactory.createDataSource(properties);

            Assert.assertEquals("jdbc:mock:xxx", dataSource.getUrl());
            Assert.assertEquals(Connection.TRANSACTION_REPEATABLE_READ,
                                dataSource.getDefaultTransactionIsolation().intValue());

        } finally {
            JdbcUtils.close(dataSource);
        }
    }

    public void test_SERIALIZABLE() throws Exception {
        Properties properties = new Properties();
        properties.put(DruidDataSourceFactory.PROP_URL, "jdbc:mock:xxx");
        properties.put(DruidDataSourceFactory.PROP_DEFAULTTRANSACTIONISOLATION, "SERIALIZABLE");

        DruidDataSource dataSource = null;

        try {
            dataSource = (DruidDataSource) DruidDataSourceFactory.createDataSource(properties);

            Assert.assertEquals("jdbc:mock:xxx", dataSource.getUrl());
            Assert.assertEquals(Connection.TRANSACTION_SERIALIZABLE,
                                dataSource.getDefaultTransactionIsolation().intValue());

        } finally {
            JdbcUtils.close(dataSource);
        }
    }

    public void test_other() throws Exception {
        Properties properties = new Properties();
        properties.put(DruidDataSourceFactory.PROP_URL, "jdbc:mock:xxx");
        properties.put(DruidDataSourceFactory.PROP_DEFAULTTRANSACTIONISOLATION, "xxx");

        DruidDataSource dataSource = null;

        try {
            dataSource = (DruidDataSource) DruidDataSourceFactory.createDataSource(properties);

            Assert.assertEquals("jdbc:mock:xxx", dataSource.getUrl());
            Assert.assertEquals(-1, dataSource.getDefaultTransactionIsolation().intValue());

        } finally {
            JdbcUtils.close(dataSource);
        }
    }

    public void test_integer() throws Exception {
        Properties properties = new Properties();
        properties.put(DruidDataSourceFactory.PROP_URL, "jdbc:mock:xxx");
        properties.put(DruidDataSourceFactory.PROP_DEFAULTTRANSACTIONISOLATION,
                       Integer.toString(Connection.TRANSACTION_SERIALIZABLE));

        DruidDataSource dataSource = null;

        try {
            dataSource = (DruidDataSource) DruidDataSourceFactory.createDataSource(properties);

            Assert.assertEquals("jdbc:mock:xxx", dataSource.getUrl());
            Assert.assertEquals(Connection.TRANSACTION_SERIALIZABLE,
                                dataSource.getDefaultTransactionIsolation().intValue());

        } finally {
            JdbcUtils.close(dataSource);
        }
    }

    public void test_init() throws Exception {
        Properties properties = new Properties();
        properties.put(DruidDataSourceFactory.PROP_URL, "jdbc:mock:xxx");
        properties.put(DruidDataSourceFactory.PROP_INIT, "true");

        DruidDataSource dataSource = null;

        try {
            dataSource = (DruidDataSource) DruidDataSourceFactory.createDataSource(properties);

            Assert.assertEquals("jdbc:mock:xxx", dataSource.getUrl());
            Assert.assertTrue(dataSource.isInited());
        } finally {
            JdbcUtils.close(dataSource);
        }
    }

    public void test_getObjectInstance() throws Exception {
        Assert.assertNull(new DruidDataSourceFactory().getObjectInstance(null, null, null, null));
    }
    
    public void test_getObjectInstance_1() throws Exception {
        Assert.assertNull(new DruidDataSourceFactory().getObjectInstance(new Object(), null, null, null));
    }
}
