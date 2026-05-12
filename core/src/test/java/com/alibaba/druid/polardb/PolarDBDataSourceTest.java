package com.alibaba.druid.polardb;

import com.alibaba.druid.DbType;
import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.util.JdbcConstants;
import com.alibaba.druid.util.JdbcUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

public class PolarDBDataSourceTest {
    private String jdbcUrl;
    private DruidDataSource dataSource;

    @BeforeEach
    protected void setUp() throws Exception {
        jdbcUrl = "jdbc:polardb://a.b.c.d:5432/polardb";
        dataSource = new DruidDataSource();
        dataSource.setUrl(jdbcUrl);
        dataSource.setUsername("polardb");
        dataSource.setPassword("polardb");
        dataSource.setValidationQuery("select 1");
        dataSource.setFilters("stat");
    }

    @AfterEach
    protected void tearDown() throws Exception {
        dataSource.close();
    }

    @Test
    public void testDataSource() throws SQLException {
        dataSource.init();

        assertTrue(JdbcConstants.POLARDB.equals(DbType.of(dataSource.getDbType())));
        assertTrue(JdbcConstants.POLARDB_DRIVER.equals(dataSource.getDriverClassName()));

        Connection conn = dataSource.getConnection(1000);
        PreparedStatement stmt = conn.prepareStatement("SELECT 1");
        ResultSet rs = stmt.executeQuery();
        while (rs.next()) {
            System.out.println("result: " + rs.getInt(1));
        }
        JdbcUtils.close(rs);
        JdbcUtils.close(stmt);
        JdbcUtils.close(conn);
    }
}
