package com.alibaba.druid.pool.postgres;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.junit.Assert;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.vendor.PGValidConnectionChecker;
import com.alibaba.druid.util.JdbcUtils;

import junit.framework.TestCase;

/**
 * 这个场景测试minIdle > maxActive
 * 
 * @author wenshao [szujobs@hotmail.com]
 */
public class PG_500_connection_Test extends TestCase {

    private DruidDataSource dataSource;

    protected void setUp() throws Exception {
        dataSource = new DruidDataSource();
        dataSource.setUrl("jdbc:postgresql://192.168.199.231:5432/druid_test_db");
        dataSource.setUsername("druid_test");
        dataSource.setPassword("druid_test");
        dataSource.setInitialSize(50);
        dataSource.setMaxActive(80);
        dataSource.setMinIdle(50);
        dataSource.setMinEvictableIdleTimeMillis(300 * 1000); // 300 / 10
        dataSource.setTimeBetweenEvictionRunsMillis(180 * 1000); // 180 / 10
        dataSource.setTestWhileIdle(false);
        dataSource.setTestOnBorrow(false);
        // dataSource.setValidationQuery("SELECT 1 FROM DUAL");
        dataSource.setFilters("stat");
    }

    protected void tearDown() throws Exception {
        dataSource.close();
    }

    public void test_conect_500() throws Exception {
        dataSource.init();
        Assert.assertFalse(dataSource.isOracle());
        Assert.assertTrue(dataSource.getValidConnectionChecker() instanceof PGValidConnectionChecker);

        int taskCount = 1000 * 100;
        final CountDownLatch endLatch = new CountDownLatch(taskCount);
        Runnable task = new Runnable() {

            @Override
            public void run() {
                Connection conn = null;
                Statement stmt = null;
                ResultSet rs = null;
                try {
                    conn = dataSource.getConnection();
                    stmt = conn.createStatement();
                    rs = stmt.executeQuery("SELECT 1");
                    while (rs.next()) {

                    }
                } catch (SQLException ex) {
                    // skip
                } finally {
                    endLatch.countDown();
                }
                JdbcUtils.close(rs);
                JdbcUtils.close(stmt);
                JdbcUtils.close(conn);
            }
        };
        
        ExecutorService executor = Executors.newFixedThreadPool(100);
        for (int i = 0; i < taskCount; ++i) {
            executor.submit(task);
        }
        endLatch.await();

    }
}
