package com.alibaba.druid.benckmark.wall;

import junit.framework.TestCase;

import com.alibaba.druid.wall.WallProvider;
import com.alibaba.druid.wall.spi.OracleWallProvider;

public class WallBenchmarkTest extends TestCase {

    WallProvider            provider = new OracleWallProvider();

    public final static int COUNT    = 1000 * 1000;

    public void test_0() throws Exception {
        String sql = "SELECT t1.department_id, t2.*\n" + //
                     "FROM hr_info t1, TABLE(t1.people) t2\n" + //
                     "WHERE t2.department_id = t1.department_id;";
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
