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

    public void test_addRemoteAddress() throws Exception {
        WebSessionStat item = new WebSessionStat("1b959a6db8489c4c7ef7bf0bd743ab52");
        item.addRemoteAddress("127.0.0.32");
        assertEquals("127.0.0.32",item.getRemoteAddress());
        item.addRemoteAddress(null);
        assertEquals("127.0.0.32",item.getRemoteAddress());
        item.addRemoteAddress("");
        assertEquals("127.0.0.32",item.getRemoteAddress());
        item.addRemoteAddress("123");
        assertEquals("127.0.0.32",item.getRemoteAddress());
        item.addRemoteAddress("127.0.0.32");//ip在第一个的场景
        assertEquals("127.0.0.32",item.getRemoteAddress());
        item.addRemoteAddress("127.0.0.23");
        assertEquals("127.0.0.32;127.0.0.23",item.getRemoteAddress());
        item.addRemoteAddress("127.0.0.23");//ip在最后一个的
        assertEquals("127.0.0.32;127.0.0.23",item.getRemoteAddress());
        item.addRemoteAddress("127.0.0.235");
        assertEquals("127.0.0.32;127.0.0.23;127.0.0.235",item.getRemoteAddress());
        item.addRemoteAddress("127.0.0.3");
        assertEquals("127.0.0.32;127.0.0.23;127.0.0.235;127.0.0.3",item.getRemoteAddress());
        item.addRemoteAddress("127.0.0.3");
        assertEquals("127.0.0.32;127.0.0.23;127.0.0.235;127.0.0.3",item.getRemoteAddress());
        item.addRemoteAddress("127.0.0.32");
        assertEquals("127.0.0.32;127.0.0.23;127.0.0.235;127.0.0.3",item.getRemoteAddress());
        item.addRemoteAddress("127.0.0.235");
        assertEquals("127.0.0.32;127.0.0.23;127.0.0.235;127.0.0.3",item.getRemoteAddress());
        item.addRemoteAddress("127.0.0.2");
        assertEquals("127.0.0.32;127.0.0.23;127.0.0.235;127.0.0.3;127.0.0.2",item.getRemoteAddress());



    }
}
