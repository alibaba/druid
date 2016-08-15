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
import java.sql.PreparedStatement;

import org.junit.Assert;
import junit.framework.TestCase;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.DruidPooledPreparedStatement;
import com.alibaba.druid.util.JdbcUtils;

public class PSCacheTest2 extends TestCase {

    private DruidDataSource dataSource;

    protected void setUp() throws Exception {
        dataSource = new DruidDataSource();
        dataSource.setUrl("jdbc:mock:x1");
        dataSource.setPoolPreparedStatements(true);
        dataSource.setMaxOpenPreparedStatements(10);
        dataSource.setSharePreparedStatements(true);
    }

    protected void tearDown() throws Exception {
        JdbcUtils.close(dataSource);
    }

    public void test_0() throws Exception {
        Connection conn = dataSource.getConnection();

        String sql = "select 1";

        {
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.close();
        }

        PreparedStatement stmt0 = conn.prepareStatement(sql);
        DruidPooledPreparedStatement pooledStmt0 = (DruidPooledPreparedStatement) stmt0;

        Assert.assertEquals(1, pooledStmt0.getPreparedStatementHolder().getInUseCount());

        PreparedStatement stmt1 = conn.prepareStatement(sql);
        DruidPooledPreparedStatement pooledStmt1 = (DruidPooledPreparedStatement) stmt1;

        Assert.assertSame(pooledStmt0.getPreparedStatementHolder(), pooledStmt1.getPreparedStatementHolder());

        stmt0.close();
        stmt1.close();

        conn.close();
    }

    public void test_txn() throws Exception {
        Connection conn = dataSource.getConnection();
        
        conn.setAutoCommit(true);

        String sql = "select 1";

        {
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.close();
        }

        PreparedStatement stmt0 = conn.prepareStatement(sql);
        DruidPooledPreparedStatement pooledStmt0 = (DruidPooledPreparedStatement) stmt0;

        Assert.assertEquals(1, pooledStmt0.getPreparedStatementHolder().getInUseCount());

        PreparedStatement stmt1 = conn.prepareStatement(sql);
        DruidPooledPreparedStatement pooledStmt1 = (DruidPooledPreparedStatement) stmt1;

        Assert.assertSame(pooledStmt0.getPreparedStatementHolder(), pooledStmt1.getPreparedStatementHolder());

        stmt0.close();
        stmt1.close();

        conn.close();
    }
}
