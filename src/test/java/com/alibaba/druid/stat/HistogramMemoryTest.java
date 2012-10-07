package com.alibaba.druid.stat;

import java.lang.management.ManagementFactory;
import java.text.NumberFormat;

import junit.framework.TestCase;

public class HistogramMemoryTest extends TestCase {

    public void test_0() throws Exception {
        long memoryStart = ManagementFactory.getMemoryMXBean().getHeapMemoryUsage().getUsed();
        
        final int COUNT = 1024 * 1;
        Object[] items = new Object[COUNT];
        for (int i = 0; i < COUNT; ++i) {
             items[i] = new JdbcSqlStat("");
//            items[i] = Histogram.makeHistogram(20);
        }
        
        long memoryEnd = ManagementFactory.getMemoryMXBean().getHeapMemoryUsage().getUsed();
        
        System.out.println("memory used : " + NumberFormat.getInstance().format(memoryEnd - memoryStart));
    }
}
