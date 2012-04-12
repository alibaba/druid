package com.alibaba.druid.bvt.pool;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import junit.framework.Assert;
import junit.framework.TestCase;
import oracle.jdbc.OracleConnection;
import oracle.jdbc.internal.OraclePreparedStatement;

import com.alibaba.druid.pool.DruidDataSource;
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
    }

    protected void tearDown() throws Exception {
        JdbcUtils.close(dataSource);
    }

    public void test_oracle() throws Exception {

        String sql = "SELECT 1";

        {
            Connection conn = dataSource.getConnection();

            {
                OracleConnection oracleConn = conn.unwrap(OracleConnection.class);
                Assert.assertEquals(50, oracleConn.getDefaultRowPrefetch());
            }

            PreparedStatement stmt = conn.prepareStatement(sql);

            {
                OraclePreparedStatement oracleStmt = stmt.unwrap(OraclePreparedStatement.class);
                Assert.assertEquals(50, oracleStmt.getRowPrefetch());
            }

            ResultSet rs = stmt.executeQuery();
            rs.next();

            rs.close();
            stmt.close();
            conn.close();
        }

        {
            Connection conn = dataSource.getConnection();

            {
                OracleConnection oracleConn = conn.unwrap(OracleConnection.class);
                Assert.assertEquals(50, oracleConn.getDefaultRowPrefetch());
            }

            PreparedStatement stmt = conn.prepareStatement(sql);

            ResultSet rs = stmt.executeQuery();
            rs.next();

            {
                OraclePreparedStatement oracleStmt = stmt.unwrap(OraclePreparedStatement.class);
                Assert.assertEquals(2, oracleStmt.getRowPrefetch());
            }

            rs.close();
            stmt.close();
            conn.close();
        }

        dataSource.close();
    }
}
