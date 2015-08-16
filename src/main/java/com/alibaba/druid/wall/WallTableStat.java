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
package com.alibaba.druid.wall;

import static com.alibaba.druid.util.JdbcSqlStatUtils.get;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;
import java.util.concurrent.atomic.AtomicLongFieldUpdater;

import com.alibaba.druid.support.json.JSONUtils;

public class WallTableStat {

    private volatile long                                 selectCount;
    private volatile long                                 selectIntoCount;
    private volatile long                                 insertCount;
    private volatile long                                 updateCount;
    private volatile long                                 deleteCount;
    private volatile long                                 truncateCount;
    private volatile long                                 createCount;
    private volatile long                                 alterCount;
    private volatile long                                 dropCount;
    private volatile long                                 replaceCount;
    private volatile long                                 deleteDataCount;
    private volatile long                                 updateDataCount;
    private volatile long                                 insertDataCount;
    private volatile long                                 fetchRowCount;

    final static AtomicLongFieldUpdater<WallTableStat>    selectCountUpdater                 = AtomicLongFieldUpdater.newUpdater(WallTableStat.class,
                                                                                                                                 "selectCount");
    final static AtomicLongFieldUpdater<WallTableStat>    selectIntoCountUpdater             = AtomicLongFieldUpdater.newUpdater(WallTableStat.class,
                                                                                                                                 "selectIntoCount");
    final static AtomicLongFieldUpdater<WallTableStat>    insertCountUpdater                 = AtomicLongFieldUpdater.newUpdater(WallTableStat.class,
                                                                                                                                 "insertCount");
    final static AtomicLongFieldUpdater<WallTableStat>    updateCountUpdater                 = AtomicLongFieldUpdater.newUpdater(WallTableStat.class,
                                                                                                                                 "updateCount");
    final static AtomicLongFieldUpdater<WallTableStat>    deleteCountUpdater                 = AtomicLongFieldUpdater.newUpdater(WallTableStat.class,
                                                                                                                                 "deleteCount");
    final static AtomicLongFieldUpdater<WallTableStat>    truncateCountUpdater               = AtomicLongFieldUpdater.newUpdater(WallTableStat.class,
                                                                                                                                 "truncateCount");
    final static AtomicLongFieldUpdater<WallTableStat>    createCountUpdater                 = AtomicLongFieldUpdater.newUpdater(WallTableStat.class,
                                                                                                                                 "createCount");
    final static AtomicLongFieldUpdater<WallTableStat>    alterCountUpdater                  = AtomicLongFieldUpdater.newUpdater(WallTableStat.class,
                                                                                                                                 "alterCount");
    final static AtomicLongFieldUpdater<WallTableStat>    dropCountUpdater                   = AtomicLongFieldUpdater.newUpdater(WallTableStat.class,
                                                                                                                                 "dropCount");
    final static AtomicLongFieldUpdater<WallTableStat>    replaceCountUpdater                = AtomicLongFieldUpdater.newUpdater(WallTableStat.class,
                                                                                                                                 "replaceCount");

    final static AtomicLongFieldUpdater<WallTableStat>    deleteDataCountUpdater             = AtomicLongFieldUpdater.newUpdater(WallTableStat.class,
                                                                                                                                 "deleteDataCount");
    final static AtomicLongFieldUpdater<WallTableStat>    insertDataCountUpdater             = AtomicLongFieldUpdater.newUpdater(WallTableStat.class,
                                                                                                                                 "insertDataCount");
    final static AtomicLongFieldUpdater<WallTableStat>    updateDataCountUpdater             = AtomicLongFieldUpdater.newUpdater(WallTableStat.class,
                                                                                                                                 "updateDataCount");

    final static AtomicLongFieldUpdater<WallTableStat>    fetchRowCountUpdater               = AtomicLongFieldUpdater.newUpdater(WallTableStat.class,
                                                                                                                                 "fetchRowCount");

    private volatile long                                 fetchRowCount_0_1;
    private volatile long                                 fetchRowCount_1_10;
    private volatile long                                 fetchRowCount_10_100;
    private volatile int                                  fetchRowCount_100_1000;
    private volatile int                                  fetchRowCount_1000_10000;
    private volatile int                                  fetchRowCount_10000_more;

    final static AtomicLongFieldUpdater<WallTableStat>    fetchRowCount_0_1_Updater          = AtomicLongFieldUpdater.newUpdater(WallTableStat.class,
                                                                                                                                 "fetchRowCount_0_1");
    final static AtomicLongFieldUpdater<WallTableStat>    fetchRowCount_1_10_Updater         = AtomicLongFieldUpdater.newUpdater(WallTableStat.class,
                                                                                                                                 "fetchRowCount_1_10");
    final static AtomicLongFieldUpdater<WallTableStat>    fetchRowCount_10_100_Updater       = AtomicLongFieldUpdater.newUpdater(WallTableStat.class,
                                                                                                                                 "fetchRowCount_10_100");
    final static AtomicIntegerFieldUpdater<WallTableStat> fetchRowCount_100_1000_Updater     = AtomicIntegerFieldUpdater.newUpdater(WallTableStat.class,
                                                                                                                                    "fetchRowCount_100_1000");
    final static AtomicIntegerFieldUpdater<WallTableStat> fetchRowCount_1000_10000_Updater   = AtomicIntegerFieldUpdater.newUpdater(WallTableStat.class,
                                                                                                                                    "fetchRowCount_1000_10000");
    final static AtomicIntegerFieldUpdater<WallTableStat> fetchRowCount_10000_more_Updater   = AtomicIntegerFieldUpdater.newUpdater(WallTableStat.class,
                                                                                                                                    "fetchRowCount_10000_more");

    private volatile long                                 updateDataCount_0_1;
    private volatile long                                 updateDataCount_1_10;
    private volatile long                                 updateDataCount_10_100;
    private volatile int                                  updateDataCount_100_1000;
    private volatile int                                  updateDataCount_1000_10000;
    private volatile int                                  updateDataCount_10000_more;

    final static AtomicLongFieldUpdater<WallTableStat>    updateDataCount_0_1_Updater        = AtomicLongFieldUpdater.newUpdater(WallTableStat.class,
                                                                                                                                 "updateDataCount_0_1");
    final static AtomicLongFieldUpdater<WallTableStat>    updateDataCount_1_10_Updater       = AtomicLongFieldUpdater.newUpdater(WallTableStat.class,
                                                                                                                                 "updateDataCount_1_10");
    final static AtomicLongFieldUpdater<WallTableStat>    updateDataCount_10_100_Updater     = AtomicLongFieldUpdater.newUpdater(WallTableStat.class,
                                                                                                                                 "updateDataCount_10_100");
    final static AtomicIntegerFieldUpdater<WallTableStat> updateDataCount_100_1000_Updater   = AtomicIntegerFieldUpdater.newUpdater(WallTableStat.class,
                                                                                                                                    "updateDataCount_100_1000");
    final static AtomicIntegerFieldUpdater<WallTableStat> updateDataCount_1000_10000_Updater = AtomicIntegerFieldUpdater.newUpdater(WallTableStat.class,
                                                                                                                                    "updateDataCount_1000_10000");
    final static AtomicIntegerFieldUpdater<WallTableStat> updateDataCount_10000_more_Updater = AtomicIntegerFieldUpdater.newUpdater(WallTableStat.class,
                                                                                                                                    "updateDataCount_10000_more");

    private volatile long                                 deleteDataCount_0_1;
    private volatile long                                 deleteDataCount_1_10;
    private volatile long                                 deleteDataCount_10_100;
    private volatile int                                  deleteDataCount_100_1000;
    private volatile int                                  deleteDataCount_1000_10000;
    private volatile int                                  deleteDataCount_10000_more;

    final static AtomicLongFieldUpdater<WallTableStat>    deleteDataCount_0_1_Updater        = AtomicLongFieldUpdater.newUpdater(WallTableStat.class,
                                                                                                                                 "deleteDataCount_0_1");
    final static AtomicLongFieldUpdater<WallTableStat>    deleteDataCount_1_10_Updater       = AtomicLongFieldUpdater.newUpdater(WallTableStat.class,
                                                                                                                                 "deleteDataCount_1_10");
    final static AtomicLongFieldUpdater<WallTableStat>    deleteDataCount_10_100_Updater     = AtomicLongFieldUpdater.newUpdater(WallTableStat.class,
                                                                                                                                 "deleteDataCount_10_100");
    final static AtomicIntegerFieldUpdater<WallTableStat> deleteDataCount_100_1000_Updater   = AtomicIntegerFieldUpdater.newUpdater(WallTableStat.class,
                                                                                                                                    "deleteDataCount_100_1000");
    final static AtomicIntegerFieldUpdater<WallTableStat> deleteDataCount_1000_10000_Updater = AtomicIntegerFieldUpdater.newUpdater(WallTableStat.class,
                                                                                                                                    "deleteDataCount_1000_10000");
    final static AtomicIntegerFieldUpdater<WallTableStat> deleteDataCount_10000_more_Updater = AtomicIntegerFieldUpdater.newUpdater(WallTableStat.class,
                                                                                                                                    "deleteDataCount_10000_more");

    public WallTableStat(){

    }

    public long getSelectCount() {
        return selectCount;
    }

    public long getSelectIntoCount() {
        return selectIntoCount;
    }

    public long getInsertCount() {
        return insertCount;
    }

    public long getUpdateCount() {
        return updateCount;
    }

    public long getDeleteCount() {
        return deleteCount;
    }

    public long getTruncateCount() {
        return truncateCount;
    }

    public long getCreateCount() {
        return createCount;
    }

    public long getAlterCount() {
        return alterCount;
    }

    public long getDropCount() {
        return dropCount;
    }

    public long getReplaceCount() {
        return replaceCount;
    }

    public long getDeleteDataCount() {
        return this.deleteDataCount;
    }

    public long[] getDeleteDataCountHistogramValues() {
        return new long[] {
                //
                deleteDataCount_0_1, //
                deleteDataCount_1_10, //
                deleteDataCount_10_100, //
                deleteDataCount_100_1000, //
                deleteDataCount_1000_10000, //
                deleteDataCount_10000_more //
        };
    }

    public void addDeleteDataCount(long delta) {
        deleteDataCountUpdater.addAndGet(this, delta);
        
        if (delta < 1) {
            deleteDataCount_0_1_Updater.incrementAndGet(this);
        } else if (delta < 10) {
            deleteDataCount_1_10_Updater.incrementAndGet(this);
        } else if (delta < 100) {
            deleteDataCount_10_100_Updater.incrementAndGet(this);
        } else if (delta < 1000) {
            deleteDataCount_100_1000_Updater.incrementAndGet(this);
        } else if (delta < 10000) {
            deleteDataCount_1000_10000_Updater.incrementAndGet(this);
        } else {
            deleteDataCount_10000_more_Updater.incrementAndGet(this);
        }
    }

    public long getUpdateDataCount() {
        return this.updateDataCount;
    }

    public long[] getUpdateDataCountHistogramValues() {
        return new long[] {
                //
                updateDataCount_0_1, //
                updateDataCount_1_10, //
                updateDataCount_10_100, //
                updateDataCount_100_1000, //
                updateDataCount_1000_10000, //
                updateDataCount_10000_more //
        };
    }

    public long getInsertDataCount() {
        return this.insertDataCount;
    }

    public void addInsertDataCount(long delta) {
        insertDataCountUpdater.addAndGet(this, delta);
    }

    public void addUpdateDataCount(long delta) {
        updateDataCountUpdater.addAndGet(this, delta);

        if (delta < 1) {
            updateDataCount_0_1_Updater.incrementAndGet(this);
        } else if (delta < 10) {
            updateDataCount_1_10_Updater.incrementAndGet(this);
        } else if (delta < 100) {
            updateDataCount_10_100_Updater.incrementAndGet(this);
        } else if (delta < 1000) {
            updateDataCount_100_1000_Updater.incrementAndGet(this);
        } else if (delta < 10000) {
            updateDataCount_1000_10000_Updater.incrementAndGet(this);
        } else {
            updateDataCount_10000_more_Updater.incrementAndGet(this);
        }
    }

    public long getFetchRowCount() {
        return fetchRowCount;
    }

    public long[] getFetchRowCountHistogramValues() {
        return new long[] {
                //
                fetchRowCount_0_1, //
                fetchRowCount_1_10, //
                fetchRowCount_10_100, //
                fetchRowCount_100_1000, //
                fetchRowCount_1000_10000, //
                fetchRowCount_10000_more //
        };
    }

    public void addFetchRowCount(long delta) {
        fetchRowCountUpdater.addAndGet(this, delta);

        if (delta < 1) {
            fetchRowCount_0_1_Updater.incrementAndGet(this);
        } else if (delta < 10) {
            fetchRowCount_1_10_Updater.incrementAndGet(this);
        } else if (delta < 100) {
            fetchRowCount_10_100_Updater.incrementAndGet(this);
        } else if (delta < 1000) {
            fetchRowCount_100_1000_Updater.incrementAndGet(this);
        } else if (delta < 10000) {
            fetchRowCount_1000_10000_Updater.incrementAndGet(this);
        } else {
            fetchRowCount_10000_more_Updater.incrementAndGet(this);
        }
    }

    public void addSqlTableStat(WallSqlTableStat stat) {
        {
            long val = stat.getSelectCount();
            if (val > 0) {
                selectCountUpdater.addAndGet(this, val);
            }
        }
        {
            long val = stat.getSelectIntoCount();
            if (val > 0) {
                selectIntoCountUpdater.addAndGet(this, val);
            }
        }
        {
            long val = stat.getInsertCount();
            if (val > 0) {
                insertCountUpdater.addAndGet(this, val);
            }
        }
        {
            long val = stat.getUpdateCount();
            if (val > 0) {
                updateCountUpdater.addAndGet(this, val);
            }
        }
        {
            long val = stat.getDeleteCount();
            if (val > 0) {
                deleteCountUpdater.addAndGet(this, val);
            }
        }
        {
            long val = stat.getAlterCount();
            if (val > 0) {
                alterCountUpdater.addAndGet(this, val);
            }
        }
        {
            long val = stat.getTruncateCount();
            if (val > 0) {
                truncateCountUpdater.addAndGet(this, val);
            }
        }
        {
            long val = stat.getCreateCount();
            if (val > 0) {
                createCountUpdater.addAndGet(this, val);
            }
        }
        {
            long val = stat.getDropCount();
            if (val > 0) {
                dropCountUpdater.addAndGet(this, val);
            }
        }
        {
            long val = stat.getReplaceCount();
            if (val > 0) {
                replaceCountUpdater.addAndGet(this, val);
            }
        }
    }

    public String toString() {

        Map<String, Object> map = toMap();

        return JSONUtils.toJSONString(map);
    }

    public Map<String, Object> toMap() {
        Map<String, Object> map = new LinkedHashMap<String, Object>();
        return toMap(map);
    }

    public WallTableStatValue getStatValue(boolean reset) {
        WallTableStatValue statValue = new WallTableStatValue();

        statValue.setSelectCount(get(this, selectCountUpdater, reset));
        statValue.setDeleteCount(get(this, deleteCountUpdater, reset));
        statValue.setInsertCount(get(this, insertCountUpdater, reset));
        statValue.setUpdateCount(get(this, updateCountUpdater, reset));
        statValue.setAlterCount(get(this, alterCountUpdater, reset));
        statValue.setDropCount(get(this, dropCountUpdater, reset));
        statValue.setCreateCount(get(this, createCountUpdater, reset));
        statValue.setTruncateCount(get(this, truncateCountUpdater, reset));
        statValue.setReplaceCount(get(this, replaceCountUpdater, reset));
        statValue.setDeleteDataCount(get(this, deleteDataCountUpdater, reset));
        statValue.setFetchRowCount(get(this, fetchRowCountUpdater, reset));
        statValue.setUpdateDataCount(get(this, updateDataCountUpdater, reset));

        statValue.fetchRowCount_0_1 = get(this, fetchRowCount_0_1_Updater, reset);
        statValue.fetchRowCount_1_10 = get(this, fetchRowCount_1_10_Updater, reset);
        statValue.fetchRowCount_10_100 = get(this, fetchRowCount_10_100_Updater, reset);
        statValue.fetchRowCount_100_1000 = get(this, fetchRowCount_100_1000_Updater, reset);
        statValue.fetchRowCount_1000_10000 = get(this, fetchRowCount_1000_10000_Updater, reset);
        statValue.fetchRowCount_10000_more = get(this, fetchRowCount_10000_more_Updater, reset);

        statValue.updateDataCount_0_1 = get(this, updateDataCount_0_1_Updater, reset);
        statValue.updateDataCount_1_10 = get(this, updateDataCount_1_10_Updater, reset);
        statValue.updateDataCount_10_100 = get(this, updateDataCount_10_100_Updater, reset);
        statValue.updateDataCount_100_1000 = get(this, updateDataCount_100_1000_Updater, reset);
        statValue.updateDataCount_1000_10000 = get(this, updateDataCount_1000_10000_Updater, reset);
        statValue.updateDataCount_10000_more = get(this, updateDataCount_10000_more_Updater, reset);

        statValue.deleteDataCount_0_1 = get(this, deleteDataCount_0_1_Updater, reset);
        statValue.deleteDataCount_1_10 = get(this, deleteDataCount_1_10_Updater, reset);
        statValue.deleteDataCount_10_100 = get(this, deleteDataCount_10_100_Updater, reset);
        statValue.deleteDataCount_100_1000 = get(this, deleteDataCount_100_1000_Updater, reset);
        statValue.deleteDataCount_1000_10000 = get(this, deleteDataCount_1000_10000_Updater, reset);
        statValue.deleteDataCount_10000_more = get(this, deleteDataCount_10000_more_Updater, reset);

        return statValue;
    }

    public Map<String, Object> toMap(Map<String, Object> map) {
        return getStatValue(false).toMap(map);
    }
}
