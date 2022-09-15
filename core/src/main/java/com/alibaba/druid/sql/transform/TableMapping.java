package com.alibaba.druid.sql.transform;

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.util.FnvHash;

import java.util.HashMap;
import java.util.Map;

public class TableMapping {
    private final String srcTable;
    private final String destTable;
    private final long srcTableHash;

    private final Map<Long, ColumnMapping> columnMappings = new HashMap<Long, ColumnMapping>();

    public TableMapping(String srcTable, String destTable) {
        this.srcTable = SQLUtils.normalize(srcTable);
        this.destTable = SQLUtils.normalize(destTable);
        this.srcTableHash = FnvHash.hashCode64(srcTable);
    }

    public String getSrcTable() {
        return srcTable;
    }

    public String getDestTable() {
        return destTable;
    }

    public long getSrcTableHash() {
        return srcTableHash;
    }

    public String getMappingColumn(String srcColumn) {
        long hash = FnvHash.hashCode64(srcColumn);
        ColumnMapping columnMapping = columnMappings.get(hash);
        if (columnMapping == null) {
            return null;
        }
        return columnMapping.destDestColumn;
    }

    public void addColumnMapping(String srcColumn, String destColumn) {
        ColumnMapping columnMapping = new ColumnMapping(srcColumn, destColumn);
        columnMappings.put(columnMapping.srcColumnHash, columnMapping);
    }

    private static class ColumnMapping {
        public final String srcColumn;
        public final String destDestColumn;
        public final long srcColumnHash;

        public ColumnMapping(String srcColumn, String destDestColumn) {
            this.srcColumn = SQLUtils.normalize(srcColumn);
            this.destDestColumn = SQLUtils.normalize(destDestColumn);
            this.srcColumnHash = FnvHash.hashCode64(srcColumn);
        }
    }
}
