package com.alibaba.druid.hdriver.impl.mapping;

import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.util.Bytes;

public class HMappingDefaultImpl implements HMapping {

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

    @Override
    public Object getObject(Result result, String columnName) {
        byte[] family = this.getFamily(columnName);

        byte[] bytes;
        if (isRow(columnName)) {
            bytes = getRow(result, columnName);
        } else {
            byte[] qualifier = Bytes.toBytes(columnName);
            bytes = result.getValue(family, qualifier);
        }

        return bytes;
    }

    public byte[] getFamily(String columnName) {
        return family;
    }

    public byte[] getRow(Result result, String columnName) {
        return result.getRow();
    }

    @Override
    public byte[] getQualifier(String columnName) {
        return Bytes.toBytes(columnName);
    }

    @Override
    public boolean isRow(String columnName) {
        if (rowColumn.equals(columnName)) {
            return true;
        }

        return false;
    }
}
