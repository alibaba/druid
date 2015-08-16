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

import org.junit.Assert;
import junit.framework.TestCase;

import com.alibaba.druid.pool.DruidConnectionHolder;
import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.DruidPooledConnection;
import com.alibaba.druid.pool.DruidPooledPreparedStatement;
import com.alibaba.druid.pool.DruidPooledResultSet;
import com.alibaba.druid.pool.PreparedStatementHolder;
import com.alibaba.druid.pool.PreparedStatementPool;
import com.alibaba.druid.util.JdbcUtils;

public class PSCacheTest4 extends TestCase {

    private DruidDataSource dataSource;

    protected void setUp() throws Exception {
        dataSource = new DruidDataSource();
        dataSource.setUrl("jdbc:mock:x1");
        dataSource.setPoolPreparedStatements(true);
        dataSource.setMaxOpenPreparedStatements(50);
        dataSource.setFilters("log4j");
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

        Assert.assertEquals(0, stmtPool.size());

        PreparedStatementHolder stmtHoler_0;
        PreparedStatementHolder stmtHoler_1_A;
        PreparedStatementHolder stmtHoler_1_B;
        PreparedStatementHolder stmtHoler_1_C;

        DruidPooledPreparedStatement stmt_0;
        DruidPooledPreparedStatement stmt_1_A;
        DruidPooledPreparedStatement stmt_1_B;
        DruidPooledPreparedStatement stmt_1_C;
        DruidPooledPreparedStatement stmt_1_D;
        DruidPooledPreparedStatement stmt_1_E;
        DruidPooledPreparedStatement stmt_1_F;
        DruidPooledPreparedStatement stmt_1_G;
        DruidPooledPreparedStatement stmt_1_H;
        
        DruidPooledResultSet rs_0;
        DruidPooledResultSet rs_1_A;
        DruidPooledResultSet rs_1_B;
        DruidPooledResultSet rs_1_C;
        DruidPooledResultSet rs_1_D;
        DruidPooledResultSet rs_1_E;
        DruidPooledResultSet rs_1_F;
        DruidPooledResultSet rs_1_G;
        DruidPooledResultSet rs_1_H;

        stmt_0 = (DruidPooledPreparedStatement) conn.prepareStatement(sql_0);
        rs_0 = (DruidPooledResultSet) stmt_0.executeQuery();
        
        Assert.assertTrue(stmt_0.getPreparedStatementHolder().isInUse());

        stmt_1_A = (DruidPooledPreparedStatement) conn.prepareStatement(sql_1);
        rs_1_A = (DruidPooledResultSet) stmt_1_A.executeQuery();
        
        Assert.assertTrue(stmt_0.getPreparedStatementHolder().isInUse());
        Assert.assertTrue(stmt_1_A.getPreparedStatementHolder().isInUse());
        
        stmt_1_B = (DruidPooledPreparedStatement) conn.prepareStatement(sql_1);
        rs_1_B = (DruidPooledResultSet) stmt_1_B.executeQuery();
        rs_1_B.close();
        stmt_1_B.close();
        
        Assert.assertTrue(stmt_0.getPreparedStatementHolder().isInUse());
        Assert.assertTrue(stmt_1_A.getPreparedStatementHolder().isInUse());
        Assert.assertFalse(stmt_1_B.getPreparedStatementHolder().isInUse());
        
        stmt_1_C = (DruidPooledPreparedStatement) conn.prepareStatement(sql_1);
        rs_1_C = (DruidPooledResultSet) stmt_1_C.executeQuery();
        rs_1_C.close();
        stmt_1_C.close();
        
        Assert.assertTrue(stmt_0.getPreparedStatementHolder().isInUse());
        Assert.assertTrue(stmt_1_A.getPreparedStatementHolder().isInUse());
        Assert.assertFalse(stmt_1_B.getPreparedStatementHolder().isInUse());
        Assert.assertFalse(stmt_1_C.getPreparedStatementHolder().isInUse());
        
        stmt_1_D = (DruidPooledPreparedStatement) conn.prepareStatement(sql_1);
        rs_1_D = (DruidPooledResultSet) stmt_1_D.executeQuery();
        rs_1_D.close();
        stmt_1_D.close();
        
        Assert.assertTrue(stmt_0.getPreparedStatementHolder().isInUse());
        Assert.assertTrue(stmt_1_A.getPreparedStatementHolder().isInUse());
        Assert.assertFalse(stmt_1_B.getPreparedStatementHolder().isInUse());
        Assert.assertFalse(stmt_1_C.getPreparedStatementHolder().isInUse());
        Assert.assertFalse(stmt_1_D.getPreparedStatementHolder().isInUse());
        
        stmt_1_E = (DruidPooledPreparedStatement) conn.prepareStatement(sql_1);
        rs_1_E = (DruidPooledResultSet) stmt_1_E.executeQuery();
        rs_1_E.close();
        stmt_1_E.close();
        
        rs_1_A.close();
        stmt_1_A.close();
        
        stmt_1_F = (DruidPooledPreparedStatement) conn.prepareStatement(sql_1);
        rs_1_F = (DruidPooledResultSet) stmt_1_F.executeQuery();
        rs_1_F.close();
        stmt_1_F.close();
        
        stmt_1_G = (DruidPooledPreparedStatement) conn.prepareStatement(sql_1);
        rs_1_G = (DruidPooledResultSet) stmt_1_G.executeQuery();
        
        stmt_1_H = (DruidPooledPreparedStatement) conn.prepareStatement(sql_1);
        rs_1_H = (DruidPooledResultSet) stmt_1_H.executeQuery();
        rs_1_H.close();
        stmt_1_H.close();
        
        rs_1_G.close();
        stmt_1_G.close();
        
        conn.close();
    }
}
