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

public class JdbcSqlStatValue {

    public String    sql;
    public long      id;
    public String    dataSource;
    public long      executeLastStartTime;

    public long      executeBatchSizeTotal;
    public int       executeBatchSizeMax;

    public long      executeSuccessCount;
    public long      executeSpanNanoTotal;
    public long      executeSpanNanoMax;
    public int       runningCount;
    public int       concurrentMax;
    public long      resultSetHoldTimeNano;
    public long      executeAndResultSetHoldTime;

    public String    name;
    public String    file;
    public String    dbType;

    public long      executeNanoSpanMaxOccurTime;

    public long      executeErrorCount;
    public Throwable executeErrorLast;
    public long      executeErrorLastTime;

    public long      updateCount;
    public long      updateCountMax;
    public long      fetchRowCount;
    public long      fetchRowCountMax;

    public long      inTransactionCount;

    public String    lastSlowParameters;

    public long      clobOpenCount;
    public long      blobOpenCount;
    public long      readStringLength;
    public long      readBytesLength;

    public long      inputStreamOpenCount;
    public long      readerOpenCount;

    public long      histogram_0_1;
    public long      histogram_1_10;
    public int       histogram_10_100;
    public int       histogram_100_1000;
    public int       histogram_1000_10000;
    public int       histogram_10000_100000;
    public int       histogram_100000_1000000;
    public int       histogram_1000000_more;

    public long[] getExecuteHistogram() {
        return new long[] { histogram_0_1, //
                histogram_1_10, //
                histogram_10_100, //
                histogram_100_1000, //
                histogram_1000_10000, //
                histogram_10000_100000, //
                histogram_100000_1000000, //
                histogram_1000000_more, //
        };
    }

    public long executeAndResultHoldTime_0_1;
    public long executeAndResultHoldTime_1_10;
    public int  executeAndResultHoldTime_10_100;
    public int  executeAndResultHoldTime_100_1000;
    public int  executeAndResultHoldTime_1000_10000;
    public int  executeAndResultHoldTime_10000_100000;
    public int  executeAndResultHoldTime_100000_1000000;
    public int  executeAndResultHoldTime_1000000_more;

    public long[] getExecuteAndResultHoldHistogram() {
        return new long[] { executeAndResultHoldTime_0_1, //
                executeAndResultHoldTime_1_10, //
                executeAndResultHoldTime_10_100, //
                executeAndResultHoldTime_100_1000, //
                executeAndResultHoldTime_1000_10000, //
                executeAndResultHoldTime_10000_100000, //
                executeAndResultHoldTime_100000_1000000, //
                executeAndResultHoldTime_1000000_more, //
        };
    }

    public long fetchRowCount_0_1;
    public long fetchRowCount_1_10;
    public long fetchRowCount_10_100;
    public int  fetchRowCount_100_1000;
    public int  fetchRowCount_1000_10000;
    public int  fetchRowCount_10000_more;

    public long[] getFetchRowHistogram() {
        return new long[] { fetchRowCount_0_1, //
                fetchRowCount_1_10, //
                fetchRowCount_10_100, //
                fetchRowCount_100_1000, //
                fetchRowCount_1000_10000, //
                fetchRowCount_10000_more, //
        };
    }

    public long updateCount_0_1;
    public long updateCount_1_10;
    public long updateCount_10_100;
    public int  updateCount_100_1000;
    public int  updateCount_1000_10000;
    public int  updateCount_10000_more;

    public long[] getUpdateHistogram() {
        return new long[] { updateCount_0_1, //
                updateCount_1_10, //
                updateCount_10_100, //
                updateCount_100_1000, //
                updateCount_1000_10000, //
                updateCount_10000_more, //
        };
    }

    public long getExecuteCount() {
        return executeErrorCount + executeSuccessCount;
    }

    public long getExecuteMillisMax() {
        return executeSpanNanoMax / (1000 * 1000);
    }
    
    public long getExecuteMillisTotal() {
        return executeSpanNanoTotal / (1000 * 1000);
    }
}
