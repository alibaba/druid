package com.alibaba.druid.sql;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import junit.framework.TestCase;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.util.JdbcUtils;

public class OnlineOracleTest3 extends TestCase {

    private String          jdbcUrl;
    private String          user;
    private String          password;
    private DruidDataSource dataSource;

    public void setUp() throws Exception {
        jdbcUrl = "jdbc:oracle:thin:@10.20.149.18:1521:emdb";
        user = "wardon";
        password = "wardon";

        dataSource = new DruidDataSource();
        dataSource.setInitialSize(1);
        dataSource.setUrl(jdbcUrl);
        dataSource.setUsername(user);
        dataSource.setPassword(password);
        dataSource.setMaxActive(50);
        dataSource.setPoolPreparedStatements(true);
        dataSource.setMaxOpenPreparedStatements(100);
    }

    public void test_connect() throws Exception {
        executeQuery("select utl_inaddr.get_host_address from DUAL");
    }

    public void executeQuery(String sql) throws SQLException {
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        try {
            conn = dataSource.getConnection();

            stmt = conn.createStatement();
            rs = stmt.executeQuery(sql);

            JdbcUtils.printResultSet(rs);

        } finally {
            JdbcUtils.close(rs);
            JdbcUtils.close(stmt);
            JdbcUtils.close(conn);
        }
    }

    public void execute(String sql) throws SQLException {
        Connection conn = null;
        Statement stmt = null;
        try {
            conn = dataSource.getConnection();

            stmt = conn.createStatement();
            stmt.execute(sql);

        } finally {
            JdbcUtils.close(stmt);
            JdbcUtils.close(conn);
        }
    }
}
