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
package com.alibaba.druid.bvt.pool;

import java.sql.Connection;

import javax.management.openmbean.TabularData;

import junit.framework.TestCase;

import org.junit.Assert;

import com.alibaba.druid.mock.MockConnection;
import com.alibaba.druid.mock.MockDriver;
import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.stat.DruidDataSourceStatManager;
import com.alibaba.druid.util.JdbcUtils;

public class TestIdle extends TestCase {

    private DruidDataSource dataSource;

    protected void tearDown() throws Exception {
        JdbcUtils.close(dataSource);
        DruidDataSourceStatManager.clear();
    }

    protected void setUp() throws Exception {
        TabularData dataSourceList = DruidDataSourceStatManager.getInstance().getDataSourceList();
        if (dataSourceList.size() > 0) {
            DruidDataSource first = DruidDataSourceStatManager.getInstance().getDruidDataSourceInstances().iterator().next();
            System.out.println(first.getInitStackTrace());
        }
        Assert.assertEquals(0, dataSourceList.size());
    }

    public void test_idle() throws Exception {
        MockDriver driver = new MockDriver();

        dataSource = new DruidDataSource();
        dataSource.setUrl("jdbc:mock:xxx");
        dataSource.setDriver(driver);
        dataSource.setInitialSize(2);
        dataSource.setMaxActive(4);
        // dataSource.setMaxIdle(4);
        dataSource.setMinIdle(1);
        dataSource.setMinEvictableIdleTimeMillis(50 * 1);
        dataSource.setTimeBetweenEvictionRunsMillis(10);
        dataSource.setTestWhileIdle(true);
        dataSource.setTestOnBorrow(false);
        dataSource.setValidationQuery("SELECT 1");

        {
            Assert.assertEquals(0, dataSource.getCreateCount());
            Assert.assertEquals(0, dataSource.getActiveCount());

            Connection conn = dataSource.getConnection();

            Assert.assertEquals(dataSource.getInitialSize(), dataSource.getCreateCount());
            Assert.assertEquals(dataSource.getInitialSize(), driver.getConnections().size());
            Assert.assertEquals(1, dataSource.getActiveCount());

            conn.close();
            Assert.assertEquals(0, dataSource.getDestroyCount());
            Assert.assertEquals(dataSource.getInitialSize(), driver.getConnections().size());
            Assert.assertEquals(0, dataSource.getActiveCount());
        }

        {
            int count = 4;
            Connection[] connections = new Connection[4];
            for (int i = 0; i < count; ++i) {
                connections[i] = dataSource.getConnection();
                Assert.assertEquals(i + 1, dataSource.getActiveCount());
            }
            Assert.assertEquals(dataSource.getMaxActive(), dataSource.getCreateCount());
            Assert.assertEquals(4, driver.getConnections().size());
            for (int i = 0; i < count; ++i) {
                connections[i].close();
                Assert.assertEquals(count - i - 1, dataSource.getActiveCount());
            }
            Assert.assertEquals(dataSource.getMaxActive(), dataSource.getCreateCount());
            Assert.assertEquals(0, dataSource.getActiveCount());
            Assert.assertEquals(4, driver.getConnections().size());

            Thread.sleep(dataSource.getMinEvictableIdleTimeMillis() * 2);
            Assert.assertEquals(dataSource.getMinIdle(), driver.getConnections().size());
        }

        System.out.println("----------raw close all connection");
        for (MockConnection rawConn : driver.getConnections()) {
            rawConn.close();
        }

        Thread.sleep(dataSource.getMinEvictableIdleTimeMillis() * 2);
        Assert.assertEquals(0, driver.getConnections().size());
        Assert.assertEquals(1, dataSource.getPoolingCount());
        {
            Connection conn = dataSource.getConnection();
            Assert.assertEquals(1, dataSource.getActiveCount());
            Assert.assertEquals(dataSource.getMinIdle(), driver.getConnections().size());
            conn.close();
            Assert.assertEquals(dataSource.getMinIdle(), driver.getConnections().size());
            Assert.assertEquals(0, dataSource.getActiveCount());
        }

        {
            int count = 4;
            Connection[] connections = new Connection[4];
            for (int i = 0; i < count; ++i) {
                connections[i] = dataSource.getConnection();
                Assert.assertEquals(i + 1, dataSource.getActiveCount());
            }
            Assert.assertEquals(4, driver.getConnections().size());
            for (int i = 0; i < count; ++i) {
                connections[i].close();
                Assert.assertEquals(count - i - 1, dataSource.getActiveCount());
            }
            Assert.assertEquals(4, driver.getConnections().size());
            Assert.assertEquals("activeCount not zero", 0, dataSource.getActiveCount());

            dataSource.shrink();
            Assert.assertEquals("activeCount not zero", 0, dataSource.getActiveCount());
            Assert.assertEquals("minIdle not equal physical", dataSource.getMinIdle(), driver.getConnections().size());
        }

    }
}
