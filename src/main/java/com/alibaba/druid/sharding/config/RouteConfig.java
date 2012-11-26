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

import java.util.HashMap;
import java.util.Map;

public class RouteConfig {

    private String                   defaultDatabase;
    private Map<String, LogicTable>  logicTables  = new HashMap<String, LogicTable>();
    private Map<String, MappingRule> mappingRules = new HashMap<String, MappingRule>();

    public String getDefaultDatabase() {
        return defaultDatabase;
    }

    public void setDefaultDatabase(String defaultDatabase) {
        this.defaultDatabase = defaultDatabase;
    }

    public Map<String, LogicTable> getLogicTables() {
        return logicTables;
    }

    public void setLogicTables(Map<String, LogicTable> logicTables) {
        this.logicTables = logicTables;
    }

    public Map<String, MappingRule> getMappingRules() {
        return mappingRules;
    }

    public void setMappingRules(Map<String, MappingRule> mappingRules) {
        this.mappingRules = mappingRules;
    }

    public MappingRule getMappingRule(String table) {
        if (table == null) {
            return null;
        }

        String lowerTable = table.toLowerCase();

        return mappingRules.get(lowerTable);
    }

    public TablePartition getPartition(String table, String partitionName) {
        if (table == null) {
            throw new IllegalArgumentException("table is null");
        }
        if (partitionName == null) {
            throw new IllegalArgumentException("partitionName is null");
        }

        String lowerTable = table.toLowerCase();
        LogicTable config = logicTables.get(lowerTable);
        if (config == null) {
            throw new IllegalArgumentException("table's partition config not exists, tableName : " + table);
        }

        String lowerPartitionName = partitionName.toLowerCase();
        TablePartition tablePartition = config.getPartitions().get(lowerPartitionName);

        if (tablePartition == null) {
            throw new IllegalArgumentException("table's partition config not exists, tableName : " + table
                                               + ", partition : " + lowerPartitionName);
        }

        return tablePartition;
    }
}
