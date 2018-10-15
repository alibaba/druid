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
package com.alibaba.druid.bvt.pool.basic;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;

import com.alibaba.druid.PoolTestCase;
import org.junit.Assert;
import junit.framework.TestCase;

import com.alibaba.druid.mock.MockDriver;
import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.DruidPooledConnection;
import com.alibaba.druid.stat.DruidDataSourceStatManager;
import com.alibaba.druid.stat.JdbcStatContext;
import com.alibaba.druid.stat.JdbcStatManager;

public class ConnectionTest3 extends PoolTestCase {

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
        dataSource.setTestOnBorrow(false);
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

        super.tearDown();
    }

    public void test_basic() throws Exception {
        DruidPooledConnection conn = (DruidPooledConnection) dataSource.getConnection();

        conn.getTransactionInfo();
        conn.getMetaData();
        conn.setReadOnly(true);
        Assert.assertEquals(true, conn.isReadOnly());

        conn.setCatalog("xxx");
        Assert.assertEquals("xxx", conn.getCatalog());

        conn.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
        Assert.assertEquals(Connection.TRANSACTION_READ_COMMITTED, conn.getTransactionIsolation());

        conn.getWarnings();
        conn.clearWarnings();
        conn.getTypeMap();
        conn.setTypeMap(null);

        conn.setHoldability(ResultSet.CLOSE_CURSORS_AT_COMMIT);
        Assert.assertEquals(ResultSet.CLOSE_CURSORS_AT_COMMIT, conn.getHoldability());

        conn.setSavepoint();
        conn.setSavepoint("savepoint");
        conn.rollback();
        {
            Exception error = null;
            try {
                conn.rollback(null);
            } catch (SQLException e) {
                error = e;
            }
            Assert.assertNotNull(error);
        }
        {
            Exception error = null;
            try {
                conn.releaseSavepoint(null);
            } catch (SQLException e) {
                error = e;
            }
            Assert.assertNotNull(error);
        }
        conn.createBlob();
        conn.createClob();
        conn.createNClob();
        conn.createSQLXML();
        conn.isValid(200);
        conn.setClientInfo(new Properties());
        conn.setClientInfo("xx", "11");
        conn.getClientInfo("xx");
        conn.getClientInfo();

        conn.createArrayOf("int", new Object[0]);
        conn.createStruct("int", new Object[0]);

        conn.addConnectionEventListener(null);
        conn.removeConnectionEventListener(null);
        conn.addStatementEventListener(null);
        conn.removeStatementEventListener(null);

        conn.close();
    }

}
