package com.alibaba.druid.hdriver.execute;

import java.io.IOException;
import java.math.BigDecimal;

import org.apache.hadoop.hbase.util.Bytes;


public class HBaseUtils {
    public static byte[] toBytes(Object value) throws IOException {
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
