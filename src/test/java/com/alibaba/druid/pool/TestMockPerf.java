package com.alibaba.druid.pool;

import java.sql.Connection;
import java.sql.Statement;
import java.util.concurrent.CountDownLatch;

import junit.framework.TestCase;

public class TestMockPerf extends TestCase {

    private DruidDataSource dataSource;

    protected void setUp() throws Exception {
        dataSource = new DruidDataSource();
        dataSource.setUrl("jdbc:mock:xx");
        dataSource.setInitialSize(3);
        dataSource.setMinIdle(3);
        dataSource.setMaxActive(20);
        dataSource.setDbType("mysql");
        dataSource.setFilters("stat");
        dataSource.init();
    }

    public void test_perf() throws Exception {
        final CountDownLatch latch = new CountDownLatch(10);
        for (int i = 0; i < 10; ++i) {
            Thread thread = new Thread() {
                public void run() {
                    try {
                        long startMillis = System.currentTimeMillis();
                        perf();
                        long millis = System.currentTimeMillis() - startMillis;
                        System.out.println("millis : " + millis);
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        latch.countDown();
                    }
                }
            };
            thread.start();
     
        }
        
        latch.await();
    }

    public void perf() throws Exception {
        for (int i = 0; i < 1000 * 1000; ++i) {
            Connection conn = dataSource.getConnection();
            Statement stmt = conn.createStatement();
            stmt.execute("SELECT " + i % 1000);
            stmt.close();
            conn.close();
        }
    }
}
