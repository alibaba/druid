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
package com.alibaba.druid.pool;

import java.util.Date;
import java.util.List;

import com.alibaba.druid.stat.JdbcSqlStatValue;

public class DruidDataSourceStatValue {

    public String                 name;
    public String                 dbType;
    public String                 driverClassName;
    public String                 url;
    public String                 userName;
    public List<String>           filterClassNames;
    public boolean                removeAbandoned;

    public int                    initialSize;
    public int                    minIdle;
    public int                    maxActive;
    public int                    queryTimeout;
    public int                    transactionQueryTimeout;
    public int                    loginTimeout;
    public String                 validConnectionCheckerClassName;
    public String                 exceptionSorterClassName;
    public boolean                testOnBorrow;
    public boolean                testOnReturn;
    public boolean                testWhileIdle;
    public boolean                defaultAutoCommit;
    public boolean                defaultReadOnly;
    public Integer                defaultTransactionIsolation;

    public int                    activeCount;
    public int                    activePeak;
    public long                   activePeakTime;

    public int                    poolingCount;
    public int                    poolingPeak;
    public long                   poolingPeakTime;

    public long                   connectCount;
    public long                   closeCount;
    public long                   waitThreadCount;
    public long                   notEmptyWaitCount;
    public long                   notEmptyWaitNanos;

    public long                   logicConnectErrorCount;
    public long                   physicalConnectCount;
    public long                   physicalCloseCount;
    public long                   physicalConnectErrorCount;
    public long                   executeCount;
    public long                   errorCount;
    public long                   commitCount;
    public long                   rollbackCount;
    public long                   pstmtCacheHitCount;
    public long                   pstmtCacheMissCount;
    public long                   startTransactionCount;
    public long[]                 transactionHistogram;
    public long[]                 connectionHoldTimeHistogram;

    public long                   clobOpenCount;
    public long                   blobOpenCount;

    public List<JdbcSqlStatValue> sqlList;

    public Date getPoolingPeakTime() {
        if (poolingPeakTime <= 0) {
            return null;
        }

        return new Date(poolingPeakTime);
    }

    public Date getActivePeakTime() {
        if (activePeakTime <= 0) {
            return null;
        }

        return new Date(activePeakTime);
    }

    public long getNotEmptyWaitMillis() {
        return notEmptyWaitNanos / (1000 * 1000);
    }
}
