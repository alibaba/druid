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

import java.sql.PreparedStatement;
import java.util.HashMap;

import com.alibaba.druid.pool.PoolablePreparedStatement.PreparedStatementKey;

/**
 * @author wenshao<szujobs@hotmail.com>
 */
public class PreparedStatementPool {

    private HashMap<PreparedStatementKey, PreparedStatement> map = new HashMap<PreparedStatementKey, PreparedStatement>();

    public static enum MethodType {
        M1, M2, M3, M4, M5, M6, Precall_1, Precall_2, Precall_3
    }

    public PreparedStatement get(PreparedStatementKey key) {
        return map.remove(key);
    }

    public void put(PoolablePreparedStatement poolableStatement) {
        PreparedStatementKey key = poolableStatement.getKey();
        map.put(key, poolableStatement.getRawPreparedStatement());
    }

    public HashMap<PreparedStatementKey, PreparedStatement> getMap() {
        return map;
    }

}
