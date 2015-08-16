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
package com.alibaba.druid.bvt.pool.basic;

import java.sql.SQLException;
import java.util.Properties;

import javax.sql.ConnectionEvent;
import javax.sql.ConnectionEventListener;

import org.junit.Assert;
import junit.framework.TestCase;

import com.alibaba.druid.mock.MockDriver;
import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.DruidPooledConnection;
import com.alibaba.druid.stat.DruidDataSourceStatManager;
import com.alibaba.druid.stat.JdbcStatContext;
import com.alibaba.druid.stat.JdbcStatManager;

public class ConnectionTest5 extends TestCase {

    private MockDriver      driver;
    private DruidDataSource dataSource;

    protected void setUp() throws Exception {
        DruidDataSourceStatManager.clear();

        driver = new MockDriver();

        dataSource = new DruidDataSource();
        dataSource.setUrl("jdbc:mock:xxx");
        dataSource.setDriver(driver);
        dataSource.setInitialSize(1);
        dataSource.setMaxActive(2);
        dataSource.setMaxIdle(2);
        dataSource.setMinIdle(1);
        dataSource.setMinEvictableIdleTimeMillis(300 * 1000); // 300 / 10
        dataSource.setTimeBetweenEvictionRunsMillis(180 * 1000); // 180 / 10
        dataSource.setTestWhileIdle(true);
        dataSource.setTestOnBorrow(true);
        dataSource.setTestOnReturn(true);
        dataSource.setValidationQuery("SELECT 1");
        dataSource.setFilters("stat,trace");

        JdbcStatContext context = new JdbcStatContext();
        context.setTraceEnable(true);
        JdbcStatManager.getInstance().setStatContext(context);
    }

    protected void tearDown() throws Exception {
        dataSource.close();
        Assert.assertEquals(0, DruidDataSourceStatManager.getInstance().getDataSourceList().size());

        JdbcStatManager.getInstance().setStatContext(null);
    }

    public void test_basic() throws Exception {
        DruidPooledConnection conn = (DruidPooledConnection) dataSource.getConnection();
        conn.close();

        Assert.assertEquals(true, dataSource.isResetStatEnable());
        dataSource.setResetStatEnable(false);
        Assert.assertEquals(false, dataSource.isResetStatEnable());
        Assert.assertEquals(1, dataSource.getConnectCount());
        dataSource.resetStat();
        Assert.assertEquals(1, dataSource.getConnectCount());

        dataSource.setResetStatEnable(true);
        dataSource.resetStat();
        Assert.assertEquals(0, dataSource.getConnectCount());

    }

    public void test_handleException() throws Exception {
        DruidPooledConnection conn = (DruidPooledConnection) dataSource.getConnection();
        conn.close();

        SQLException error = new SQLException();
        try {
            conn.handleException(error);
        } catch (SQLException ex) {
            Assert.assertEquals(error, ex);
        }
    }

    public void test_handleException_2() throws Exception {
        DruidPooledConnection conn = dataSource.getConnection().unwrap(DruidPooledConnection.class);
        conn.getConnection().close();

        {
            SQLException error = null;
            try {
                conn.handleException(new RuntimeException());
            } catch (SQLException ex) {
                error = ex;
            }
            Assert.assertNotNull(error);
        }

        conn.close();
    }

    public void test_handleException_3() throws Exception {
        DruidPooledConnection conn = dataSource.getConnection().unwrap(DruidPooledConnection.class);
        conn.getConnection().close();

        {
            SQLException error = null;
            try {
                conn.handleException(new RuntimeException());
            } catch (SQLException ex) {
                error = ex;
            }
            Assert.assertNotNull(error);
        }

        conn.close();
    }

    public void test_handleException_4() throws Exception {
        DruidPooledConnection conn = dataSource.getConnection().unwrap(DruidPooledConnection.class);
        conn.getConnection().close();

        {
            SQLException error = null;
            try {
                conn.prepareStatement("SELECT 1");
            } catch (SQLException ex) {
                error = ex;
            }
            Assert.assertNotNull(error);
        }

        Assert.assertEquals(true, conn.isClosed());
    }

    public void test_handleException_5() throws Exception {
        DruidPooledConnection conn = dataSource.getConnection().unwrap(DruidPooledConnection.class);
        conn.addConnectionEventListener(new ConnectionEventListener() {

            @Override
            public void connectionClosed(ConnectionEvent event) {

            }

            @Override
            public void connectionErrorOccurred(ConnectionEvent event) {

            }

        });
        conn.close();

        {
            SQLException error = null;
            try {
                conn.handleException(new RuntimeException());
            } catch (SQLException ex) {
                error = ex;
            }
            Assert.assertNotNull(error);
        }

    }

    public void test_setClientInfo() throws Exception {
        DruidPooledConnection conn = dataSource.getConnection().unwrap(DruidPooledConnection.class);
        conn.close();

        {
            SQLException error = null;
            try {
                conn.setClientInfo("name", "xxx");
            } catch (SQLException ex) {
                error = ex;
            }
            Assert.assertNotNull(error);
        }

    }

    public void test_setClientInfo_1() throws Exception {
        DruidPooledConnection conn = dataSource.getConnection().unwrap(DruidPooledConnection.class);
        conn.close();

        {
            SQLException error = null;
            try {
                conn.setClientInfo(new Properties());
            } catch (SQLException ex) {
                error = ex;
            }
            Assert.assertNotNull(error);
        }

    }
}
