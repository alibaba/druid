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


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import junit.framework.TestCase;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.DruidPooledConnection;
import com.alibaba.druid.pool.DruidPooledPreparedStatement;
import com.alibaba.druid.pool.DruidPooledResultSet;
import com.alibaba.druid.test.util.OracleMockDriver;
import com.alibaba.druid.util.JdbcUtils;
import com.alibaba.druid.wall.WallConfig;
import com.alibaba.druid.wall.WallFilter;

public class TestOracleWall2 extends TestCase {
    private DruidDataSource dataSource;

    protected void setUp() throws Exception {
        dataSource = new DruidDataSource();

        WallFilter wall = new WallFilter();
        wall.setConfig(new WallConfig());
        wall.getConfig().setWrapAllow(false);

        dataSource.setOracle(true);
        dataSource.setUrl("jdbc:mock:xxx");
        dataSource.setDriver(new OracleMockDriver());
        dataSource.setPoolPreparedStatements(true);
        dataSource.setConnectionProperties("defaultRowPrefetch=50");
        dataSource.setFilters("stat");
        dataSource.getProxyFilters().add(wall);
        dataSource.setDbType("oracle");
        // dataSource.setFilters("log4j");
    }

    protected void tearDown() throws Exception {
        JdbcUtils.close(dataSource);
    }

    public void test_oracle() throws Exception {
        String sql = "SELECT 1";

        {
            Connection conn = dataSource.getConnection();

            assertTrue(conn.isWrapperFor(DruidPooledConnection.class));
            assertNotNull(conn.unwrap(DruidPooledConnection.class));

            assertFalse(conn.isWrapperFor(oracle.jdbc.OracleConnection.class));
            assertNull(conn.unwrap(oracle.jdbc.OracleConnection.class));

            assertFalse(conn.isWrapperFor(java.sql.Connection.class));
            assertNull(conn.unwrap(java.sql.Connection.class));

            // /////////////

            PreparedStatement stmt = conn.prepareStatement(sql);

            assertNull(stmt.unwrap(oracle.jdbc.OraclePreparedStatement.class));
            assertFalse(stmt.isWrapperFor(oracle.jdbc.OraclePreparedStatement.class));

            assertTrue(stmt.isWrapperFor(DruidPooledPreparedStatement.class));
            assertNotNull(stmt.unwrap(DruidPooledPreparedStatement.class));

            assertFalse(stmt.isWrapperFor(java.sql.PreparedStatement.class));
            assertNull(stmt.unwrap(java.sql.PreparedStatement.class));

            ResultSet rs = stmt.executeQuery();

            assertNull(rs.unwrap(oracle.jdbc.OracleResultSet.class));
            assertFalse(rs.isWrapperFor(oracle.jdbc.OracleResultSet.class));

            assertTrue(rs.isWrapperFor(DruidPooledResultSet.class));
            assertNotNull(rs.unwrap(DruidPooledResultSet.class));

            assertFalse(rs.isWrapperFor(java.sql.ResultSet.class));
            assertNull(rs.unwrap(java.sql.ResultSet.class));

            rs.next();

            rs.close();
            stmt.close();
            conn.close();
        }

        for (int i = 0; i < 10; ++i) {
            Connection conn = dataSource.getConnection();

            PreparedStatement stmt = conn.prepareStatement(sql);

            ResultSet rs = stmt.executeQuery();
            rs.next();
            rs.close();
            stmt.close();

            conn.close();
        }

        assertEquals(1, dataSource.getCachedPreparedStatementCount());

    }
}
