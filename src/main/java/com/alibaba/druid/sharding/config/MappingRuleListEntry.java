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

import com.alibaba.druid.sql.visitor.SQLEvalVisitorUtils;


public class MappingRuleListEntry implements MappingRuleEntry {
    private List<Object> values = new ArrayList<Object>();
    private String       partition;
    
    public MappingRuleListEntry() {
        
    }
    
    public MappingRuleListEntry(String partition, Object...values) {
        this.partition = partition;
        for (Object value : values) {
            this.values.add(value);
        }
    }

    public List<Object> getValues() {
        return values;
    }

    public void setValues(List<Object> values) {
        this.values = values;
    }

    public String getPartition() {
        return partition;
    }

    public void setPartition(String partition) {
        this.partition = partition;
    }

    @Override
    public boolean match(Object value) {
        for (Object item : this.values) {
            if (SQLEvalVisitorUtils.eq(item, value)) {
                return true;
            }
        }
        return false;
    }
}
