package com.alibaba.druid.polardb;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import junit.framework.TestCase;

import org.junit.Assert;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.util.JdbcConstants;
import com.alibaba.druid.util.JdbcUtils;

public class PolarDBDataSourceTest extends TestCase {

    private String jdbcUrl;
    private DruidDataSource dataSource;

    protected void setUp() throws Exception {
        jdbcUrl = "jdbc:polardb://a.b.c.d:5432/polardb";
        dataSource = new DruidDataSource();
        dataSource.setUrl(jdbcUrl);
        dataSource.setUsername("polardb");
        dataSource.setPassword("polardb");
        dataSource.setValidationQuery("select 1");
        dataSource.setFilters("stat");
    }

    protected void tearDown() throws Exception {
        dataSource.close();
    }

    public void testDataSource() throws SQLException {
        dataSource.init();

        Assert.assertTrue(JdbcConstants.POLARDB.equals(dataSource.getDbType()));
        Assert.assertTrue(JdbcConstants.POLARDB_DRIVER.equals(dataSource.getDriverClassName()));

        Connection conn = dataSource.getConnection();
        PreparedStatement stmt = conn.prepareStatement("SELECT 1");
        ResultSet rs = stmt.executeQuery();
        while(rs.next()) {

        }
        JdbcUtils.close(rs);
        JdbcUtils.close(stmt);
        JdbcUtils.close(conn);
    }
}
