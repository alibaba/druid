/*
 * Copyright 1999-2011 Alibaba Group Holding Ltd.
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
import java.util.concurrent.atomic.AtomicLong;

public class Histogram {

    private final long[]       ranges;
    private final AtomicLong[] rangeCounters;

    public Histogram(long... ranges){
        this.ranges = ranges;
        this.rangeCounters = new AtomicLong[ranges.length + 1];
        for (int i = 0; i < rangeCounters.length; i++) {
            rangeCounters[i] = new AtomicLong();
        }
    }

    public Histogram(TimeUnit timeUnit, long... ranges){
        this.ranges = new long[ranges.length];
        for (int i = 0; i < ranges.length; i++) {
            this.ranges[i] = TimeUnit.MILLISECONDS.convert(ranges[i], timeUnit);
        }

        rangeCounters = new AtomicLong[ranges.length + 1];
        for (int i = 0; i < rangeCounters.length; ++i) {
            rangeCounters[i] = new AtomicLong();
        }
    }

    public void reset() {
        for (int i = 0; i < rangeCounters.length; i++) {
            rangeCounters[i].set(0);
        }
    }

    public void record(long millis) {
        int index = rangeCounters.length - 1;
        for (int i = 0; i < ranges.length; ++i) {
            if (millis < ranges[i]) {
                index = i;
                break;
            }
        }

        rangeCounters[index].incrementAndGet();
    }

    public long get(int index) {
        return rangeCounters[index].get();
    }

    public long[] toArray() {
        long[] array = new long[rangeCounters.length];
        for (int i = 0; i < rangeCounters.length; i++) {
            array[i] = rangeCounters[i].get();
        }
        return array;
    }

    public long[] getRanges() {
        return ranges;
    }
    
    public long getValue(int index) {
        return rangeCounters[index].get();
    }
    
    public long getSum() {
        long sum = 0;
        for (int i = 0; i < rangeCounters.length; ++i) {
            sum += rangeCounters[i].get();
        }
        return sum;
    }
    
    public String toString() {
    	StringBuilder buf = new StringBuilder();
    	buf.append('[');
    	for (int i = 0; i < rangeCounters.length; ++i) {
    		if (i != 0) {
    			buf.append(',');
    		}
    		buf.append(rangeCounters[i].get());
    	}
    	buf.append(']');
    	return buf.toString();
    }
}
