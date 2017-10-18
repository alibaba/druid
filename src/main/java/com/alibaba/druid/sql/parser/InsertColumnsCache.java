/*
 * Copyright 1999-2017 Alibaba Group Holding Ltd.
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
package com.alibaba.druid.sql.parser;

import com.alibaba.druid.sql.ast.SQLExpr;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class InsertColumnsCache {
    public static InsertColumnsCache global;

    static {
        global = new InsertColumnsCache(8192);
    }

    public ConcurrentMap<Long, Entry> cache = new ConcurrentHashMap<Long, Entry>();

    private final Entry[]   buckets;
    private final int       indexMask;

    public InsertColumnsCache(int tableSize){
        this.indexMask = tableSize - 1;
        this.buckets = new Entry[tableSize];
    }

    public final Entry get(long hashCode64) {
        final int bucket = ((int) hashCode64) & indexMask;
        for (Entry entry = buckets[bucket]; entry != null; entry = entry.next) {
            if (hashCode64 == entry.hashCode64) {
                return entry;
            }
        }

        return null;
    }

    public boolean put(long hashCode64, String columnsString, List<SQLExpr> columns) {
        final int bucket = ((int) hashCode64) & indexMask;

        for (Entry entry = buckets[bucket]; entry != null; entry = entry.next) {
            if (hashCode64 == entry.hashCode64) {
                return true;
            }
        }

        Entry entry = new Entry(hashCode64, columnsString, columns, buckets[bucket]);
        buckets[bucket] = entry;  // 并发是处理时会可能导致缓存丢失，但不影响正确性

        return false;
    }

    public final static class Entry {
        public final long hashCode64;
        public final String columnsString;
        public final List<SQLExpr> columns;
        public final Entry next;

        public Entry(long hashCode64, String columnsString, List<SQLExpr> columns, Entry next) {
            this.hashCode64 = hashCode64;
            this.columnsString = columnsString;
            this.columns = columns;
            this.next = next;
        }
    }
}
