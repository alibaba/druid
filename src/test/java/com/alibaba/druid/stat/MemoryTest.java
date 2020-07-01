package com.alibaba.druid.stat;

import java.lang.management.ManagementFactory;
import java.text.NumberFormat;

import junit.framework.TestCase;

public class MemoryTest extends TestCase {

    public void test_0() throws Exception {
        A item = new A();
        gc();
        final int COUNT = 1024 * 1024;
        A[] items = new A[COUNT];

        long memoryStart = ManagementFactory.getMemoryMXBean().getHeapMemoryUsage().getUsed();

        for (int i = 0; i < COUNT; ++i) {
            items[i] = new A();
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

    public static class A {
        private volatile long v;
    }
}
