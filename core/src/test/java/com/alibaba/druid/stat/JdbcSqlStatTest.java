package com.alibaba.druid.stat;

import java.lang.management.ManagementFactory;
import java.text.NumberFormat;

import junit.framework.TestCase;

public class JdbcSqlStatTest extends TestCase {
    private JdbcSqlStat item;

    public void test_0() throws Exception {
        item = new JdbcSqlStat("");
        gc();
        long memoryStart = ManagementFactory.getMemoryMXBean().getHeapMemoryUsage().getUsed();

        final int COUNT = 1000 * 5;
        JdbcSqlStat[] items = new JdbcSqlStat[COUNT];
        for (int i = 0; i < COUNT; ++i) {
            items[i] = new JdbcSqlStat("");
            // items[i] = Histogram.makeHistogram(20);
        }

        long memoryEnd = ManagementFactory.getMemoryMXBean().getHeapMemoryUsage().getUsed();

        System.out.println("memory used : " + NumberFormat.getInstance().format(memoryEnd - memoryStart));
    }

    private void gc() {
        for (int i = 0; i < 10; ++i) {
            System.gc();
        }
    }
}
