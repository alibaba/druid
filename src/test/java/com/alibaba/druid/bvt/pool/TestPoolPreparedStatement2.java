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
package com.alibaba.druid.bvt.pool;

import java.sql.Connection;
import java.sql.PreparedStatement;

import org.junit.Assert;
import junit.framework.TestCase;

import com.alibaba.druid.mock.MockDriver;
import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.DruidPooledConnection;
import com.alibaba.druid.stat.DruidDataSourceStatManager;

public class TestPoolPreparedStatement2 extends TestCase {

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
        dataSource.setTimeBetweenEvictionRunsMillis(10); // 180 / 10
        dataSource.setTestWhileIdle(true);
        dataSource.setTestOnBorrow(false);
        dataSource.setValidationQuery("SELECT 1");
        dataSource.setFilters("stat");
        dataSource.setPoolPreparedStatements(false);
        dataSource.setMaxPoolPreparedStatementPerConnectionSize(20);
        
//        ((StatFilter) dataSource.getProxyFilters().get(0)).setMaxSqlStatCount(100);
    }

    protected void tearDown() throws Exception {
        dataSource.close();
        Assert.assertEquals(0, DruidDataSourceStatManager.getInstance().getDataSourceList().size());
    }

    public void test_stmtCache() throws Exception {
        for (int j = 0; j < 10; ++j) {
            for (int i = 0; i < 10; ++i) {
                Connection conn = dataSource.getConnection();
                String sql = "SELECT" + i;
                PreparedStatement stmt = conn.prepareStatement(sql);
                stmt.execute();
                stmt.close();
                conn.close();
            }
        }

        dataSource.setPoolPreparedStatements(true);

        for (int j = 0; j < 10; ++j) {
            for (int i = 0; i < 10; ++i) {
                Connection conn = dataSource.getConnection();
                String sql = "SELECT" + i;
                PreparedStatement stmt = conn.prepareStatement(sql);
                stmt.execute();
                stmt.close();
                conn.close();
            }
        }

        for (int i = 0; i < 10 * 1; ++i) {
            Connection conn = dataSource.getConnection();
            PreparedStatement stmt = conn.prepareStatement("SELECT " + i);
            stmt.execute();
            stmt.close();
            conn.close();
        }

        Connection conn = dataSource.getConnection();
        DruidPooledConnection poolableConn = conn.unwrap(DruidPooledConnection.class);
        Assert.assertNotNull(poolableConn);

        Assert.assertEquals(dataSource.getMaxPoolPreparedStatementPerConnectionSize(),
                            poolableConn.getConnectionHolder().getStatementPool().getMap().size());

        conn.close();

        Assert.assertEquals(0, dataSource.getActiveCount());
        Assert.assertEquals(1, dataSource.getPoolingCount());
    }
}
