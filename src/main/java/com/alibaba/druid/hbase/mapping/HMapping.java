package com.alibaba.druid.hbase.mapping;

import org.apache.hadoop.hbase.client.Result;

public interface HMapping {

    byte[] getFamily(String columnName);

    byte[] getQualifier(String columnName);
    
    byte[] getRow(Result result, String columnName);
    
    boolean isRow(String columnName);

    Object getObject(Result result, String name);
}
