package com.alibaba.druid.stat;

import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicLongFieldUpdater;

import junit.framework.TestCase;

public class PerfTest extends TestCase {

    public void test_0() throws Exception {
        for (int i = 0; i < 3; ++i) {
            long startMillis = System.currentTimeMillis();
            a();
            long millis = System.currentTimeMillis() - startMillis;
            System.out.println("a : " + millis);
        }
    }

    private void a() {
        A a = new A();
        for (int i = 0; i < 1000 * 1000 * 1000; ++i) {
            a.incrementAndGet();
        }
    }

    private void b() {
        B b = new B();
        for (int i = 0; i < 1000 * 1000 * 1000; ++i) {
            b.incrementAndGet();
        }
    }

    public static class A {

        private volatile long                  value;

        static final AtomicLongFieldUpdater<A> updater = AtomicLongFieldUpdater.newUpdater(A.class, "value");

        public long incrementAndGet() {
            return updater.incrementAndGet(this);
        }

        public long getValue() {
            return value;
        }
    }

    public static class B {

        private final AtomicLong value = new AtomicLong();

        public long incrementAndGet() {
            return value.incrementAndGet();
        }
    }
}
