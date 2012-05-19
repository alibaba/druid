package com.alibaba.druid.hbase.mapping;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public class HMapping {

    private Map<String, HMappingColumn> columns = new LinkedHashMap<String, HMappingColumn>();

    public Collection<HMappingColumn> getColumns() {
        return Collections.unmodifiableCollection(columns.values());
    }

    public void addColumn(HMappingColumn column) {
        this.columns.put(column.getName(), column);
    }
    
    public HMappingColumn getColumn(String columnName) {
        return columns.get(columnName);
    }
}
