package com.alibaba.druid.hdriver.impl.mapping;

import java.io.IOException;
import java.math.BigDecimal;

import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.util.Bytes;

public abstract class HMappingAdapter implements HMapping {

    @Override
    public byte[] getQualifier(String columnName) {
        return Bytes.toBytes(columnName);
    }

    @Override
    public byte[] getRow(Result result, String columnName) {
        return result.getRow();
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

    @Override
    public byte[] toBytes(String columnName, Object value) throws IOException {
        if (value == null) {
            return null;
        }

        byte[] bytes;
        if (value instanceof String) {
            String strValue = (String) value;
            bytes = Bytes.toBytes(strValue);
        } else if (value instanceof Integer) {
            int intValue = ((Integer) value).intValue();
            bytes = Bytes.toBytes(intValue);
        } else if (value instanceof Long) {
            long longValue = ((Long) value).longValue();
            bytes = Bytes.toBytes(longValue);
        } else if (value instanceof Boolean) {
            boolean booleanValue = ((Boolean) value).booleanValue();
            bytes = Bytes.toBytes(booleanValue);
        } else if (value instanceof BigDecimal) {
            BigDecimal decimalValue = (BigDecimal) value;
            bytes = Bytes.toBytes(decimalValue);
        } else {
            throw new IOException("TODO"); // TODO
        }

        return bytes;
    }
}
