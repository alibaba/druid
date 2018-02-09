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
import oracle.jdbc.OracleConnection;
import oracle.jdbc.internal.OraclePreparedStatement;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.PreparedStatementHolder;
import com.alibaba.druid.test.util.OracleMockDriver;
import com.alibaba.druid.util.JdbcUtils;

public class TestOraclePrefetch extends TestCase {

    private DruidDataSource dataSource;

    protected void setUp() throws Exception {
        dataSource = new DruidDataSource();
        dataSource.setOracle(true);
        dataSource.setUrl("jdbc:mock:xxx");
        dataSource.setDriver(new OracleMockDriver());
        dataSource.setPoolPreparedStatements(true);
        dataSource.setConnectionProperties("defaultRowPrefetch=50");
//        dataSource.setFilters("log4j");
    }

    protected void tearDown() throws Exception {
        JdbcUtils.close(dataSource);
    }

    public void test_oracle() throws Exception {

        String sql = "SELECT 1";

        OracleConnection oracleConn;
        OraclePreparedStatement oracleStmt;
        PreparedStatementHolder stmtHolder;
        {
            Connection conn = dataSource.getConnection();

            {
                oracleConn = conn.unwrap(OracleConnection.class);
                Assert.assertEquals(50, oracleConn.getDefaultRowPrefetch());
            }

            PreparedStatement stmt = conn.prepareStatement(sql);

            oracleStmt = stmt.unwrap(OraclePreparedStatement.class);
            Assert.assertEquals(50, oracleStmt.getRowPrefetch());
            
            Assert.assertTrue(stmt.isWrapperFor(PreparedStatementHolder.class));
            stmtHolder = stmt.unwrap(PreparedStatementHolder.class);
            Assert.assertNotNull(stmtHolder);
            Assert.assertEquals(0, stmtHolder.getHitCount());

            ResultSet rs = stmt.executeQuery();
            rs.next();

            rs.close();
            stmt.close();
            conn.close();
        }

        {
            Connection conn = dataSource.getConnection();

            {
                OracleConnection oracleConn2 = conn.unwrap(OracleConnection.class);
                Assert.assertEquals(50, oracleConn2.getDefaultRowPrefetch());
                Assert.assertSame(oracleConn, oracleConn2);
            }

            PreparedStatement stmt = conn.prepareStatement(sql);
            
            {
                PreparedStatementHolder stmtHolder2 = stmt.unwrap(PreparedStatementHolder.class);
                Assert.assertSame(stmtHolder2, stmtHolder);
                Assert.assertEquals(1, stmtHolder.getHitCount());
            }

            ResultSet rs = stmt.executeQuery();
            rs.next();
            rs.close();
            stmt.close();
            {
                OraclePreparedStatement oracleStmt2 = stmt.unwrap(OraclePreparedStatement.class);
                Assert.assertSame(oracleStmt, oracleStmt2);
                Assert.assertEquals(2, oracleStmt.getRowPrefetch());
            }

            conn.close();
        }
        
        Assert.assertEquals(1, dataSource.getCachedPreparedStatementCount());

    }
}
