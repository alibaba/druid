package com.alibaba.druid.pool.ha;

import java.sql.Connection;
import java.sql.Statement;
import java.util.concurrent.CountDownLatch;

import junit.framework.TestCase;

import com.alibaba.druid.pool.ha.cobar.CobarDataSource;

public class CobarDataSourceTest extends TestCase {

    public void test_cobar() throws Exception {
        String url = "jdbc:cobar://10.20.165.79:8080/druid";

        final CobarDataSource dataSource = new CobarDataSource();
        dataSource.setUrl(url);
        dataSource.setUsername("test");
        dataSource.setPassword("");
        
        final int COUNT = 10;
        final CountDownLatch latch = new CountDownLatch(COUNT);

        Runnable task = new Runnable() {

            public void run() {
                try {
                    for (int i = 0; i < 1000 * 1000; ++i) {
                        Connection conn = dataSource.getConnection();

                        Statement stmt = conn.createStatement();
                        stmt.execute("select 1");
                        stmt.close();

                        conn.close();
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                } finally {
                    latch.countDown();
                }
            }
        };

        
        for (int i = 0; i < 10; ++i) {
            Thread thread = new Thread(task);
            thread.setName("thread-" + i);
            thread.start();
        }
        
        latch.await();
    }
}
