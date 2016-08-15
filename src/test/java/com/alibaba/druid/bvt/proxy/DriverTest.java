/*
 * Copyright 1999-2101 Alibaba Group Holding Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.alibaba.druid.bvt.proxy;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.util.Properties;

import junit.framework.TestCase;

import org.junit.Assert;

import com.alibaba.druid.proxy.DruidDriver;
import com.alibaba.druid.proxy.jdbc.ConnectionProxy;
import com.alibaba.druid.proxy.jdbc.DataSourceProxy;
import com.alibaba.druid.stat.JdbcStatManager;

public class DriverTest extends TestCase {
    protected void tearDown() throws Exception {
        DruidDriver.getProxyDataSources().clear();
        Assert.assertEquals(0, JdbcStatManager.getInstance().getSqlList().size());
    }
    
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
        Assert.assertEquals(1, dataSource_1.getProxyFilters().size());

        {
            Connection connection_2 = DriverManager.getConnection(url_2);
            ConnectionProxy connection_wrapper_2 = connection_2.unwrap(ConnectionProxy.class);
            DataSourceProxy dataSource_2 = connection_wrapper_2.getDirectDataSource();
            Assert.assertEquals(1, dataSource_2.getProxyFilters().size());
        }

        {
            Connection connection_3 = DriverManager.getConnection(url_3);
            ConnectionProxy connection_wrapper_3 = connection_3.unwrap(ConnectionProxy.class);
            DataSourceProxy dataSource_3 = connection_wrapper_3.getDirectDataSource();
            Assert.assertEquals(1, dataSource_3.getProxyFilters().size());
        }

        {
            Connection connection_4 = DriverManager.getConnection(url_4);
            ConnectionProxy connection_wrapper_4 = connection_4.unwrap(ConnectionProxy.class);
            DataSourceProxy dataSource_4 = connection_wrapper_4.getDirectDataSource();
            dataSource_4.getProxyFilters().toString();
            Assert.assertEquals(1, dataSource_4.getProxyFilters().size());
        }
    }

}
