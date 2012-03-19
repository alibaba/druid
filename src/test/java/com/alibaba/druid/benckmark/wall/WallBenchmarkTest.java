package com.alibaba.druid.benckmark.wall;

import junit.framework.TestCase;

import com.alibaba.druid.wall.spi.MySqlWallProvider;

public class WallBenchmarkTest extends TestCase {

    MySqlWallProvider       provider = new MySqlWallProvider();

    public final static int COUNT    = 1000 * 1000;

    public void test_0() throws Exception {
        String sql = "select * from t";
        for (int i = 0; i < 10; ++i) {
            provider.clearCache();
            long startMillis = System.currentTimeMillis();
            perf(sql);
            long millis = System.currentTimeMillis() - startMillis;
            System.out.println("millis : " + millis);
        }
    }

    public void perf(String sql) {
        for (int i = 0; i < COUNT; ++i) {
            provider.check(sql);
        }
    }
}
