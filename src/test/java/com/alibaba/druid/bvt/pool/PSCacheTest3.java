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

import org.junit.Assert;
import junit.framework.TestCase;

import com.alibaba.druid.pool.DruidConnectionHolder;
import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.DruidPooledConnection;
import com.alibaba.druid.pool.DruidPooledPreparedStatement;
import com.alibaba.druid.pool.PreparedStatementHolder;
import com.alibaba.druid.pool.PreparedStatementPool;
import com.alibaba.druid.util.JdbcUtils;

public class PSCacheTest3 extends TestCase {

    private DruidDataSource dataSource;

    protected void setUp() throws Exception {
        dataSource = new DruidDataSource();
        dataSource.setUrl("jdbc:mock:x1");
        dataSource.setPoolPreparedStatements(true);
        dataSource.setMaxOpenPreparedStatements(3);
    }

    protected void tearDown() throws Exception {
        JdbcUtils.close(dataSource);
    }

    public void test_pscache() throws Exception {
        DruidPooledConnection conn = dataSource.getConnection();

        DruidConnectionHolder holder = conn.getConnectionHolder();
        PreparedStatementPool stmtPool = holder.getStatementPool();

        final String sql_0 = "select 0";
        final String sql_1 = "select 1";
        final String sql_2 = "select 2";
        final String sql_3 = "select 3";
        final String sql_4 = "select 4";

        Assert.assertEquals(0, stmtPool.size());

        PreparedStatementHolder stmtHoler_0;
        PreparedStatementHolder stmtHoler_1;
        PreparedStatementHolder stmtHoler_2;
        PreparedStatementHolder stmtHoler_3;
        PreparedStatementHolder stmtHoler_4;
        
        // sql_0连续执行两次
        {
            DruidPooledPreparedStatement stmt_0 = (DruidPooledPreparedStatement) conn.prepareStatement(sql_0);

            Assert.assertFalse(stmt_0.getPreparedStatementHolder().isPooling());

            stmt_0.close();
            Assert.assertEquals(1, stmtPool.size());
            Assert.assertTrue(stmt_0.getPreparedStatementHolder().isPooling());
        }
        {
            DruidPooledPreparedStatement stmt_0 = (DruidPooledPreparedStatement) conn.prepareStatement(sql_0);
            Assert.assertEquals(1, stmtPool.size());
            Assert.assertTrue(stmt_0.getPreparedStatementHolder().isInUse());
            Assert.assertTrue(stmt_0.getPreparedStatementHolder().isPooling());

            stmt_0.close();

            Assert.assertFalse(stmt_0.getPreparedStatementHolder().isInUse());
            Assert.assertTrue(stmt_0.getPreparedStatementHolder().isPooling());
            Assert.assertEquals(1, stmtPool.size());
        }
        
        DruidPooledPreparedStatement stmt_0 = (DruidPooledPreparedStatement) conn.prepareStatement(sql_0);
        
        stmtHoler_0 = stmt_0.getPreparedStatementHolder();
        
        Assert.assertTrue(stmtHoler_0.isInUse());
        Assert.assertTrue(stmtHoler_0.isPooling());
        
        stmt_0.execute();
        
        {
            DruidPooledPreparedStatement stmt_1 = (DruidPooledPreparedStatement) conn.prepareStatement(sql_1);
            Assert.assertEquals(1, stmtPool.size());
            
            stmtHoler_1 = stmt_1.getPreparedStatementHolder();
            
            Assert.assertTrue(stmt_1.getPreparedStatementHolder().isInUse());
            Assert.assertFalse(stmt_1.getPreparedStatementHolder().isPooling());

            stmt_1.close();

            Assert.assertFalse(stmt_1.getPreparedStatementHolder().isInUse());
            Assert.assertTrue(stmt_1.getPreparedStatementHolder().isPooling());
            Assert.assertTrue(stmt_1.getPreparedStatementHolder().isPooling());
            Assert.assertEquals(2, stmtPool.size());
        }
        
        Assert.assertTrue(stmtHoler_0.isPooling());
        Assert.assertTrue(stmtHoler_1.isPooling());
        
        {
            DruidPooledPreparedStatement stmt_2 = (DruidPooledPreparedStatement) conn.prepareStatement(sql_2);
            Assert.assertEquals(2, stmtPool.size());
            
            stmtHoler_2 = stmt_2.getPreparedStatementHolder();
            
            Assert.assertTrue(stmt_2.getPreparedStatementHolder().isInUse());
            Assert.assertFalse(stmt_2.getPreparedStatementHolder().isPooling());
            
            stmt_2.close();
            
            Assert.assertFalse(stmt_2.getPreparedStatementHolder().isInUse());
            Assert.assertTrue(stmt_2.getPreparedStatementHolder().isPooling());
            Assert.assertTrue(stmt_2.getPreparedStatementHolder().isPooling());
            Assert.assertEquals(3, stmtPool.size());
        }
        
        Assert.assertTrue(stmtHoler_0.isPooling());
        Assert.assertTrue(stmtHoler_1.isPooling());
        Assert.assertTrue(stmtHoler_2.isPooling());
        
        {
            DruidPooledPreparedStatement stmt_3 = (DruidPooledPreparedStatement) conn.prepareStatement(sql_3);
            Assert.assertEquals(3, stmtPool.size());
            
            stmtHoler_3 = stmt_3.getPreparedStatementHolder();
            
            Assert.assertTrue(stmt_3.getPreparedStatementHolder().isInUse());
            Assert.assertFalse(stmt_3.getPreparedStatementHolder().isPooling());
            
            stmt_3.close();
            
            Assert.assertFalse(stmt_3.getPreparedStatementHolder().isInUse());
            Assert.assertTrue(stmt_3.getPreparedStatementHolder().isPooling());
            Assert.assertTrue(stmt_3.getPreparedStatementHolder().isPooling());
            Assert.assertEquals(3, stmtPool.size());
        }
        
        
        Assert.assertFalse(stmtHoler_0.isPooling());
        Assert.assertTrue(stmtHoler_1.isPooling());
        Assert.assertTrue(stmtHoler_2.isPooling());
        Assert.assertTrue(stmtHoler_3.isPooling());
        
        {
            DruidPooledPreparedStatement stmt_4 = (DruidPooledPreparedStatement) conn.prepareStatement(sql_4);
            Assert.assertEquals(3, stmtPool.size());
            
            stmtHoler_4 = stmt_4.getPreparedStatementHolder();
            
            Assert.assertTrue(stmt_4.getPreparedStatementHolder().isInUse());
            Assert.assertFalse(stmt_4.getPreparedStatementHolder().isPooling());
            
            stmt_4.close();
            
            Assert.assertFalse(stmt_4.getPreparedStatementHolder().isInUse());
            Assert.assertTrue(stmt_4.getPreparedStatementHolder().isPooling());
            Assert.assertTrue(stmt_4.getPreparedStatementHolder().isPooling());
            Assert.assertEquals(3, stmtPool.size());
        }
        
        Assert.assertFalse(stmtHoler_0.isPooling());
        Assert.assertFalse(stmtHoler_1.isPooling());
        Assert.assertTrue(stmtHoler_2.isPooling());
        Assert.assertTrue(stmtHoler_3.isPooling());
        Assert.assertTrue(stmtHoler_4.isPooling());
        
        stmt_0.close();
        
        Assert.assertTrue(stmtHoler_0.isPooling());
        Assert.assertFalse(stmtHoler_1.isPooling());
        Assert.assertFalse(stmtHoler_2.isPooling());
        Assert.assertTrue(stmtHoler_3.isPooling());
        Assert.assertTrue(stmtHoler_4.isPooling());

        conn.close();
    }
}
