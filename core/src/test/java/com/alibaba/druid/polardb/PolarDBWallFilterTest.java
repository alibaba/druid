package com.alibaba.druid.polardb;

import com.alibaba.druid.DbType;
import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.wall.WallConfig;
import com.alibaba.druid.wall.WallFilter;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

import junit.framework.TestCase;

import org.junit.Assert;

public class PolarDBWallFilterTest extends TestCase {
    private DruidDataSource dataSource;

    protected void setUp() throws Exception {
        String jdbcUrl = "jdbc:polardb://a.b.c.d:5432/polardb";
        String user = "polardb";
        String password = "polardb";
        dataSource = new DruidDataSource();
        dataSource.setDbType(DbType.polardb);
        dataSource.setUrl(jdbcUrl);
        dataSource.setUsername(user);
        dataSource.setPassword(password);
        dataSource.setFilters("stat");

        WallConfig wallConfig = new WallConfig();
        wallConfig.setStrictSyntaxCheck(true);
        WallFilter wallFilter = new WallFilter();
        wallFilter.setConfig(wallConfig);
        dataSource.getProxyFilters().add(wallFilter);
    }

    /*
     * PolarDB-Oracle 1.0 is regarded as PostgreSQL in Druid
     * and it only accepts PostgreSQL style grammar.
     */
    public void testWallFilter() throws Exception {
        String sql = null;
        Connection connect = dataSource.getConnection();
        PreparedStatement pstmt = null;
        ResultSet resultSet = null;

        /*
         * 1. Simple query ok
         */
        sql = "SELECT sysdate FROM dual";
        pstmt = connect.prepareStatement(sql);
        resultSet = pstmt.executeQuery();
        while (resultSet.next()) {
            Timestamp timestamp = resultSet.getTimestamp("sysdate");
            System.out.println("sysdate: " + timestamp);
        }
        pstmt.close();

        /*
         * 2. PostgreSQL style SET ok
         */
        sql = "SET seq_page_cost TO 1";
        pstmt = connect.prepareStatement(sql);
        pstmt.execute();
        pstmt.close();

        /*
         * 3. Oracle style ALTER SESSION filtered by wall filter
         */
        boolean sql_filtered = false;
        try {
            sql = "ALTER SESSION SET seq_page_cost = 1";
            pstmt = connect.prepareStatement(sql);
            pstmt.execute();
        } catch (SQLException e) {
            sql_filtered = true;
            e.printStackTrace();
            System.out.println("SQL filtered by wall filter");
        } finally {
            pstmt.close();
        }

        Assert.assertTrue(sql_filtered);
    }
}