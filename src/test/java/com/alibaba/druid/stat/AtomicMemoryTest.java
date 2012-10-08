package com.alibaba.druid.stat;

import java.lang.management.ManagementFactory;
import java.text.NumberFormat;
import java.util.concurrent.atomic.AtomicLong;

import junit.framework.TestCase;

public class AtomicMemoryTest extends TestCase {

    public void test_0() throws Exception {
        AtomicLong item = new AtomicLong();
        gc();
        long memoryStart = ManagementFactory.getMemoryMXBean().getHeapMemoryUsage().getUsed();

        final int COUNT = 1024 * 1024;
        AtomicLong[] items = new AtomicLong[COUNT];
        for (int i = 0; i < COUNT; ++i) {
            items[i] = new AtomicLong();
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
