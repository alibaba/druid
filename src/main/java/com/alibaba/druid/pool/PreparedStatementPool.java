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

import java.util.HashMap;
import java.util.Map;

import com.alibaba.druid.pool.PoolablePreparedStatement.PreparedStatementKey;
import com.alibaba.druid.util.LRUCache;

/**
 * @author wenshao<szujobs@hotmail.com>
 */
public class PreparedStatementPool {

    private final Map<PreparedStatementKey, PreparedStatementHolder> map;

    public PreparedStatementPool(int maxSize){
        if (maxSize < 0) {
            map = new HashMap<PreparedStatementKey, PreparedStatementHolder>(maxSize);
        } else {
            map = new LRUCache<PreparedStatementKey, PreparedStatementHolder>(maxSize);
        }
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
        map.put(key, holder);
    }

    public Map<PreparedStatementKey, PreparedStatementHolder> getMap() {
        return map;
    }

}
