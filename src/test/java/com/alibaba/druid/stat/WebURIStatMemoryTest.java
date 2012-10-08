package com.alibaba.druid.stat;

import java.lang.management.ManagementFactory;
import java.text.NumberFormat;

import junit.framework.TestCase;

import com.alibaba.druid.support.http.stat.WebURIStat;

public class WebURIStatMemoryTest extends TestCase {

    public void test_0() throws Exception {
        WebURIStat item = new WebURIStat("");
        gc();
        long memoryStart = ManagementFactory.getMemoryMXBean().getHeapMemoryUsage().getUsed();

        final int COUNT = 1024 * 1024;
        WebURIStat[] items = new WebURIStat[COUNT];
        for (int i = 0; i < COUNT; ++i) {
            items[i] = new WebURIStat("");
            items[i].getProfiletat();
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
