/*
 * Copyright 1999-2101 Alibaba Group Holding Ltd.
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
package com.alibaba.druid.benckmark.proxy;

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
