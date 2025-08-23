/*
 * Copyright 1999-2018 Alibaba Group Holding Ltd.
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.sql.DriverManager;
import java.sql.Statement;
import java.util.Properties;

import junit.framework.TestCase;

import com.alibaba.druid.filter.FilterChain;
import com.alibaba.druid.proxy.DruidDriver;
import com.alibaba.druid.proxy.jdbc.ConnectionProxyImpl;
import com.alibaba.druid.proxy.jdbc.DataSourceProxyImpl;
import com.alibaba.druid.stat.JdbcStatManager;
import com.alibaba.druid.util.Utils;

public class WrapImplTest extends TestCase {
    private static String url = "jdbc:wrap-jdbc:filters=default,commonLogging,log4j,encoding,null:name=demo:jdbc:derby:classpath:petstore-db";

    public void test_clone() throws Exception {
        Class.forName("com.alibaba.druid.proxy.DruidDriver");

        DruidDriver driver = (DruidDriver) DriverManager.getDriver(url);

        ConnectionProxyImpl connection = (ConnectionProxyImpl) driver.connect(url, new Properties());

        connection.getRawObject();

        FilterChain filterChain = (FilterChain) connection.createChain();
        filterChain.cloneChain();

        DataSourceProxyImpl dataSource = (DataSourceProxyImpl) connection.getDirectDataSource();
        dataSource.getId();
        assertEquals(4, dataSource.getProxyFilters().size());
        assertEquals(4, dataSource.getFilterClasses().length);
        assertNotNull(dataSource.getCreatedTime());
        assertTrue(dataSource.getCreatedTime().getTime() != 0);
        assertEquals("org.apache.derby.jdbc.EmbeddedDriver", dataSource.getRawDriverClassName());

        assertEquals(url, dataSource.getUrl());
        assertEquals("jdbc:derby:classpath:petstore-db", dataSource.getRawUrl());
        assertEquals(10, dataSource.getRawDriverMajorVersion());
        assertEquals(12, dataSource.getRawDriverMinorVersion());

        Class<?> mysql5ConnectionClass = Utils.loadClass("com.mysql.jdbc.Connection");
        if (mysql5ConnectionClass != null) {
            assertFalse(connection.isWrapperFor(mysql5ConnectionClass));
        }
        assertTrue(connection.isWrapperFor(ConnectionProxyImpl.class));
        assertTrue(connection.isWrapperFor(org.apache.derby.impl.jdbc.EmbedConnection.class));
        assertNotNull(connection.unwrap(ConnectionProxyImpl.class));
        assertNull(connection.unwrap(null));

        org.apache.derby.impl.jdbc.EmbedConnection derbyConnection = connection.unwrap(org.apache.derby.impl.jdbc.EmbedConnection.class);
        assertNotNull(derbyConnection);

        Statement statement = connection.createStatement();
        if (mysql5ConnectionClass != null) {
            assertFalse(statement.isWrapperFor(Class.forName("com.mysql.jdbc.Statement")));
        }
        assertFalse(statement.isWrapperFor(null));
        assertTrue(statement.isWrapperFor(org.apache.derby.impl.jdbc.EmbedStatement.class));

        org.apache.derby.impl.jdbc.EmbedStatement rayStatement = statement.unwrap(org.apache.derby.impl.jdbc.EmbedStatement.class);
        assertNotNull(rayStatement);
        statement.close();
    }

    protected void tearDown() throws Exception {
        DruidDriver.getProxyDataSources().clear();
        assertEquals(0, JdbcStatManager.getInstance().getSqlList().size());
    }
}
