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

    public void recode(long millis) {
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

    @Override
    public String toString() {
        StringBuilder strBuilder = new StringBuilder();
        for (int i = 0; i < ranges.length; i++) {
            strBuilder.append((i == 0 ? 0l : ranges[i - 1]) + " - " + ranges[i] + " : " + rangeCounters[i] + " ");
        }
        return strBuilder.toString();
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
}
