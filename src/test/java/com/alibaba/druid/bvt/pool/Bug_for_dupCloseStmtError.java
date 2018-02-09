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

import com.alibaba.druid.PoolTestCase;
import com.alibaba.druid.util.JdbcUtils;
import junit.framework.TestCase;

import org.junit.Assert;

import com.alibaba.druid.mock.MockConnection;
import com.alibaba.druid.mock.MockConnectionClosedException;
import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.stat.DruidDataSourceStatManager;

public class Bug_for_dupCloseStmtError extends PoolTestCase {

    protected DruidDataSource dataSource;

    protected void setUp() throws Exception {
        super.setUp();

        dataSource = new DruidDataSource();
        dataSource.setUrl("jdbc:mock:");
        dataSource.setPoolPreparedStatements(false);
        dataSource.setTestOnBorrow(true);
    }

    protected void tearDown() throws Exception {
        dataSource.close();
        for (DruidDataSource dataSource : DruidDataSourceStatManager.getDruidDataSourceInstances()) {
            JdbcUtils.close(dataSource);
        }

        super.tearDown();
    }

    public void test_2() throws Exception {
        {
            Connection conn = dataSource.getConnection();
            PreparedStatement stmt = conn.prepareStatement("select 1");
            stmt.setString(1, "xx");

            MockConnection mockConn = conn.unwrap(MockConnection.class);
            mockConn.close();

            MockConnectionClosedException error = null;
            try {
                stmt.execute();
            } catch (MockConnectionClosedException ex) {
                error = ex;
            }
            
            Assert.assertNotNull(error);
            
            conn.close();
            stmt.close();
        }
        {
            Connection conn = dataSource.getConnection();
            PreparedStatement stmt = conn.prepareStatement("select 1");
            stmt.close();
            conn.close();
        }
        Assert.assertEquals(0, dataSource.getDupCloseCount());
    }
}
