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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.alibaba.druid.pool.PoolablePreparedStatement.PreparedStatementKey;

/**
 * @author wenshao<szujobs@hotmail.com>
 */
public class PreparedStatementPool {

    private HashMap<PreparedStatementKey, List<PoolablePreparedStatement>> map = new HashMap<PreparedStatementKey, List<PoolablePreparedStatement>>();

    public static enum MethodType {
        M1, M2, M3, M4, M5, M6, Precall_1, Precall_2, Precall_3
    }

    public PoolablePreparedStatement get(PreparedStatementKey key) {
        List<PoolablePreparedStatement> list = map.get(key);

        if (list == null) {
            list = new ArrayList<PoolablePreparedStatement>();
            map.put(key, list);

            return null;
        }

        int size = list.size();

        if (size == 0) {
            return null;
        }

        PoolablePreparedStatement last = list.remove(size - 1);

        return last;
    }

    public void put(PoolablePreparedStatement poolableStatement) {
        PreparedStatementKey key = poolableStatement.getKey();
        List<PoolablePreparedStatement> list = map.get(key);

        if (list == null) {
            list = new ArrayList<PoolablePreparedStatement>();
            map.put(key, list);
        }

        list.add(poolableStatement);
    }

    public HashMap<PreparedStatementKey, List<PoolablePreparedStatement>> getMap() {
        return map;
    }

}
