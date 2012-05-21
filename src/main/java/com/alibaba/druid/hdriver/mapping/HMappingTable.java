package com.alibaba.druid.hdriver.mapping;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public class HMappingTable {

    private String                      tableName;

    private Map<String, HMappingColumn> columns = new LinkedHashMap<String, HMappingColumn>();
    private HMappingColumn              keyColumn;

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public Collection<HMappingColumn> getColumns() {
        return Collections.unmodifiableCollection(columns.values());
    }

    public void addColumn(HMappingColumn column) {
        this.columns.put(column.getName(), column);
    }

    public HMappingColumn getColumn(String columnName) {
        return columns.get(columnName);
    }

    public HMappingColumn getKeyColumn() {
        return keyColumn;
    }

    public void setKeyColumn(HMappingColumn keyColumn) {
        keyColumn.setKey(true);
        this.keyColumn = keyColumn;
    }

}
