package com.alibaba.druid.polardb2;

import com.alibaba.druid.DbType;
import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.util.JdbcConstants;
import com.alibaba.druid.util.JdbcUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import junit.framework.TestCase;

import org.junit.Assert;

public class PolarDB2DbTypeTest extends TestCase {
    private String jdbcUrl;
    private String jdbcUrl1;
    private String jdbcUrl2;
    private String user;
    private String password;
    private String validateQuery;
    private DruidDataSource dataSource;

    protected void setUp() throws Exception {
        jdbcUrl1 = "jdbc:polardb://a.b.c.d:5432/polardb";
        jdbcUrl2 = "jdbc:polardb2://a.b.c.d:5432/polardb";
        user = "polardb";
        password = "polardb";
        validateQuery = "select 1";
    }

    private void configDataSource() throws Exception {
        dataSource.setUrl(jdbcUrl);
        dataSource.setUsername(user);
        dataSource.setPassword(password);
        dataSource.setValidationQuery(validateQuery);
        dataSource.setFilters("stat");
    }

    /**
     * Init datasource without setting DbType and druid will get DbType
     * from jdbc url prefix "jdbc:polardb2".
     */
    public void testDefaultDbType() throws Exception {
        dataSource = new DruidDataSource();
        jdbcUrl = jdbcUrl2;
        configDataSource();
        dataSource.init();

        Assert.assertTrue(JdbcConstants.POLARDB2.equals(DbType.of(dataSource.getDbType())));
        Assert.assertTrue(JdbcConstants.POLARDB2_DRIVER.equals(dataSource.getDriverClassName()));

        Connection conn = dataSource.getConnection();
        PreparedStatement stmt = conn.prepareStatement(validateQuery);
        ResultSet rs = stmt.executeQuery();
        while (rs.next()) {
            System.out.println("result: " + rs.getInt(1));
        }
        JdbcUtils.close(rs);
        JdbcUtils.close(stmt);
        JdbcUtils.close(conn);
        dataSource.close();
    }

    /**
     * Init datasource without setting DbType and druid will get DbType
     * from jdbc url. url with prefix "jdbc:polardb" is recognized as PolarDB-Oracle 1.0
     * for backward compatibility. If the user want to use SQL firewall in this
     * case, set DbType to PolarDB-Oracle 2.0 explicitly.
     */
    public void testSetDbType() throws Exception {
        /*
         * Case 1: set DbType and driver name after initializing data source.
         * DbType could be changed after initializing, while driver name not.
         */
        dataSource = new DruidDataSource();
        // JDBC url in PolarDB-Oracle 1.0 format
        jdbcUrl = jdbcUrl1;
        configDataSource();
        dataSource.init();

        // Driver and DbType are set to PolarDB-Oracle 1.0 automatically
        Assert.assertTrue(JdbcConstants.POLARDB.equals(DbType.of(dataSource.getDbType())));
        Assert.assertTrue(JdbcConstants.POLARDB_DRIVER.equals(dataSource.getDriverClassName()));

        boolean conn_failed = false;
        try {
            Connection conn = dataSource.getConnection(1000);
        } catch (Exception e) {
            // Fail to connect to PolarDB-Oracle 2.0 with PolarDB-Oracle 1.0 driver
            conn_failed = true;
            e.printStackTrace();
            System.out.println("failed to connect to PolarDB-Oracle 2.0 with PolarDB-Oracle 1.0 DbType and driver");
        }
        Assert.assertTrue(conn_failed);

        // Set new DbType with string
        dataSource.setDbType("polardb2");
        Assert.assertTrue(JdbcConstants.POLARDB2.equals(DbType.of(dataSource.getDbType())));

        // Reset
        dataSource.setDbType("polardb");
        Assert.assertTrue(JdbcConstants.POLARDB.equals(DbType.of(dataSource.getDbType())));

        // Set new DbType with const
        dataSource.setDbType(DbType.polardb2);
        Assert.assertTrue(JdbcConstants.POLARDB2.equals(DbType.of(dataSource.getDbType())));

        // Failed to set driver name after init
        // dataSource.setDriverClassName(JdbcConstants.POLARDB2_DRIVER);
        Assert.assertTrue(JdbcConstants.POLARDB_DRIVER.equals(dataSource.getDriverClassName()));

        dataSource.clone();

        /*
         * Case 2: set DbType and driver name after initializing data source.
         */
        dataSource = new DruidDataSource();
        // JDBC url in PolarDB-Oracle 1.0 format
        jdbcUrl = jdbcUrl1;
        configDataSource();
        // Set DbType and driver to PolarDB-Oracle 2.0 before init data source
        dataSource.setDbType(DbType.polardb2);
        dataSource.setDriverClassName(JdbcConstants.POLARDB2_DRIVER);
        dataSource.init();

        Assert.assertTrue(JdbcConstants.POLARDB2.equals(DbType.of(dataSource.getDbType())));
        Assert.assertTrue(JdbcConstants.POLARDB2_DRIVER.equals(dataSource.getDriverClassName()));

        Connection conn = dataSource.getConnection();
        PreparedStatement stmt = conn.prepareStatement(validateQuery);
        ResultSet rs = stmt.executeQuery();
        while (rs.next()) {
            System.out.println("result: " + rs.getInt(1));
        }
        JdbcUtils.close(rs);
        JdbcUtils.close(stmt);
        JdbcUtils.close(conn);
        dataSource.close();
    }
}
