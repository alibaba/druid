package com.alibaba.druid.benckmark;

import java.util.concurrent.atomic.AtomicInteger;

import junit.framework.TestCase;

public class TestAtomicPerformance extends TestCase {

    public void test_0() throws Exception {
        AtomicInteger count = new AtomicInteger();
        for (int i = 0; i < 5; ++i) {
            perf(count);
        }
        System.out.println();
        for (int i = 0; i < 5; ++i) {
            perf2(count);
        }
    }

    private void perf(AtomicInteger count) {
        long startMillis = System.currentTimeMillis();

        count.set(0);
        for (int i = 0; i < 1000 * 1000 * 100; ++i) {
            int value = count.get();
            count.compareAndSet(value, value + 1);
        }

        long millis = System.currentTimeMillis() - startMillis;
        System.out.println("millis " + millis);
    }

    private void perf2(AtomicInteger count) {
        long startMillis = System.currentTimeMillis();

        count.set(0);
        for (int i = 0; i < 1000 * 1000 * 100; ++i) {
            int value = count.get();
            count.weakCompareAndSet(value, value + 1);
        }

        long millis = System.currentTimeMillis() - startMillis;
        System.out.println("weak millis " + millis);
    }
}
