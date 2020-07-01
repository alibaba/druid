package com.alibaba.druid.stat;

import java.lang.management.ManagementFactory;
import java.text.NumberFormat;

import junit.framework.TestCase;

public class JdbcSqlStatTest1 extends TestCase {

    private JdbcDataSourceStat dataSourceStat = new JdbcDataSourceStat("", "");

    public void test_0() throws Exception {
        for (int i = 0; i < 3; ++i) {
            gc();
            long memoryStart = ManagementFactory.getMemoryMXBean().getHeapMemoryUsage().getUsed();

            final int COUNT = 1024 * 1024;
            for (int j = 0; j < COUNT; ++j) {
                dataSourceStat.createSqlStat(Integer.toString(j));
                // items[i] = Histogram.makeHistogram(20);
            }
            gc();

            long memoryEnd = ManagementFactory.getMemoryMXBean().getHeapMemoryUsage().getUsed();

            System.out.println("memory used : " + NumberFormat.getInstance().format(memoryStart));
            System.out.println("memory used : " + NumberFormat.getInstance().format(memoryEnd));
            System.out.println("memory used : " + NumberFormat.getInstance().format(memoryEnd - memoryStart));
        }
    }

    private void gc() {
        for (int i = 0; i < 10; ++i) {
            System.gc();
        }
    }
}
