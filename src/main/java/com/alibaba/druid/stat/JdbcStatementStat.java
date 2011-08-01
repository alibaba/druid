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
package com.alibaba.druid.stat;

import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import javax.management.JMException;
import javax.management.openmbean.CompositeData;

import com.alibaba.druid.util.JMXUtils;

public class JdbcStatementStat implements JdbcStatementStatMBean {

    private final AtomicLong    createCount           = new AtomicLong(0);  // 执行createStatement的计数
    private final AtomicLong    prepareCount          = new AtomicLong(0);  // 执行parepareStatement的计数
    private final AtomicLong    prepareCallCount      = new AtomicLong(0);  // 执行preCall的计数
    private final AtomicLong    closeCount            = new AtomicLong(0);  // Statement关闭的计数

    private final AtomicInteger runningCount          = new AtomicInteger();
    private final AtomicInteger concurrentMax         = new AtomicInteger();

    private final AtomicLong    count                 = new AtomicLong();
    private final AtomicLong    errorCount            = new AtomicLong();

    private final AtomicLong    nanoTotal             = new AtomicLong();

    private Throwable           lastError;
    private long                lastErrorTime;

    private long                lastSampleTime        = 0;

    private AtomicLong          count_0_1_Millis      = new AtomicLong();
    private AtomicLong          count_1_2_Millis      = new AtomicLong();
    private AtomicLong          count_2_5_Millis      = new AtomicLong();
    private AtomicLong          count_5_10_Millis     = new AtomicLong();
    private AtomicLong          count_10_20_Millis    = new AtomicLong();

    private AtomicLong          count_20_50_Millis    = new AtomicLong();
    private AtomicLong          count_50_100_Millis   = new AtomicLong();
    private AtomicLong          count_100_200_Millis  = new AtomicLong();
    private AtomicLong          count_200_500_Millis  = new AtomicLong();
    private AtomicLong          count_500_1000_Millis = new AtomicLong();

    private AtomicLong          count_1_2_Seconds     = new AtomicLong();
    private AtomicLong          count_2_5_Seconds     = new AtomicLong();
    private AtomicLong          count_5_10_Seconds    = new AtomicLong();
    private AtomicLong          count_10_30_Seconds   = new AtomicLong();
    private AtomicLong          count_30_60_Seconds   = new AtomicLong();

    private AtomicLong          count_1_2_minutes     = new AtomicLong();
    private AtomicLong          count_2_5_minutes     = new AtomicLong();
    private AtomicLong          count_5_10_minutes    = new AtomicLong();
    private AtomicLong          count_10_30_minutes   = new AtomicLong();
    private AtomicLong          count_30_more_minutes = new AtomicLong();

    public void reset() {
        runningCount.set(0);
        concurrentMax.set(0);
        count.set(0);
        errorCount.set(0);
        nanoTotal.set(0);
        lastError = null;
        lastErrorTime = 0;
        lastSampleTime = 0;

        createCount.set(0);
        prepareCount.set(0);
        prepareCallCount.set(0);
        closeCount.set(0);

        count_0_1_Millis.set(0);
        count_1_2_Millis.set(0);
        count_2_5_Millis.set(0);
        count_5_10_Millis.set(0);
        count_10_20_Millis.set(0);

        count_20_50_Millis.set(0);
        count_50_100_Millis.set(0);
        count_100_200_Millis.set(0);
        count_200_500_Millis.set(0);
        count_500_1000_Millis.set(0);

        count_1_2_Seconds.set(0);
        count_2_5_Seconds.set(0);
        count_5_10_Seconds.set(0);
        count_10_30_Seconds.set(0);
        count_30_60_Seconds.set(0);

        count_1_2_minutes.set(0);
        count_2_5_minutes.set(0);
        count_5_10_minutes.set(0);
        count_10_30_minutes.set(0);
        count_30_more_minutes.set(0);
    }

    public void afterExecute(long nanoSpan) {
        runningCount.decrementAndGet();

        nanoTotal.addAndGet(nanoSpan);

        final long MILLIS = 1000 * 1000;
        final long SECOND = 1000 * MILLIS;
        final long MINUTE = 60 * SECOND;
        if (nanoSpan < MILLIS) {
            count_0_1_Millis.incrementAndGet();
        } else if (nanoSpan < 2 * MILLIS) {
            count_1_2_Millis.incrementAndGet();
        } else if (nanoSpan < 5 * MILLIS) {
            count_2_5_Millis.incrementAndGet();
        } else if (nanoSpan < 10 * MILLIS) {
            count_5_10_Millis.incrementAndGet();
        } else if (nanoSpan < 20 * MILLIS) {
            count_10_20_Millis.incrementAndGet();

        } else if (nanoSpan < 50 * MILLIS) {
            count_20_50_Millis.incrementAndGet();
        } else if (nanoSpan < 100 * MILLIS) {
            count_50_100_Millis.incrementAndGet();
        } else if (nanoSpan < 200 * MILLIS) {
            count_100_200_Millis.incrementAndGet();
        } else if (nanoSpan < 500 * MILLIS) {
            count_200_500_Millis.incrementAndGet();
        } else if (nanoSpan < 1000 * MILLIS) {
            count_500_1000_Millis.incrementAndGet();

        } else if (nanoSpan < 2 * SECOND) {
            count_1_2_Seconds.incrementAndGet();
        } else if (nanoSpan < 5 * SECOND) {
            count_2_5_Seconds.incrementAndGet();
        } else if (nanoSpan < 10 * SECOND) {
            count_5_10_Seconds.incrementAndGet();
        } else if (nanoSpan < 30 * SECOND) {
            count_10_30_Seconds.incrementAndGet();
        } else if (nanoSpan < 60 * SECOND) {
            count_30_60_Seconds.incrementAndGet();

        } else if (nanoSpan < 2 * MINUTE) {
            count_1_2_minutes.incrementAndGet();
        } else if (nanoSpan < 5 * MINUTE) {
            count_2_5_minutes.incrementAndGet();
        } else if (nanoSpan < 10 * MINUTE) {
            count_5_10_minutes.incrementAndGet();
        } else if (nanoSpan < 30 * MINUTE) {
            count_10_30_minutes.incrementAndGet();
        } else {
            count_30_more_minutes.incrementAndGet();
        }
    }

    public void beforeExecute() {
        int invoking = runningCount.incrementAndGet();

        for (;;) {
            int max = concurrentMax.get();
            if (invoking > max) {
                if (concurrentMax.compareAndSet(max, invoking)) {
                    break;
                } else {
                    continue;
                }
            } else {
                break;
            }
        }

        count.incrementAndGet();
        lastSampleTime = System.currentTimeMillis();
    }

    public long getErrorCount() {
        return errorCount.get();
    }

    public int getRunningCount() {
        return runningCount.get();
    }

    public int getConcurrentMax() {
        return concurrentMax.get();
    }

    public long getExecuteCount() {
        return count.get();
    }

    public Date getExecuteLastTime() {
        if (lastSampleTime == 0) {
            return null;
        }

        return new Date(lastSampleTime);
    }

    public long getNanoTotal() {
        return nanoTotal.get();
    }

    public long getMillisTotal() {
        return nanoTotal.get() / (1000 * 1000);
    }

    public Throwable getLastException() {
        return lastError;
    }

    public Date getLastErrorTime() {
        if (lastErrorTime <= 0) {
            return null;
        }

        return new Date(lastErrorTime);
    }

    public void error(Throwable error) {
        errorCount.incrementAndGet();
        lastError = error;
        lastErrorTime = System.currentTimeMillis();
    }

    @Override
    public long getCloseCount() {
        return closeCount.get();
    }

    @Override
    public long getCreateCount() {
        return createCount.get();
    }

    @Override
    public long getExecuteMillisTotal() {
        return this.getNanoTotal() / (1000 * 1000);
    }

    @Override
    public long getPrepareCallCount() {
        return prepareCallCount.get();
    }

    @Override
    public long getPrepareCount() {
        return prepareCount.get();
    }

    @Override
    public long getExecuteSuccessCount() {
        return this.getExecuteCount() - this.getErrorCount() - this.getRunningCount();
    }

    @Override
    public CompositeData getLastError() throws JMException {
        return JMXUtils.getErrorCompositeData(this.getLastException());
    }

    public void incrementCreateCounter() {
        createCount.incrementAndGet();
    }

    public void incrementPrepareCallCount() {
        prepareCallCount.incrementAndGet();
    }

    public void incrementPrepareCounter() {
        prepareCount.incrementAndGet();
    }

    public void incrementStatementCloseCounter() {
        closeCount.incrementAndGet();
    }

    public long getCount_0_1_Millis() {
        return count_0_1_Millis.get();
    }

    public long getCount_1_2_Millis() {
        return count_1_2_Millis.get();
    }

    public long getCount_2_5_Millis() {
        return count_2_5_Millis.get();
    }

    public long getCount_5_10_Millis() {
        return count_5_10_Millis.get();
    }

    public long getCount_10_20_Millis() {
        return count_10_20_Millis.get();
    }

    public long getCount_20_50_Millis() {
        return count_20_50_Millis.get();
    }

    public long getCount_50_100_Millis() {
        return count_50_100_Millis.get();
    }

    public long getCount_100_200_Millis() {
        return count_100_200_Millis.get();
    }

    public long getCount_200_500_Millis() {
        return count_200_500_Millis.get();
    }

    public long getCount_500_1000_Millis() {
        return count_500_1000_Millis.get();
    }

    public long getCount_1_2_Seconds() {
        return count_1_2_Seconds.get();
    }

    public long getCount_2_5_Seconds() {
        return count_2_5_Seconds.get();
    }

    public long getCount_5_10_Seconds() {
        return count_5_10_Seconds.get();
    }

    public long getCount_10_30_Seconds() {
        return count_10_30_Seconds.get();
    }

    public long getCount_30_60_Seconds() {
        return count_30_60_Seconds.get();
    }

    public long getCount_1_2_minutes() {
        return count_1_2_minutes.get();
    }

    public long getCount_2_5_minutes() {
        return count_2_5_minutes.get();
    }

    public long getCount_5_10_minutes() {
        return count_5_10_minutes.get();
    }

    public long getCount_10_30_minutes() {
        return count_10_30_minutes.get();
    }

    public long getCount_30_more_minutes() {
        return count_30_more_minutes.get();
    }

    public static class Entry {

        private long   lastExecuteStartNano;
        private String lastExecuteSql;

        public long getLastExecuteStartNano() {
            return lastExecuteStartNano;
        }

        public void setLastExecuteStartNano(long lastExecuteStartNano) {
            this.lastExecuteStartNano = lastExecuteStartNano;
        }

        public String getLastExecuteSql() {
            return lastExecuteSql;
        }

        public void setLastExecuteSql(String lastExecuteSql) {
            this.lastExecuteSql = lastExecuteSql;
        }
    }
}
