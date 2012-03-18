package com.alibaba.druid.pool;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Random;

import com.alibaba.druid.util.JdbcUtils;

import junit.framework.TestCase;

public class TestPoolStatement extends TestCase {

    private DruidDataSource dataSource;

    protected void setUp() throws Exception {

        dataSource = new DruidDataSource();
        dataSource.setUrl("jdbc:oracle:thin:@10.20.36.18:1521:testconn");
        dataSource.setInitialSize(1);
        dataSource.setMaxActive(14);
        dataSource.setMaxIdle(14);
        dataSource.setMinIdle(1);
        dataSource.setMinEvictableIdleTimeMillis(300 * 1000); // 300 / 10
        dataSource.setTimeBetweenEvictionRunsMillis(180 * 1000); // 180 / 10
        dataSource.setTestWhileIdle(false);
        dataSource.setTestOnBorrow(false);
        dataSource.setValidationQuery("SELECT 1");
        dataSource.setFilters("stat");
        dataSource.setPoolPreparedStatements(true);
        dataSource.setUsername("alibaba");
        dataSource.setPassword("alibaba");
    }

    public void test_0() throws Exception {
        stat();
        long startMillis = System.currentTimeMillis();
        for (int i = 0; i < 1000 * 5; ++i) {
            exec();
        }
        long millis = System.currentTimeMillis() - startMillis;
        stat();
        System.out.println();
        System.out.println("millis : " + millis);
    }

    private void stat() throws SQLException {
        Connection conn = dataSource.getConnection();

        String sql = "SELECT sysdate,name,value FROM V$SYSSTAT WHERE NAME IN ('parse count (total)', 'execute count')";

        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery(sql);
        JdbcUtils.printResultSet(rs);
        rs.close();
        stmt.close();
        conn.close();
    }

    private void exec() throws SQLException {
        Connection conn = dataSource.getConnection();

        String sql = "select * from ALIBABA.ORDER_MAIN WHERE ID = ?";

        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setLong(1, new Random().nextInt(1000 * 100));
        ResultSet rs = stmt.executeQuery();
        while (rs.next()) {

        }
        rs.close();
        stmt.close();
        conn.close();
    }
}
