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
package com.alibaba.druid.util;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLongArray;

public class Histogram {

    private final long[]          ranges;
    private final AtomicLongArray rangeCounters;

    public Histogram(long... ranges){
        this.ranges = ranges;
        this.rangeCounters = new AtomicLongArray(ranges.length + 1);
    }

    public static Histogram makeHistogram(int rangeCount) {
        long[] rangeValues = new long[rangeCount];

        for (int i = 0; i < rangeValues.length; ++i) {
            rangeValues[i] = (long) Math.pow(10, i);
        }

        return new Histogram(rangeValues);
    }

    public Histogram(TimeUnit timeUnit, long... ranges){
        this.ranges = new long[ranges.length];
        for (int i = 0; i < ranges.length; i++) {
            this.ranges[i] = TimeUnit.MILLISECONDS.convert(ranges[i], timeUnit);
        }

        rangeCounters = new AtomicLongArray(ranges.length + 1);
    }

    public void reset() {
        for (int i = 0; i < rangeCounters.length(); i++) {
            rangeCounters.set(i, 0);
        }
    }

    public void record(long millis) {
        int index = rangeCounters.length() - 1;
        for (int i = 0; i < ranges.length; ++i) {
            if (millis < ranges[i]) {
                index = i;
                break;
            }
        }

        rangeCounters.incrementAndGet(index);
    }

    public long get(int index) {
        return rangeCounters.get(index);
    }

    public long[] toArray() {
        long[] array = new long[rangeCounters.length()];
        for (int i = 0; i < rangeCounters.length(); i++) {
            array[i] = rangeCounters.get(i);
        }
        return array;
    }

    public long[] toArrayAndReset() {
        long[] array = new long[rangeCounters.length()];
        for (int i = 0; i < rangeCounters.length(); i++) {
            array[i] = rangeCounters.getAndSet(i, 0);
        }

        return array;
    }

    public long[] getRanges() {
        return ranges;
    }

    public long getValue(int index) {
        return rangeCounters.get(index);
    }

    public long getSum() {
        long sum = 0;
        for (int i = 0; i < rangeCounters.length(); ++i) {
            sum += rangeCounters.get(i);
        }
        return sum;
    }

    public String toString() {
        StringBuilder buf = new StringBuilder();
        buf.append('[');
        for (int i = 0; i < rangeCounters.length(); ++i) {
            if (i != 0) {
                buf.append(',');
            }
            buf.append(rangeCounters.get(i));
        }
        buf.append(']');
        return buf.toString();
    }
}
