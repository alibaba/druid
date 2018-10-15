/*
 * Copyright 1999-2018 Alibaba Group Holding Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.alibaba.druid;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.junit.Assert;
import junit.framework.TestCase;

public class TestLockCount extends TestCase {

    public void test_current() throws Exception {
        DataSource dataSource = new DataSource();

        final int threadCount = 10;
        final int loopCount = 1000 * 1000 * 1000;
        concurrent(dataSource, threadCount, loopCount);
        long result = (long) threadCount * ((long) loopCount);

        Assert.assertEquals(result, dataSource.getCount());
        Assert.assertEquals(result, dataSource.getC0());
        Assert.assertEquals(result, dataSource.getC1());
        Assert.assertEquals(result, dataSource.getC2());
        Assert.assertEquals(result, dataSource.getC3());
        Assert.assertEquals(result, dataSource.getC4());
        Assert.assertEquals(result, dataSource.getC5());
        Assert.assertEquals(result, dataSource.getC6());
        Assert.assertEquals(result, dataSource.getC7());
        Assert.assertEquals(result, dataSource.getC8());
        Assert.assertEquals(result, dataSource.getC9());

        Assert.assertEquals(result, dataSource.getC10());
        Assert.assertEquals(result, dataSource.getC11());
        Assert.assertEquals(result, dataSource.getC12());
        Assert.assertEquals(result, dataSource.getC13());
        Assert.assertEquals(result, dataSource.getC14());
        Assert.assertEquals(result, dataSource.getC15());
        Assert.assertEquals(result, dataSource.getC16());
        Assert.assertEquals(result, dataSource.getC17());
        Assert.assertEquals(result, dataSource.getC18());
        Assert.assertEquals(result, dataSource.getC19());
    }

    private void concurrent(final DataSource dataSource, int threadCount, final int loopCount)
                                                                                              throws InterruptedException {

        final CountDownLatch startLatch = new CountDownLatch(1);
        final CountDownLatch endLatch = new CountDownLatch(threadCount);
        Thread[] threads = new Thread[threadCount];
        for (int i = 0; i < threadCount; ++i) {
            threads[i] = new Thread("thread-" + i) {

                public void run() {
                    try {
                        startLatch.await();
                        for (int i = 0; i < loopCount; ++i) {
                            dataSource.increment();
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    } finally {
                        endLatch.countDown();
                    }
                }
            };
        }

        for (int i = 0; i < threadCount; ++i) {
            threads[i].start();
        }
        startLatch.countDown();
        System.out.println("concurrent start...");
        endLatch.await();
        System.out.println("concurrent end");
    }

    private static class DataSource {

        private long       c0;
        private long       c1;
        private long       c2;
        private long       c3;
        private long       c4;
        private long       c5;
        private long       c6;
        private long       c7;
        private long       c8;
        private long       c9;
        private long       c10;
        private long       c11;
        private long       c12;
        private long       c13;
        private long       c14;
        private long       c15;
        private long       c16;
        private long       c17;
        private long       c18;
        private long       c19;
        private final Lock lock = new ReentrantLock();

        public long getCount() {
            return c1;
        }

        public long getC0() {
            return c0;
        }

        public long getC1() {
            return c1;
        }

        public long getC2() {
            return c2;
        }

        public long getC3() {
            return c3;
        }

        public long getC4() {
            return c4;
        }

        public long getC5() {
            return c5;
        }

        public long getC6() {
            return c6;
        }

        public long getC7() {
            return c7;
        }

        public long getC8() {
            return c8;
        }

        public long getC9() {
            return c9;
        }

        public long getC10() {
            return c10;
        }

        public long getC11() {
            return c11;
        }

        public long getC12() {
            return c12;
        }

        public long getC13() {
            return c13;
        }

        public long getC14() {
            return c14;
        }

        public long getC15() {
            return c15;
        }

        public long getC16() {
            return c16;
        }

        public long getC17() {
            return c17;
        }

        public long getC18() {
            return c18;
        }

        public long getC19() {
            return c19;
        }

        public void increment() {
            lock.lock();
            try {
                c0++;
                c1++;
                c2++;
                c3++;
                c4++;
                c5++;
                c6++;
                c7++;
                c8++;
                c9++;
                c10++;
                c11++;
                c12++;
                c13++;
                c14++;
                c15++;
                c16++;
                c17++;
                c18++;
                c19++;
            } finally {
                lock.unlock();
            }
        }

    }
}
