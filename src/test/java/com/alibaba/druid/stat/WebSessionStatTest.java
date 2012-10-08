package com.alibaba.druid.stat;

import java.lang.management.ManagementFactory;
import java.text.NumberFormat;

import junit.framework.TestCase;

import com.alibaba.druid.support.http.stat.WebSessionStat;

public class WebSessionStatTest extends TestCase {

    public void test_0() throws Exception {
        WebSessionStat item = new WebSessionStat("1b959a6db8489c4c7ef7bf0bd743ab52");
        gc();
        long memoryStart = ManagementFactory.getMemoryMXBean().getHeapMemoryUsage().getUsed();

        final int COUNT = 1024 * 1024;
        WebSessionStat[] items = new WebSessionStat[COUNT];
        for (int i = 0; i < COUNT; ++i) {
            items[i] = new WebSessionStat("");
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
