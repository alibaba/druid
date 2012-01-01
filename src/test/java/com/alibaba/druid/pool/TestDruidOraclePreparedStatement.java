package com.alibaba.druid.pool;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

import junit.framework.TestCase;

import com.alibaba.druid.util.JdbcUtils;

public class TestDruidOraclePreparedStatement extends TestCase {

    private String          jdbcUrl;
    private String          user;
    private String          password;

    private DruidDataSource dataSource;

    public void setUp() throws Exception {
        jdbcUrl = "jdbc:oracle:thin:@10.20.149.85:1521:ocnauto";
        // jdbcUrl = "jdbc:oracle:thin:@20.20.149.85:1521:ocnauto"; // error url
        user = "alibaba";
        password = "ccbuauto";

        dataSource = new DruidDataSource();
        dataSource.setPoolPreparedStatements(true);

        dataSource.setUrl(jdbcUrl);
        dataSource.setUsername(user);
        dataSource.setPassword(password);
    }

    protected void tearDown() throws Exception {
        JdbcUtils.close(dataSource);
    }

    public void test_0() throws Exception {

        Class.forName(JdbcUtils.getDriverClassName(jdbcUrl));

//        {
//            Connection conn = dataSource.getConnection();
//
//            ResultSet metaRs = conn.getMetaData().getTables(null, "ALIBABA", null, new String[] { "TABLE" });
//            JdbcUtils.printResultSet(metaRs);
//            metaRs.close();
//
//            conn.close();
//        }

//        {
//            Connection conn = dataSource.getConnection();
//            Statement stmt = conn.createStatement();
//
//            ResultSet rs = stmt.executeQuery("SELECT * FROM WP_ORDERS");
//            JdbcUtils.printResultSet(rs);
//            rs.close();
//
//            stmt.close();
//            conn.close();
//        }

        for (int i = 0; i < 3; ++i) {
            Connection conn = dataSource.getConnection();

            // ResultSet metaRs = conn.getMetaData().getTables(null, "ALIBABA", null, new String[] {"TABLE"});
            // JdbcUtils.printResultSet(metaRs);
            // metaRs.close();

            String sql = "SELECT * FROM WS_OFFER WHERE ROWNUM <= ?";

            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, 1);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {

            }

            rs.close();
            stmt.close();
            conn.close();
        }
        {
            Connection conn = dataSource.getConnection();

            // ResultSet metaRs = conn.getMetaData().getTables(null, "ALIBABA", null, new String[] {"TABLE"});
            // JdbcUtils.printResultSet(metaRs);
            // metaRs.close();

            String sql = "SELECT * FROM WS_OFFER WHERE ROWNUM <= ?";

            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, 11);
            ResultSet rs = stmt.executeQuery();
            rs.next();

            rs.close();
            stmt.close();
            conn.close();
        }

    }
}
