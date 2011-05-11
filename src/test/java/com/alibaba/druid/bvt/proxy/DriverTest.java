package com.alibaba.druid.bvt.proxy;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.util.Properties;

import junit.framework.Assert;
import junit.framework.TestCase;

import com.alibaba.druid.proxy.DruidDriver;
import com.alibaba.druid.proxy.jdbc.ConnectionProxy;
import com.alibaba.druid.proxy.jdbc.DataSourceProxy;

public class DriverTest extends TestCase {

    public void test_driver() throws Exception {
        String url_0 = "jdbc:wrap-jdbc:filters=default:name=driverTest:jdbc:derby:memory:driverTestDB;create=true";
        String url_1 = "jdbc:wrap-jdbc:filters=counter:name=driverTest:jdbc:derby:memory:driverTestDB;create=true";
        String url_2 = "jdbc:wrap-jdbc:filters=log4j:name=driverTest:jdbc:derby:memory:driverTestDB;create=true";
        String url_3 = "jdbc:wrap-jdbc:filters=commonLogging:name=driverTest:jdbc:derby:memory:driverTestDB;create=true";
        String url_4 = "jdbc:wrap-jdbc:driver=org.apache.derby.jdbc.EmbeddedDriver:filters=commonLogging:name=driverTest:jdbc:derby:memory:driverTestDB;create=true";

        Class.forName("com.alibaba.druid.proxy.DruidDriver");

        Connection connection = DriverManager.getConnection(url_0);
        connection.close();

        Driver driver = DriverManager.getDriver(url_0);

        DruidDriver driverWrapper = (DruidDriver) driver;

        Assert.assertEquals(4, driverWrapper.getMajorVersion());
        Assert.assertEquals(0, driverWrapper.getMinorVersion());
        Assert.assertEquals(true, driverWrapper.jdbcCompliant());

        Assert.assertTrue(driverWrapper.getConnectCount() > 0);

        Assert.assertNotNull(DruidDriver.getInstance());

        Assert.assertEquals("jdbc:wrap-jdbc:", driverWrapper.getAcceptPrefix());

        Assert.assertTrue(driverWrapper.getDataSourceUrls().length > 0);

        driverWrapper.getPropertyInfo(url_0, new Properties());

        Assert.assertFalse(driverWrapper.acceptsURL(null));
        Assert.assertFalse(driverWrapper.acceptsURL("xxx"));
        Assert.assertTrue(driverWrapper.acceptsURL(url_1));

        Connection connection_1 = DriverManager.getConnection(url_1);
        ConnectionProxy connection_wrapper_1 = connection_1.unwrap(ConnectionProxy.class);
        DataSourceProxy dataSource_1 = connection_wrapper_1.getDirectDataSource();
        Assert.assertEquals(1, dataSource_1.getFilters().size());

        {
            Connection connection_2 = DriverManager.getConnection(url_2);
            ConnectionProxy connection_wrapper_2 = connection_2.unwrap(ConnectionProxy.class);
            DataSourceProxy dataSource_2 = connection_wrapper_2.getDirectDataSource();
            Assert.assertEquals(1, dataSource_2.getFilters().size());
        }

        {
            Connection connection_3 = DriverManager.getConnection(url_3);
            ConnectionProxy connection_wrapper_3 = connection_3.unwrap(ConnectionProxy.class);
            DataSourceProxy dataSource_3 = connection_wrapper_3.getDirectDataSource();
            Assert.assertEquals(1, dataSource_3.getFilters().size());
        }

        {
            Connection connection_4 = DriverManager.getConnection(url_4);
            ConnectionProxy connection_wrapper_4 = connection_4.unwrap(ConnectionProxy.class);
            DataSourceProxy dataSource_4 = connection_wrapper_4.getDirectDataSource();
            dataSource_4.getFilters().toString();
            Assert.assertEquals(1, dataSource_4.getFilters().size());
        }
    }

}
