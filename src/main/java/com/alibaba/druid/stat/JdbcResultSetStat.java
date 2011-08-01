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

public class JdbcResultSetStat implements JdbcResultSetStatMBean {

    private final AtomicInteger openningCount  = new AtomicInteger();
    private final AtomicInteger opeeningtMax   = new AtomicInteger();

    private final AtomicLong    openCount      = new AtomicLong();
    private final AtomicLong    errorCount     = new AtomicLong();

    private final AtomicLong    aliveNanoTotal = new AtomicLong();
    private final AtomicLong    aliveNanoMax   = new AtomicLong();
    private final AtomicLong    aliveNanoMin   = new AtomicLong();
    private Throwable           lastError;
    private long                lastErrorTime;

    private long                lastOpenTime   = 0;

    private final AtomicLong    fetchRowCount  = new AtomicLong(0);  // 总共读取的行数
    private final AtomicLong    closeCount     = new AtomicLong(0);  // ResultSet打开的计数

    public void reset() {
        opeeningtMax.set(0);
        openCount.set(0);
        errorCount.set(0);
        aliveNanoTotal.set(0);
        aliveNanoMax.set(0);
        aliveNanoMin.set(0);
        lastError = null;
        lastErrorTime = 0;
        lastOpenTime = 0;
        fetchRowCount.set(0);
        closeCount.set(0);
    }

    public void beforeOpen() {
        int invoking = openningCount.incrementAndGet();

        for (;;) {
            int max = opeeningtMax.get();
            if (invoking > max) {
                if (opeeningtMax.compareAndSet(max, invoking)) {
                    break;
                } else {
                    continue;
                }
            } else {
                break;
            }
        }

        openCount.incrementAndGet();
        lastOpenTime = System.currentTimeMillis();
    }

    public long getErrorCount() {
        return errorCount.get();
    }

    public int getOpenningCount() {
        return openningCount.get();
    }

    public int getOpenningMax() {
        return opeeningtMax.get();
    }

    public long getOpenCount() {
        return openCount.get();
    }

    public Date getLastOpenTime() {
        if (lastOpenTime == 0) {
            return null;
        }

        return new Date(lastOpenTime);
    }

    public long getAliveNanoTotal() {
        return aliveNanoTotal.get();
    }

    public long getAliveMillisTotal() {
        return aliveNanoTotal.get() / (1000 * 1000);
    }

    public long getAliveMilisMin() {
        return aliveNanoMin.get() / (1000 * 1000);
    }

    public long getAliveMilisMax() {
        return aliveNanoMax.get() / (1000 * 1000);
    }

    public void afterClose(long aliveNano) {
        openningCount.decrementAndGet();

        aliveNanoTotal.addAndGet(aliveNano);

        for (;;) {
            long max = aliveNanoMax.get();
            if (aliveNano > max) {
                if (aliveNanoMax.compareAndSet(max, aliveNano)) {
                    break;
                } else {
                    continue;
                }
            } else {
                break;
            }
        }

        for (;;) {
            long min = aliveNanoMin.get();
            if (aliveNano < min) {
                if (aliveNanoMin.compareAndSet(min, aliveNano)) {
                    break;
                } else {
                    continue;
                }
            } else {
                break;
            }
        }
    }

    public Throwable getLastError() {
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
    public long getHoldMillisTotal() {
        return JdbcStatManager.getInstance().getResultSetStat().getAliveNanoTotal() / (1000 * 1000);
    }

    @Override
    public long getFetchRowCount() {
        return fetchRowCount.get();
    }

    @Override
    public long getCloseCount() {
        return closeCount.get();
    }

    public void addFetchRowCount(long fetchCount) {
        fetchRowCount.addAndGet(fetchCount);
    }

    public void incrementCloseCounter() {
        closeCount.incrementAndGet();
    }

    public static class Entry {

        protected final long constructNano;
        protected int        cusorIndex    = 0;
        protected int        fetchRowCount = 0;

        public Entry(){
            this.constructNano = System.nanoTime();
        }

        public void decrementCusorIndex() {
            cusorIndex--;
        }

        public long getConstructNano() {
            return constructNano;
        }

        public int getCusorIndex() {
            return cusorIndex;
        }

        public void setCusorIndex(int cusorIndex) {
            this.cusorIndex = cusorIndex;
        }

        public int getFetchRowCount() {
            return fetchRowCount;
        }

        public void setFetchRowCount(int fetchRowCount) {
            this.fetchRowCount = fetchRowCount;
        }
    }
}
