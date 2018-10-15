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
import java.sql.ResultSet;

import org.junit.Assert;
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

            Assert.assertTrue(conn.isWrapperFor(DruidPooledConnection.class));
            Assert.assertNotNull(conn.unwrap(DruidPooledConnection.class));

            Assert.assertFalse(conn.isWrapperFor(oracle.jdbc.OracleConnection.class));
            Assert.assertNull(conn.unwrap(oracle.jdbc.OracleConnection.class));
            
            Assert.assertFalse(conn.isWrapperFor(java.sql.Connection.class));
            Assert.assertNull(conn.unwrap(java.sql.Connection.class));

            // /////////////

            PreparedStatement stmt = conn.prepareStatement(sql);

            Assert.assertNull(stmt.unwrap(oracle.jdbc.OraclePreparedStatement.class));
            Assert.assertFalse(stmt.isWrapperFor(oracle.jdbc.OraclePreparedStatement.class));

            Assert.assertTrue(stmt.isWrapperFor(DruidPooledPreparedStatement.class));
            Assert.assertNotNull(stmt.unwrap(DruidPooledPreparedStatement.class));
            
            Assert.assertFalse(stmt.isWrapperFor(java.sql.PreparedStatement.class));
            Assert.assertNull(stmt.unwrap(java.sql.PreparedStatement.class));

            ResultSet rs = stmt.executeQuery();

            Assert.assertNull(rs.unwrap(oracle.jdbc.OracleResultSet.class));
            Assert.assertFalse(rs.isWrapperFor(oracle.jdbc.OracleResultSet.class));
            
            Assert.assertTrue(rs.isWrapperFor(DruidPooledResultSet.class));
            Assert.assertNotNull(rs.unwrap(DruidPooledResultSet.class));
            
            Assert.assertFalse(rs.isWrapperFor(java.sql.ResultSet.class));
            Assert.assertNull(rs.unwrap(java.sql.ResultSet.class));

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

        Assert.assertEquals(1, dataSource.getCachedPreparedStatementCount());

    }
}
