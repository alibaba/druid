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

import static com.alibaba.druid.util.JdbcSqlStatUtils.get;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLongFieldUpdater;

public class WallFunctionStat {

    private volatile long                                 invokeCount;
    final static AtomicLongFieldUpdater<WallFunctionStat> invokeCountUpdater = AtomicLongFieldUpdater.newUpdater(WallFunctionStat.class,
                                                                                                                 "invokeCount");

    public long getInvokeCount() {
        return invokeCount;
    }

    public void incrementInvokeCount() {
        invokeCountUpdater.incrementAndGet(this);
    }

    public void addSqlFunctionStat(WallSqlFunctionStat sqlFunctionStat) {
        this.invokeCount += sqlFunctionStat.getInvokeCount();
    }

    public String toString() {
        return "{\"invokeCount\":" + invokeCount + "}";
    }

    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("invokeCount", invokeCount);
        return map;
    }

    public WallFunctionStatValue getStatValue(boolean reset) {
        WallFunctionStatValue statValue = new WallFunctionStatValue();
        statValue.setInvokeCount(get(this, invokeCountUpdater, reset));
        return statValue;
    }
}
