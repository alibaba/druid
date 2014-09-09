package com.alibaba.druid.pool.oracle;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.CountDownLatch;

import junit.framework.TestCase;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.util.JdbcUtils;

public class QueryTimeoutTest extends TestCase {

    private DruidDataSource dataSource;

    protected void setUp() throws Exception {
        dataSource = new DruidDataSource();
        dataSource.setUrl("jdbc:mysql://a.b.c.d:3306/umiddb");
        dataSource.setUsername("umiddb");
        dataSource.setPassword("umiddb1001");
        dataSource.setInitialSize(1);
        dataSource.setMaxActive(14);
        dataSource.setMinIdle(1);
        dataSource.setMinEvictableIdleTimeMillis(300 * 1000); // 300 / 10
        dataSource.setTimeBetweenEvictionRunsMillis(180 * 1000); // 180 / 10
        dataSource.setTestWhileIdle(true);
        dataSource.setTestOnBorrow(false);
        dataSource.setValidationQuery("SELECT 1");
        dataSource.setFilters("stat");
    }

    public void test_queryTimeout() throws Exception {
        try {
            final Connection conn = dataSource.getConnection();
            
            String sql = "SELECT sleep(1)";
            final CountDownLatch latch = new CountDownLatch(1);
            Thread thread = new Thread() {
                public void run() {
                    try {
                        latch.countDown();
                        Thread.sleep(100);
                        conn.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            };
            thread.start();
            
            latch.await();
            final PreparedStatement stmt = conn.prepareStatement(sql);
//            stmt.setQueryTimeout(1);
            
            final ResultSet rs = stmt.executeQuery();
            JdbcUtils.printResultSet(rs);
            rs.close();
            stmt.close();
            conn.close();
            
        } catch (Exception e) {
            //
            e.printStackTrace();
        }

        Connection conn = dataSource.getConnection();

        String sql = "SELECT 'x'";
        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setQueryTimeout(1);
        ResultSet rs = stmt.executeQuery();
        JdbcUtils.printResultSet(rs);
        rs.close();
        stmt.close();
        conn.close();
    }
}
