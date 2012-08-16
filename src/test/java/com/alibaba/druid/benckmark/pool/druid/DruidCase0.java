package com.alibaba.druid.benckmark.pool.druid;

import java.sql.Connection;

import junit.framework.TestCase;

import com.alibaba.druid.pool.DruidDataSource;

public class DruidCase0 extends TestCase {

    private DruidDataSource dataSource;

    protected void setUp() throws Exception {
        dataSource = new DruidDataSource();
        dataSource.setUrl("jdbc:mock:xxx");
        dataSource.setMaxActive(8);
        dataSource.setMinIdle(1);
        dataSource.setMinEvictableIdleTimeMillis(1000 * 60 * 5);
    }

    protected void tearDown() throws Exception {
        dataSource.close();
    }

    public void test_benchmark() throws Exception {
        for (int i = 0; i < 10; ++i) {
            long startMillis = System.currentTimeMillis();
            benchmark();
            long millis = System.currentTimeMillis() - startMillis;
            
            System.out.println("millis : " + millis);
        }
    }

    public void benchmark() throws Exception {
        for (int i = 0; i < 1000 * 1000 * 10; ++i) {
            Connection conn = dataSource.getConnection();
            conn.close();
        }
    }

}
