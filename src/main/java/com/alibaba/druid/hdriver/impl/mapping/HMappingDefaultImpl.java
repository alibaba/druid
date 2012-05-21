package com.alibaba.druid.hdriver.impl.mapping;

import org.apache.hadoop.hbase.util.Bytes;

public class HMappingDefaultImpl extends HMappingAdapter implements HMapping {

    private byte[] family    = Bytes.toBytes("d");
    private String rowColumn = "id";

    public byte[] getFamily() {
        return family;
    }

    public void setFamily(byte[] family) {
        this.family = family;
    }

    public String getRowColumn() {
        return rowColumn;
    }

    public void setRowColumn(String rowColumn) {
        this.rowColumn = rowColumn;
    }

    public byte[] getFamily(String columnName) {
        return family;
    }

    @Override
    public boolean isRow(String columnName) {
        if (rowColumn.equals(columnName)) {
            return true;
        }

        return false;
    }

}
