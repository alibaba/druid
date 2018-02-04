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

@MTable(name = "druid_wall_function")
public class WallFunctionStatValue {

    @MField(groupBy=true, aggregate=AggregateType.None)
    private String name;

    @MField(aggregate=AggregateType.Sum)
    private long   invokeCount;

    public WallFunctionStatValue(){

    }

    public WallFunctionStatValue(String name){
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getInvokeCount() {
        return invokeCount;
    }

    public void setInvokeCount(long invokeCount) {
        this.invokeCount = invokeCount;
    }

    public Map<String, Object> toMap() {
        Map<String, Object> map = new LinkedHashMap<String, Object>(2);
        map.put("name", name);
        map.put("invokeCount", invokeCount);
        return map;
    }
}
