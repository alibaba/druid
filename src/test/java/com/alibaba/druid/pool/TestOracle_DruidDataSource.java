package com.alibaba.druid.pool;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.TreeMap;
import java.util.concurrent.CountDownLatch;

import junit.framework.TestCase;

import com.alibaba.druid.pool.vendor.OracleExceptionSorter;
import com.alibaba.druid.stat.JdbcStatManager;
import com.alibaba.druid.support.json.JSONUtils;

public class TestOracle_DruidDataSource extends TestCase {

    private String jdbcUrl;
    private String user;
    private String password;

    public void setUp() throws Exception {
        jdbcUrl = "jdbc:oracle:thin:@10.20.149.85:1521:ocnauto";
        // jdbcUrl = "jdbc:oracle:thin:@20.20.149.85:1521:ocnauto"; // error url
        user = "alibaba";
        password = "ccbuauto";
    }

    public void test_0() throws Exception {
        final String SQL = "SELECT SYSDATE FROM DUAL";
        final DruidDataSource dataSource = new DruidDataSource();
        dataSource.setInitialSize(1);
        dataSource.setUrl(jdbcUrl);
        dataSource.setUsername(user);
        dataSource.setPassword(password);
        dataSource.setFilters("stat");
        dataSource.setExceptionSorter(OracleExceptionSorter.class.getName());

        final int COUNT = 100 * 100;
        final int THREAD_COUNT = 10;
        final CountDownLatch startLatch = new CountDownLatch(1);
        final CountDownLatch endLatch = new CountDownLatch(THREAD_COUNT);

        for (int i = 0; i < THREAD_COUNT; ++i) {
            Thread thread = new Thread() {

                public void run() {
                    try {
                        startLatch.await();
                        for (int i = 0; i < COUNT; ++i) {
                            Connection conn = dataSource.getConnection();
                            Statement stmt = conn.createStatement();
                            ResultSet rs = stmt.executeQuery(SQL);
                            rs.next();
                            rs.close();
                            stmt.close();
                            conn.close();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        endLatch.countDown();
                    }
                }
            };
            thread.start();
        }
        startLatch.countDown();
        endLatch.await();

        for (Object item : JdbcStatManager.getInstance().getDataSourceList().values()) {
            String text = JSONUtils.toJSONString(item);
            System.out.println(text);
        }

        for (Object item : JdbcStatManager.getInstance().getSqlList().values()) {
            String text = JSONUtils.toJSONString(item);
            System.out.println(text);
        }
    }
}
