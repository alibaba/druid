package com.alibaba.druid.hbase.mapping;

import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.util.Bytes;

public class HMappingDefaultImpl implements HMapping {

    private byte[] family = Bytes.toBytes("d");

    @Override
    public Object getObject(Result result, String columnName) {
        byte[] family = this.getFamily(columnName);

        byte[] bytes;
        if (isRow(columnName)) {
            bytes = result.getRow();
        } else {
            byte[] qualifier = Bytes.toBytes(columnName);
            bytes = result.getValue(family, qualifier);
        }

        return bytes;
    }

    public byte[] getFamily(String columnName) {
        return family;
    }

    @Override
    public byte[] getQualifier(String columnName) {
        return Bytes.toBytes(columnName);
    }

    @Override
    public boolean isRow(String columnName) {
        if ("id".equals(columnName)) {
            return true;
        }

        return false;
    }
}
