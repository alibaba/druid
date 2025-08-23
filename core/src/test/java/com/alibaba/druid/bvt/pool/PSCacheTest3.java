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

import static org.junit.Assert.*;


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

        assertEquals(0, stmtPool.size());

        PreparedStatementHolder stmtHoler_0;
        PreparedStatementHolder stmtHoler_1;
        PreparedStatementHolder stmtHoler_2;
        PreparedStatementHolder stmtHoler_3;
        PreparedStatementHolder stmtHoler_4;

        // sql_0连续执行两次
        {
            DruidPooledPreparedStatement stmt_0 = (DruidPooledPreparedStatement) conn.prepareStatement(sql_0);

            assertFalse(stmt_0.getPreparedStatementHolder().isPooling());

            stmt_0.close();
            assertEquals(1, stmtPool.size());
            assertTrue(stmt_0.getPreparedStatementHolder().isPooling());
        }
        {
            DruidPooledPreparedStatement stmt_0 = (DruidPooledPreparedStatement) conn.prepareStatement(sql_0);
            assertEquals(1, stmtPool.size());
            assertTrue(stmt_0.getPreparedStatementHolder().isInUse());
            assertTrue(stmt_0.getPreparedStatementHolder().isPooling());

            stmt_0.close();

            assertFalse(stmt_0.getPreparedStatementHolder().isInUse());
            assertTrue(stmt_0.getPreparedStatementHolder().isPooling());
            assertEquals(1, stmtPool.size());
        }

        DruidPooledPreparedStatement stmt_0 = (DruidPooledPreparedStatement) conn.prepareStatement(sql_0);

        stmtHoler_0 = stmt_0.getPreparedStatementHolder();

        assertTrue(stmtHoler_0.isInUse());
        assertTrue(stmtHoler_0.isPooling());

        stmt_0.execute();

        {
            DruidPooledPreparedStatement stmt_1 = (DruidPooledPreparedStatement) conn.prepareStatement(sql_1);
            assertEquals(1, stmtPool.size());

            stmtHoler_1 = stmt_1.getPreparedStatementHolder();

            assertTrue(stmt_1.getPreparedStatementHolder().isInUse());
            assertFalse(stmt_1.getPreparedStatementHolder().isPooling());

            stmt_1.close();

            assertFalse(stmt_1.getPreparedStatementHolder().isInUse());
            assertTrue(stmt_1.getPreparedStatementHolder().isPooling());
            assertTrue(stmt_1.getPreparedStatementHolder().isPooling());
            assertEquals(2, stmtPool.size());
        }

        assertTrue(stmtHoler_0.isPooling());
        assertTrue(stmtHoler_1.isPooling());

        {
            DruidPooledPreparedStatement stmt_2 = (DruidPooledPreparedStatement) conn.prepareStatement(sql_2);
            assertEquals(2, stmtPool.size());

            stmtHoler_2 = stmt_2.getPreparedStatementHolder();

            assertTrue(stmt_2.getPreparedStatementHolder().isInUse());
            assertFalse(stmt_2.getPreparedStatementHolder().isPooling());

            stmt_2.close();

            assertFalse(stmt_2.getPreparedStatementHolder().isInUse());
            assertTrue(stmt_2.getPreparedStatementHolder().isPooling());
            assertTrue(stmt_2.getPreparedStatementHolder().isPooling());
            assertEquals(3, stmtPool.size());
        }

        assertTrue(stmtHoler_0.isPooling());
        assertTrue(stmtHoler_1.isPooling());
        assertTrue(stmtHoler_2.isPooling());

        {
            DruidPooledPreparedStatement stmt_3 = (DruidPooledPreparedStatement) conn.prepareStatement(sql_3);
            assertEquals(3, stmtPool.size());

            stmtHoler_3 = stmt_3.getPreparedStatementHolder();

            assertTrue(stmt_3.getPreparedStatementHolder().isInUse());
            assertFalse(stmt_3.getPreparedStatementHolder().isPooling());

            stmt_3.close();

            assertFalse(stmt_3.getPreparedStatementHolder().isInUse());
            assertTrue(stmt_3.getPreparedStatementHolder().isPooling());
            assertTrue(stmt_3.getPreparedStatementHolder().isPooling());
            assertEquals(3, stmtPool.size());
        }


        assertFalse(stmtHoler_0.isPooling());
        assertTrue(stmtHoler_1.isPooling());
        assertTrue(stmtHoler_2.isPooling());
        assertTrue(stmtHoler_3.isPooling());

        {
            DruidPooledPreparedStatement stmt_4 = (DruidPooledPreparedStatement) conn.prepareStatement(sql_4);
            assertEquals(3, stmtPool.size());

            stmtHoler_4 = stmt_4.getPreparedStatementHolder();

            assertTrue(stmt_4.getPreparedStatementHolder().isInUse());
            assertFalse(stmt_4.getPreparedStatementHolder().isPooling());

            stmt_4.close();

            assertFalse(stmt_4.getPreparedStatementHolder().isInUse());
            assertTrue(stmt_4.getPreparedStatementHolder().isPooling());
            assertTrue(stmt_4.getPreparedStatementHolder().isPooling());
            assertEquals(3, stmtPool.size());
        }

        assertFalse(stmtHoler_0.isPooling());
        assertFalse(stmtHoler_1.isPooling());
        assertTrue(stmtHoler_2.isPooling());
        assertTrue(stmtHoler_3.isPooling());
        assertTrue(stmtHoler_4.isPooling());

        stmt_0.close();

        assertTrue(stmtHoler_0.isPooling());
        assertFalse(stmtHoler_1.isPooling());
        assertFalse(stmtHoler_2.isPooling());
        assertTrue(stmtHoler_3.isPooling());
        assertTrue(stmtHoler_4.isPooling());

        conn.close();
    }
}
