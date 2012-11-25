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
package com.alibaba.druid.sharding.config;

import java.util.ArrayList;
import java.util.List;

public class MappingRuleAdapter implements MappingRule {

    private String                 defaultPartition;

    private String                 table;
    private String                 column;

    private List<MappingRuleEntry> entries = new ArrayList<MappingRuleEntry>();

    public List<MappingRuleEntry> getEntries() {
        return entries;
    }

    public void setEntries(List<MappingRuleEntry> entries) {
        this.entries = entries;
    }

    public String getPartition(Object value) {
        for (MappingRuleEntry entry : this.entries) {
            if (entry.match(value)) {
                return entry.getPartition();
            }
        }

        return this.getDefaultPartition();
    }

    public String getTable() {
        return table;
    }

    public void setTable(String table) {
        this.table = table;
    }

    public String getColumn() {
        return column;
    }

    public void setColumn(String column) {
        this.column = column;
    }

    public String getDefaultPartition() {
        return defaultPartition;
    }

    public void setDefaultPartition(String defaultPartition) {
        this.defaultPartition = defaultPartition;
    }
}
