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
package com.alibaba.druid.wall;

import java.util.LinkedHashMap;
import java.util.Map;

import com.alibaba.druid.support.monitor.annotation.AggregateType;
import com.alibaba.druid.support.monitor.annotation.MField;
import com.alibaba.druid.support.monitor.annotation.MTable;

@MTable(name = "druid_wall_table")
public class WallTableStatValue {

    @MField(aggregate = AggregateType.None)
    private String name;

    @MField(aggregate = AggregateType.Sum)
    private long   selectCount;

    @MField(aggregate = AggregateType.Sum)
    private long   selectIntoCount;

    @MField(aggregate = AggregateType.Sum)
    private long   insertCount;

    @MField(aggregate = AggregateType.Sum)
    private long   updateCount;

    @MField(aggregate = AggregateType.Sum)
    private long   deleteCount;

    @MField(aggregate = AggregateType.Sum)
    private long   truncateCount;

    @MField(aggregate = AggregateType.Sum)
    private long   createCount;

    @MField(aggregate = AggregateType.Sum)
    private long   alterCount;

    @MField(aggregate = AggregateType.Sum)
    private long   dropCount;

    @MField(aggregate = AggregateType.Sum)
    private long   replaceCount;

    @MField(aggregate = AggregateType.Sum)
    private long   deleteDataCount;

    @MField(aggregate = AggregateType.Sum)
    private long   updateDataCount;

    @MField(aggregate = AggregateType.Sum)
    private long   insertDataCount;

    @MField(aggregate = AggregateType.Sum)
    private long   fetchRowCount;

    @MField(name = "f1", aggregate = AggregateType.Sum)
    protected long fetchRowCount_0_1;

    @MField(name = "f10", aggregate = AggregateType.Sum)
    protected long fetchRowCount_1_10;

    @MField(name = "f100", aggregate = AggregateType.Sum)
    protected long fetchRowCount_10_100;

    @MField(name = "f1000", aggregate = AggregateType.Sum)
    protected int  fetchRowCount_100_1000;

    @MField(name = "f10000", aggregate = AggregateType.Sum)
    protected int  fetchRowCount_1000_10000;

    @MField(name = "fmore", aggregate = AggregateType.Sum)
    protected int  fetchRowCount_10000_more;
    
    @MField(name = "u1", aggregate = AggregateType.Sum)
    protected long updateDataCount_0_1;

    @MField(name = "u10", aggregate = AggregateType.Sum)
    protected long updateDataCount_1_10;

    @MField(name = "u100", aggregate = AggregateType.Sum)
    protected long updateDataCount_10_100;

    @MField(name = "u1000", aggregate = AggregateType.Sum)
    protected int  updateDataCount_100_1000;

    @MField(name = "u10000", aggregate = AggregateType.Sum)
    protected int  updateDataCount_1000_10000;

    @MField(name = "umore", aggregate = AggregateType.Sum)
    protected int  updateDataCount_10000_more;
    
    @MField(name = "del_1", aggregate = AggregateType.Sum)
    protected long deleteDataCount_0_1;

    @MField(name = "del_10", aggregate = AggregateType.Sum)
    protected long deleteDataCount_1_10;

    @MField(name = "del_100", aggregate = AggregateType.Sum)
    protected long deleteDataCount_10_100;

    @MField(name = "del_1000", aggregate = AggregateType.Sum)
    protected int  deleteDataCount_100_1000;

    @MField(name = "del_10000", aggregate = AggregateType.Sum)
    protected int  deleteDataCount_1000_10000;

    @MField(name = "del_more", aggregate = AggregateType.Sum)
    protected int  deleteDataCount_10000_more;

    public long[] getDeleteDataHistogram() {
        return new long[] { deleteDataCount_0_1, //
                deleteDataCount_1_10, //
                deleteDataCount_10_100, //
                deleteDataCount_100_1000, //
                deleteDataCount_1000_10000, //
                deleteDataCount_10000_more, //
        };
    }

    public WallTableStatValue(){
        this(null);
    }

    public WallTableStatValue(String name){
        this.name = name;
    }

    public long[] getFetchRowHistogram() {
        return new long[] { fetchRowCount_0_1, //
                fetchRowCount_1_10, //
                fetchRowCount_10_100, //
                fetchRowCount_100_1000, //
                fetchRowCount_1000_10000, //
                fetchRowCount_10000_more, //
        };
    }

    public long getTotalExecuteCount() {
        return selectCount //
               + selectIntoCount //
               + insertCount //
               + updateCount //
               + deleteCount //
               + truncateCount //
               + createCount //
               + dropCount //
               + replaceCount //
        ;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getSelectCount() {
        return selectCount;
    }

    public void setSelectCount(long selectCount) {
        this.selectCount = selectCount;
    }

    public long getSelectIntoCount() {
        return selectIntoCount;
    }

    public void setSelectIntoCount(long selectIntoCount) {
        this.selectIntoCount = selectIntoCount;
    }

    public long getInsertCount() {
        return insertCount;
    }

    public void setInsertCount(long insertCount) {
        this.insertCount = insertCount;
    }

    public long getUpdateCount() {
        return updateCount;
    }

    public void setUpdateCount(long updateCount) {
        this.updateCount = updateCount;
    }

    public long getDeleteCount() {
        return deleteCount;
    }

    public void setDeleteCount(long deleteCount) {
        this.deleteCount = deleteCount;
    }

    public long getTruncateCount() {
        return truncateCount;
    }

    public void setTruncateCount(long truncateCount) {
        this.truncateCount = truncateCount;
    }

    public long getCreateCount() {
        return createCount;
    }

    public void setCreateCount(long createCount) {
        this.createCount = createCount;
    }

    public long getAlterCount() {
        return alterCount;
    }

    public void setAlterCount(long alterCount) {
        this.alterCount = alterCount;
    }

    public long getDropCount() {
        return dropCount;
    }

    public void setDropCount(long dropCount) {
        this.dropCount = dropCount;
    }

    public long getReplaceCount() {
        return replaceCount;
    }

    public void setReplaceCount(long replaceCount) {
        this.replaceCount = replaceCount;
    }

    public long getDeleteDataCount() {
        return deleteDataCount;
    }

    public void setDeleteDataCount(long deleteDataCount) {
        this.deleteDataCount = deleteDataCount;
    }

    public long getUpdateDataCount() {
        return updateDataCount;
    }
    
    public long[] getUpdateDataHistogram() {
        return new long[] { updateDataCount_0_1, //
                updateDataCount_1_10, //
                updateDataCount_10_100, //
                updateDataCount_100_1000, //
                updateDataCount_1000_10000, //
                updateDataCount_10000_more, //
        };
    }

    public void setUpdateDataCount(long updateDataCount) {
        this.updateDataCount = updateDataCount;
    }

    public long getInsertDataCount() {
        return insertDataCount;
    }

    public void setInsertDataCount(long insertDataCount) {
        this.insertDataCount = insertDataCount;
    }

    public long getFetchRowCount() {
        return fetchRowCount;
    }

    public void setFetchRowCount(long fetchRowCount) {
        this.fetchRowCount = fetchRowCount;
    }

    public Map<String, Object> toMap() {
        Map<String, Object> map = new LinkedHashMap<String, Object>();
        toMap(map);
        return map;
    }

    public Map<String, Object> toMap(Map<String, Object> map) {
        map.put("name", name);

        if (selectCount > 0) {
            map.put("selectCount", selectCount);
        }
        if (deleteCount > 0) {
            map.put("deleteCount", deleteCount);
        }
        if (insertCount > 0) {
            map.put("insertCount", insertCount);
        }
        if (updateCount > 0) {
            map.put("updateCount", updateCount);
        }
        if (alterCount > 0) {
            map.put("alterCount", alterCount);
        }
        if (dropCount > 0) {
            map.put("dropCount", dropCount);
        }
        if (createCount > 0) {
            map.put("createCount", createCount);
        }
        if (truncateCount > 0) {
            map.put("truncateCount", truncateCount);
        }
        if (replaceCount > 0) {
            map.put("replaceCount", replaceCount);
        }
        if (deleteDataCount > 0) {
            map.put("deleteDataCount", deleteDataCount);
            map.put("deleteDataCountHistogram", getDeleteDataHistogram());
        }
        if (fetchRowCount > 0) {
            map.put("fetchRowCount", fetchRowCount);
            map.put("fetchRowCountHistogram", getFetchRowHistogram());
        }
        if (updateDataCount > 0) {
            map.put("updateDataCount", updateDataCount);
            map.put("updateDataCountHistogram", getUpdateDataHistogram());
        }
        return map;
    }
}
