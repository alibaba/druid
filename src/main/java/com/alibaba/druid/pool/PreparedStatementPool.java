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

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.alibaba.druid.pool.PoolablePreparedStatement.PreparedStatementKey;
import com.alibaba.druid.util.JdbcUtils;

/**
 * @author wenshao<szujobs@hotmail.com>
 */
public class PreparedStatementPool {

    private final Map<PreparedStatementKey, PreparedStatementHolder> map;
    private final DruidAbstractDataSource dataSource;

    public PreparedStatementPool(ConnectionHolder holder){
        this.dataSource = holder.getDataSource();
        int initCapacity = holder.getDataSource().getMaxPoolPreparedStatementPerConnectionSize();
        if (initCapacity <= 0) {
            initCapacity = 16;
        }
        map = new LRUCache<PreparedStatementKey, PreparedStatementHolder>(initCapacity);
    }

    public static enum MethodType {
        M1, M2, M3, M4, M5, M6, Precall_1, Precall_2, Precall_3
    }

    public PreparedStatementHolder get(PreparedStatementKey key) {
        PreparedStatementHolder holder = map.remove(key);

        if (holder != null) {
            holder.incrementReusedCount();
        }

        return holder;
    }

    public void put(PreparedStatementHolder holder) {
        PreparedStatementKey key = holder.getKey();
        PreparedStatementHolder oldHolder = map.put(key, holder);
        if (oldHolder != null) {
            JdbcUtils.close(oldHolder.getStatement());
        }
    }

    public Map<PreparedStatementKey, PreparedStatementHolder> getMap() {
        return map;
    }

    public class LRUCache<K, V> extends LinkedHashMap<K, V> {

        private static final long serialVersionUID = 1L;

        public LRUCache(int maxSize){
            super(maxSize);
        }

        protected boolean removeEldestEntry(Entry<K, V> eldest) {
            return (size() > dataSource.getMaxPoolPreparedStatementPerConnectionSize());
        }
    }
}
