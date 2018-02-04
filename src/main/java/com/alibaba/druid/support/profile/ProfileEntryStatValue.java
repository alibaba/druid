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
package com.alibaba.druid.support.profile;

import java.util.LinkedHashMap;
import java.util.Map;

public class ProfileEntryStatValue {

    private String parentName;
    private String name;
    private String type;
    private long   executeCount     = 0;
    private long   executeTimeNanos = 0;

    public String getParentName() {
        return parentName;
    }

    public void setParentName(String parentName) {
        this.parentName = parentName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public long getExecuteCount() {
        return executeCount;
    }

    public void setExecuteCount(long executeCount) {
        this.executeCount = executeCount;
    }

    public long getExecuteTimeNanos() {
        return executeTimeNanos;
    }

    public void setExecuteTimeNanos(long executeTimeNanos) {
        this.executeTimeNanos = executeTimeNanos;
    }

    public Map<String, Object> getData() {
        Map<String, Object> entryData = new LinkedHashMap<String, Object>();

        entryData.put("Name", this.getName());
        entryData.put("Parent", this.getParentName());
        entryData.put("Type", this.getType());
        entryData.put("ExecuteCount", this.getExecuteCount());
        entryData.put("ExecuteTimeMillis", this.getExecuteTimeNanos() / 1000 / 1000);

        return entryData;
    }
}
